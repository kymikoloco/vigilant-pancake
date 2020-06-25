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
   
   def newWorkspace = env.JOB_NAME
   // Replace the %2F from git branch name replacement
   newWorkspace = newWorkspace.replaceAll(~/(?i)%2f/, '_')
   // Replace any suffix of -1, _1, _2, etc
   newWorkspace = newWorkspace.replaceAll(~/[-_]\d$/, '')

   return newWorkspace ?: "customWorkspace"
}
def ccacheCheck() {
   if( env.TAG_NAME_A ) {
      return 1
   }
   return 0
}
pipeline {
   agent any
   environment {
      TAG_NAME_A = "testing"
   }

   options {
      // [buildDiscarder, catchError, checkoutToSubdirectory, 
      // copyArtifactPermission, disableConcurrentBuilds, disableResume, durabilityHint, lock, 
      // newContainerPerStage, overrideIndexTriggers, parallelsAlwaysFailFast, preserveStashes, 
      // quietPeriod, rateLimitBuilds, retry, script, skipDefaultCheckout, skipStagesAfterUnstable, 
      // timeout, timestamps, waitUntil, warnError, withContext, withCredentials, withEnv, ws]
      skipDefaultCheckout true
      ws("workspace/" + customWorkspaceCompute())

      // 3 minute quiet period to see if a push has anything following it.
      quietPeriod 180
   }
   stages {
      stage('Set Params') {  
         environment{
            CCACHE_DISABLE = ccacheCheck()
         }
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

            sh 'env | sort'
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
