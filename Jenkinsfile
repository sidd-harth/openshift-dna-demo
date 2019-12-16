def mvnHome="mvn -s nexusconfigurations/nexus.xml"

pipeline {
 agent any
 tools {
  maven 'M3'
 // jdk 'JDK'
 // nodejs 'NODEJS'
 } 

 stages {
  stage('Check Parameters') {
   steps {
    echo "Production App Name - ${PROD_NAME}"
    echo "Application Name - ${APP_NAME}"
    echo "Development App Name - ${DEV_NAME}"
    echo "Master Host - ${MASTER_URL}"
    echo "Number of Replicas - ${SCALE_APP}"
   }
  }

  
  // Using Maven build the war file
  // Do not run tests in this step 
  stage('Build Artifact') {
   steps {
    sh "${mvnHome} clean install -DskipTests=true"

    archive 'target/*.jar'
   }
  }

  // Using Maven run the unit tests
  stage('Unit Tests') {
   steps {
     sh "${mvnHome} test"
   }
  }

  // Using Maven call SonarQube for Code Analysis
  stage('Sonar Code Analysis') {
   steps {
    // bat "${mvn} sonar:sonar -Dsonar.host.url=http://localhost:9000   -Dsonar.login=aab02659e091858dfd99ddace56d44c604390a52"
// office login     
   // bat "${mvn} sonar:sonar -Dsonar.host.url=http://localhost:9000   -Dsonar.login=410469ab34867377f5f95d2c7e8a9a4d9339fbb2"
   //openshift sonar
   sh "${mvnHome} sonar:sonar -Dsonar.host.url=http://sonarqube:9000   -Dsonar.login=1383b6bf1d677e4bc4d56740c59c7b05132cabcd"
    }
  }

  // Publish the latest war file to Nexus. This needs to go into <nexusurl>/repository/releases.
/*  stage('Publish to Nexus Repository') {
   steps {
    sh "${mvnHome} deploy -DskipTests=true"
   }
  }  */

stage('Deploy on Openshift?') {
   steps {
    timeout(time: 2, unit: 'DAYS') {
     input message: 'Do you want to Approve?'
    }
   }
  } 
  
  
  stage('Openshift New Build') {
   steps {
    sh "oc login ${MASTER_URL} --token=${OAUTH_TOKEN} --insecure-skip-tls-verify"
    sh "oc project ${DEV_NAME}"
    sh "oc delete all -l app=${APP_NAME}"
    sh "oc new-build --name=${APP_NAME} redhat-openjdk18-openshift --binary=true -l app=${APP_NAME}"

   }
  }

  
  stage('Openshift Start Build') {
   steps {
    sh "rm -rf oc-build && mkdir -p oc-build/deployments"
    //bat "rmdir oc-build && mkdir oc-build && cd oc-build && mkdir deployments"
    sh "cp target/openshift-jenkins-0.0.1-SNAPSHOT.jar oc-build/deployments/ROOT.jar"
    sh "oc start-build ${APP_NAME}  --from-dir=oc-build --wait=true  --follow"
   }
  }

 
  
  
  stage('Deploy in Development') {
   steps {
    //sh "oc new-app -f '<(curl https://raw.githubusercontent.com/sidd-harth/openshift-dna-demo/master/template-dev.yaml)'"
    sh "oc new-app -f $WORKSPACE/template-dev.yaml"
    sh "oc expose svc/${APP_NAME}"
    sh "sleep 30s"
   }
  }
  
  /*
  stage('Integration Tests') {
   steps {
    parallel(
     "Status Code": {
      sh "curl -I -s -L http://${APP_NAME}-${DEV_NAME}.35.244.32.156.nip.io/api/info | grep 200"
     },
     "Content String": {
      sh "curl -s http://${APP_NAME}-${DEV_NAME}.35.244.32.156.nip.io/api/info | grep 'Service UP and RUNNING'"
     }
    )
   }
  } */
   
   stage('Promote to Production?') {
     steps {
      timeout(time: 2, unit: 'DAYS') {
       input message: "Promote to Production?", ok: "Promote"
      }
     }
    }
  
  stage('Deploy in Production') {
   steps {
    sh "oc project ${PROD_NAME}"
    sh "oc delete all -l app=${APP_NAME}"
    sh "oc new-app -f $WORKSPACE/template-prod.yaml"
    sh "oc tag ${DEV_NAME}/${APP_NAME}:latest ${PROD_NAME}/${APP_NAME}:prod"
    sh "oc expose svc/${APP_NAME} -n ${PROD_NAME}"
   }
  }

  stage('Scaling Application') {
   steps {
    sh "oc scale --replicas=${SCALE_APP} dc ${APP_NAME} -n ${PROD_NAME}"
   }
  }
 }
}
