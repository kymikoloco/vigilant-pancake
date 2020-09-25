
def call() {
  env.getEnvironment().each { k,v -> env.setProperty(k, v)  }
  env.getOverriddenEnvironment().each { k,v -> env.setProperty(k, v)  }
}
