def mvn = "mvn -s nexusconfigurations/nexus.xml"

pipeline {
 agent any
/* tools {
  maven 'M2'
  jdk 'JDK'
  nodejs 'NODEJS'
 } */

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
/*
  // Using Maven build the war file
  // Do not run tests in this step 
  stage('Build Artifact') {
   steps {
    bat "${mvn} clean install -DskipTests=true"

    archive 'target/*.jar'
   }
  }

  // Using Maven run the unit tests
  stage('Unit Tests') {
   steps {
     bat "${mvn} test"
   }
  }

  // Using Maven call SonarQube for Code Analysis
  stage('Sonar Code Analysis') {
   steps {
    // bat "${mvn} sonar:sonar -Dsonar.host.url=http://localhost:9000   -Dsonar.login=aab02659e091858dfd99ddace56d44c604390a52"
// office login     
    bat "${mvn} sonar:sonar -Dsonar.host.url=http://localhost:9000   -Dsonar.login=410469ab34867377f5f95d2c7e8a9a4d9339fbb2"
    }
  }

  // Publish the latest war file to Nexus. This needs to go into <nexusurl>/repository/releases.
  stage('Publish to Nexus Repository') {
   steps {
     bat "${mvn} deploy -DskipTests=true"
   }
  } 

stage('Deploy on Openshift?') {
   steps {
    timeout(time: 2, unit: 'DAYS') {
     input message: 'Do you want to Approve?'
    }
   }
  } */
  stage('Openshift New Build') {
   steps {
    bat "oc login ${MASTER_URL} --token=${OAUTH_TOKEN} --insecure-skip-tls-verify"
    bat "oc project ${DEV_NAME}"
    bat "oc delete all -l app=${APP_NAME}"
    bat "oc new-build --name=${APP_NAME} redhat-openjdk18-openshift --binary=true -l app=${APP_NAME}"

   }
  }

  stage('Openshift Start Build') {
   steps {
    sh "rm -rf oc-build && mkdir -p oc-build/deployments"
    //bat "rmdir oc-build && mkdir oc-build && cd oc-build && mkdir deployments"
    bat "cp target/openshift-jenkins-0.0.1-SNAPSHOT.jar oc-build/deployments/ROOT.jar"
    bat "oc start-build ${APP_NAME}  --from-dir=oc-build --wait=true  --follow"
   }
  }

  stage('Deploy in Development') {
   steps {
    //sh "oc new-app -f '<(curl https://raw.githubusercontent.com/sidd-harth/openshift-dna-demo/master/template-dev.yaml)'"
    bat "oc new-app -f $WORKSPACE/template-dev.yaml"
    bat "oc expose svc/${APP_NAME}"
    bat "sleep 30s"
   }
  }
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
  }
  /*  stage('Promote to Production?') {
     steps {
      timeout(time: 2, unit: 'DAYS') {
       input message: "Promote to Production?", ok: "Promote"
      }
     }
    }
  */
  stage('Deploy in Production') {
   steps {
    bat "oc project ${PROD_NAME}"
    bat "oc delete all -l app=${APP_NAME}"
    sh  "oc new-app -f $WORKSPACE/template-prod.yaml"
    bat "oc tag ${DEV_NAME}/${APP_NAME}:latest ${PROD_NAME}/${APP_NAME}:prod"
    bat "oc expose svc/${APP_NAME} -n ${PROD_NAME}"
   }
  }

  stage('Scaling Application') {
   steps {
    bat "oc scale --replicas=${SCALE_APP} dc ${APP_NAME} -n ${PROD_NAME}"
   }
  }
 }
}
