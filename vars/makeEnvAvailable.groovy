
def call() {
  env.getEnvironment().each { k,v -> env.setProperty(k, v)  }
}
