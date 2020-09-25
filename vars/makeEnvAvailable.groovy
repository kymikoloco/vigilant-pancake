
def call(env) {
  env.getEnvironment().each { k,v -> echo "e - ${k}: ${v}"; env.setProperty(k, v)  }
  env.getOverriddenEnvironment().each { k,v -> echo "o - ${k}: ${v}"; env.setProperty(k, v)  }
}
