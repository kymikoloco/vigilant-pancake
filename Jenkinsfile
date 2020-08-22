def TEST_VER='2020.1'
def builtImage;
@NonCPS
def printParams() {
    def _text = ""
    env.getOverriddenEnvironment().each { name, value ->  println "$name=$value" }
    env.getOverriddenEnvironment().each { name, value ->  _text += "$name=$value\n" }
    writeFile(file="test-vals.env", text=_text)
}
pipeline{
    agent { label 'model_i' }
    parameters {
        booleanParam(name: 'QUICK_BUILD', defaultValue: false,
            description: 'Skip running git clean and perform a quick build' )
    }
    environment {
        TEST_VER="${TEST_VER}"
        VIVARIUM="/tools/pancake"
        USER="cmake"
        PATH="/opt/cmake/bin:$PATH"
    }
    stages {
        stage('Clean') {
            when { not {expression { return params.QUICK_BUILD } } }
            steps {
                bat 'git clean -xffd ./'
            }
        }
        stage("Dockerfile build") {
            steps { script {
                docker.withRegistry("https://index.docker.io/", "dockerhub") {
                    // Build the image. It's probably already built, but just check
                    //builtImage = docker.build('cmake_image', "-f ./Dockerfile ." )
                    builtImage = docker.image("cmake_image")

                    // Also tag with the git commit
                    builtImage.tag("${env.GIT_COMMIT}")
                }

            } }
        }

        // Synthesis must run first
        stage('Synthesis') {
            steps {
                printParams()
                // Run in a custom docker
                bat script: "docker run --env-file test-vals.env -v ${env.WORKSPACE}:/src --workdir /src ${builtImage.id} /opt/cmake/bin/cmake -Bbuild -S spike && /opt/cmake/bin/cmake --build build", label: 'Run `make` in custom mounted folder'

            }
        }
        stage('Additional') {
            agent {
                docker {
                    image "${builtImage.id}"
                    // Reuses the node specified at the top of the pipeline
                    reuseNode true
                }
            }

            stages {
            stage('Compilation') {
                parallel {
                    stage('stuff') {
                        steps {
                           sh 'echo doing stuff'
                           sh 'env'
                        }
                    }
                    stage('stuff 2 ') {
                        steps {
                           sh 'echo doing stuff'
                           sh 'env'
                        }
                    }
                }
            }

        }}
    }
}
