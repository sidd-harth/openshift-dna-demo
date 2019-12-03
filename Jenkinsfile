def mvn = "mvn -s nexusconfigurations/nexus.xml"

pipeline {
 agent any
 tools {
        maven 'M2'
        jdk 'JDK'
        nodejs 'NODEJS'
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
/*  stage('Build Artifact') {
   steps {
     bat "${mvn} clean install -DskipTests=true"

     archive 'target/*.jar'
   }
  }*/

  /*// Using Maven run the unit tests
  stage('Unit Tests') {
   steps {
     bat "${mvn} test"
   }
  }

  // Using Maven call SonarQube for Code Analysis
  stage('Sonar Code Analysis') {
   steps {
     bat "${mvn} sonar:sonar -Dsonar.host.url=http://localhost:9000   -Dsonar.login=aab02659e091858dfd99ddace56d44c604390a52"
    }
   
  }

  // Publish the latest war file to Nexus. This needs to go into <nexusurl>/repository/releases.
  stage('Publish to Nexus Repository') {
   steps {

     bat "${mvn} deploy -DskipTests=true"
    
   }
  }*/

 /* stage('Deploy on Openshift?') {
   steps {
    timeout(time: 2, unit: 'DAYS') {
     input message: 'Do you want to Approve?'
    }
   }
  }
  stage('Openshift New Build') {
   steps {
    bat "oc login ${MASTER_URL} --token=${OAUTH_TOKEN} --insecure-skip-tls-verify"
//bat "oc login -u system:admin -n ${DEV_NAME} --insecure-skip-tls-verify"
    bat "oc project ${DEV_NAME}"

  bat "oc delete all -l app=${APP_NAME}"
     // clean up. keep the imagestream
     //sh 'oc delete bc,dc,svc,route -l app=${APP_NAME} -n ${DEV_NAME}'

    // create build. override the exit code since it complains about exising imagestream
   // sh "oc new-build --name=${APP_NAME} --image-stream=redhat-openjdk18-openshift --binary=true --labels=app=${APP_NAME} -n ${DEV_NAME} || true"
	              


   bat "oc new-build --name=${APP_NAME}-${env.BUILD_ID} redhat-openjdk18-openshift --binary=true"
   }
  }

  stage('Openshift Start Build') {
   steps {
    //sh "pwd" 
    //sh " curl -O -X GET -u admin:admin123 http://localhost:8081/repository/snapshot/com/openshift/test/openshift-jenkins/0.0.1-SNAPSHOT/openshift-jenkins-0.0.1-20180214.210246-15.jar "
    sh "rm -rf oc-build && mkdir -p oc-build/deployments"
   // sh "cp ./openshift-jenkins-0.0.1-20180214.210246-15.jar oc-build/deployments/ROOT.jar"
	bat "cp target/openshift-jenkins-0.0.1-SNAPSHOT.jar oc-build/deployments/ROOT.jar"

    bat "oc start-build ${APP_NAME} --from-dir=oc-build --wait=true  --follow"
   }
  }
  stage('Deploy in Development') {
   steps {
    //sh 'oc new-app ${APP_NAME}'
    //bat "oc new-app ${APP_NAME} | jq '.items[] | select(.kind == "DeploymentConfig") | .spec.template.spec.containers[0].env += [{"name":"db_name","valueFrom":{"secretKeyRef":{"key":"database-name","name":"mysql"}}},{"name":"db_username","valueFrom":{"secretKeyRef":{"key":"database-user","name":"mysql"}}},{"name":"db_password","valueFrom":{"secretKeyRef":{"key":"database-password","name":"mysql"}}}]' | oc apply --filename -"
    // create service from github raw
    bat "oc new-app -f $WORKSPACE/template.json"
   // bat "oc create svc -f $WORKSPACE/service.json"
    bat "oc expose svc/${APP_NAME}"
    bat "sleep 60s"
   }
  }
  stage('Integration Tests') {
   steps {
    parallel(
     "Status Code": {
      // sh 'sleep 20s'
      bat "curl -I -s -L http://${APP_NAME}-${DEV_NAME}.35.244.32.156.nip.io/api/info | grep 200"
     },
     "Content String": {
      // sh 'sleep 20s'
      bat "curl -s http://${APP_NAME}-${DEV_NAME}.35.244.32.156.nip.io/api/info | grep 'Service UP and RUNNING'"
     }
    )
   }
  }
  stage('Promote to Production?') {
   steps {
    timeout(time: 2, unit: 'DAYS') {
     input message: "Promote to Production?", ok: "Promote"
    }
   }
  }*/

  stage('Deploy in Production') {
   steps {
    bat "oc project ${PROD_NAME}"
    // tag for stage
    bat "oc tag ${DEV_NAME}/${APP_NAME}:latest ${PROD_NAME}/${APP_NAME}:${env.BUILD_ID}"
     // clean up. keep the imagestream
    //bat "oc delete bc,dc,svc,route -l app=${APP_NAME} -n ${PROD_NAME}"
    
    bat "oc delete all -l app=${APP_NAME}"
     
     // deploy stage image
    
    sh """oc new-app ${PROD_NAME}/${APP_NAME}:${env.BUILD_ID} --output=json --dry-run=true | jq '.items[] | select(.kind == "DeploymentConfig") | .spec.template.spec.containers[0].env += [{"name":"db_name","valueFrom":{"secretKeyRef":{"key":"database-name","name":"mysql"}}},{"name":"db_username","valueFrom":{"secretKeyRef":{"key":"database-user","name":"mysql"}}},{"name":"db_password","valueFrom":{"secretKeyRef":{"key":"database-password","name":"mysql"}}}]' |  oc apply --filename -"""
     // create service from github raw
       // bat "oc new-app ${PROD_NAME}/${APP_NAME}:${env.BUILD_ID} -f $WORKSPACE/template.json"

    //bat "oc expose svc/${APP_NAME} -n ${PROD_NAME}"
   }
  }

  stage('Scaling Application') {
   steps {
    bat "oc scale --replicas=${SCALE_APP} dc ${APP_NAME} -n ${PROD_NAME}"
   }
  }

 }
}