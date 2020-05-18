def GIT_BRANCH=''

def isBuildAReplay() {
  // https://stackoverflow.com/questions/51555910/how-to-know-inside-jenkinsfile-script-that-current-build-is-an-replay/52302879#52302879
  // https://stackoverflow.com/questions/43597803/how-to-differentiate-build-triggers-in-jenkins-pipeline
  // Can also be caused by:
  // org.jenkinsci.plugins.workflow.cps.replay.ReplayCause
  return currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')
}

// Compute the custom workspace to use
def customWorkspaceCompute() {
   
   node {
      def numberPostfix = /[-_]\d$/

   // def workspaceRoot = path[0..<-1].join(File.separator)
   // def currentWs = path[-1]

   String newWorkspace = env.JOB_NAME.replace('/', '_')
   newWorkspace = newWorkspace.replace('%2f', '_')
   newWorkspace = newWorkspace.replace('%2F', '_')
   newWorkspace = newWorkspace.replace(numberPostfix, '')
   // if (currentWs =~ '@') 
   // {
   //    newWorkspace = "${newWorkspace}@${currentWs.split('@')[-1]}"
   // }

   return newWorkspace
   }
}

pipeline {
   agent any

   options {
      // [buildDiscarder, catchError, checkoutToSubdirectory, 
      // copyArtifactPermission, disableConcurrentBuilds, disableResume, durabilityHint, lock, 
      // newContainerPerStage, overrideIndexTriggers, parallelsAlwaysFailFast, preserveStashes, 
      // quietPeriod, rateLimitBuilds, retry, script, skipDefaultCheckout, skipStagesAfterUnstable, 
      // timeout, timestamps, waitUntil, warnError, withContext, withCredentials, withEnv, ws]
      skipDefaultCheckout true
      ws("testing")

      // 3 minute quiet period to see if a push has anything following it.
      quietPeriod 180
   }
   stages {
      stage('Set Params') {  
        steps {
         checkout(
               [$class: 'GitSCM', 
               branches: scm.branches, 
               extensions: scm.extensions + [
                  [$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [
                     [path: '.github']
                     ],
                     ]
               ],
               userRemoteConfigs: scm.userRemoteConfigs
               ]
            )

            sh 'env'
            script {
               echo "Triggering job for branch ${env.BRANCH_NAME}"
               GIT_BRANCH=env.BRANCH_NAME.replace("/","%2F")
            }
         }
      }
      stage('Changeset test') {
         parallel {
            stage('Changeset') {
               when { anyOf {
                  changeset "spike/**"
                  changeset "Jenkinsfile"
                  expression { env.BUILD_NUMBER == '1' || isBuildAReplay() }
                  branch 'master'
               }}
               steps {
                   echo 'cmake -B build -S spike'
               }
            }
            stage('Docker') {
               when { anyOf {
                  changeset "Dockerfile"
                  expression { env.BUILD_NUMBER == '1' || isBuildAReplay() }
                  branch 'master'
               }
               }
               steps {
                  echo 'testing'
               }
            }
            
         }
         post {
            failure {
               postFailure()
            }
         }
      }
   }
}

def postFailure() {
    script {
        if ((env.BRANCH_NAME.equals('master'))
                || (env.BRANCH_NAME.toUpperCase().startsWith('LTS'))) {
            slackSend baseUrl: 'https://luminartech.slack.com/services/hooks/jenkins-ci/',
                botUser: true, channel: '#cicd-model-i-notifications',
                message: "Build failed!\n<${env.BUILD_URL}|${env.JOB_NAME} ${env.BUILD_NUMBER}>",
                tokenCredentialId: 'stash_notification_token'
        }
    }
}
