def GIT_BRANCH=''

def isBuildAReplay() {
  // https://stackoverflow.com/questions/51555910/how-to-know-inside-jenkinsfile-script-that-current-build-is-an-replay/52302879#52302879
  // https://stackoverflow.com/questions/43597803/how-to-differentiate-build-triggers-in-jenkins-pipeline
  // Can also be caused by:
  // org.jenkinsci.plugins.workflow.cps.replay.ReplayCause
  return currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')
}

pipeline {
   agent any

   options {
      skipDefaultCheckout true

      // 3 minute quiet period to see if a push has anything following it.
      quietPeriod 180
   }
   stages {
      stage('Set Params') {  
        steps {
         checkout(
               [$class: 'GitSCM', 
               branches: [[name: '**']], 
               // browser: [$class: 'GithubWeb', repoUrl: 'https://github.com/kymikoloco/vigilant-pancake'], 
               doGenerateSubmoduleConfigurations: false, 
               extensions: [
                  [$class: 'CloneOption', depth: 50, honorRefspec: true, noTags: true, reference: '', shallow: true], 
                  [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false],
                  [$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [
                     [path: 'spike'], 
                     [path: '*']]
                     ]
               ], 
               submoduleCfg: [], 
               userRemoteConfigs: [[
                  // refspec: '"+refs/heads/*:refs/remotes/origin/*"', 
                  url: 'https://github.com/kymikoloco/vigilant-pancake', 
                  credentialsId: 'github_token']]]
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
                  expression { env.BUILD_NUMBER == '1' || isBuildAReplay() }
                  branch 'master'
               }}
               steps {
                   bat 'cmake -B build -S spike'
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
