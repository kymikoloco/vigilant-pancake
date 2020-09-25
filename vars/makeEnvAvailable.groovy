
def call(env) {
  env.getEnvironment().each { k,v -> env.setProperty(k, v)  }
  // Possible git env vars not available through getEnvironment for some reason
  ['GIT_COMMIT', 'GIT_PREVIOUS_COMMIT', 'GIT_PREVIOUS_SUCCESSFUL_COMMIT', 'GIT_BRANCH', 'GIT_LOCAL_BRANCH', 'GIT_CHECKOUT_DIR', 'GIT_URL', 'GIT_COMMITTER_NAME', 'GIT_AUTHOR_NAME', 'GIT_COMMITTER_EMAIL', 'GIT_AUTHOR_EMAIL'].each {name ->
    if( env.getProperty(name) != null ) {
      env.setProperty(name, env.getProperty(name) )
    }
  }
}
