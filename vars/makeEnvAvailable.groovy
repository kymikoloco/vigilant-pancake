
def call(env) {
  // Ignore Jenkins/Hudson env vars
  env.getEnvironment().each { k,v -> if(!k.matches("^(JENKINS|HUDSON).*")) {env.setProperty(k, v) } }
  // Possible git env vars not available through getEnvironment for some reason
  // https://github.com/jenkinsci/git-plugin/blob/dcfff8af69b3d462d3cbc95dc39517c7875a17c9/src/main/resources/hudson/plugins/git/GitSCM/buildEnv.groovy
  ['GIT_COMMIT', 'GIT_PREVIOUS_COMMIT', 'GIT_PREVIOUS_SUCCESSFUL_COMMIT', 'GIT_BRANCH', 'GIT_LOCAL_BRANCH', 'GIT_CHECKOUT_DIR', 'GIT_URL', 'GIT_COMMITTER_NAME', 'GIT_AUTHOR_NAME', 'GIT_COMMITTER_EMAIL', 'GIT_AUTHOR_EMAIL'].each {name ->
    if( env.getProperty(name) != null ) {
      env.setProperty(name, env.getProperty(name) )
    }
  }
}
