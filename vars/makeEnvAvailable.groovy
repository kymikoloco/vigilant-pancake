
def call(env) {
  env.getEnvironment().each { k,v -> env.setProperty(k, v)  }
}
