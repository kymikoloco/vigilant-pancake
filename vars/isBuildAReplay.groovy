
def call() {
  // https://stackoverflow.com/questions/51555910/how-to-know-inside-jenkinsfile-script-that-current-build-is-an-replay/52302879#52302879
  // https://stackoverflow.com/questions/43597803/how-to-differentiate-build-triggers-in-jenkins-pipeline
  // Can also be caused by:
  // org.jenkinsci.plugins.workflow.cps.replay.ReplayCause
  return currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')
}
