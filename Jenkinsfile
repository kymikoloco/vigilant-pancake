@Library('JenkinsLib') _

def builtImage;
pipeline{
    agent { label 'model_i' }
    options {parallelsAlwaysFailFast() }
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
                env.THIS_IS_A_TEST = "test_env_set"
                ANOTHER_TEST = "not_env_set"
                QUICK_BUILD = "not_actually_modifying_params"
                env.GIT_COMMIT = env.GIT_COMMIT
                makeEnvAvailable()

                docker.withRegistry("https://index.docker.io/", "dockerhub") {
                    // Build the image. It's probably already built, but just check
                    //builtImage = docker.build('cmake_image', "-f ./Dockerfile ." )
                    builtImage = docker.image("cmake_image")

                    // Also tag with the git commit
                    builtImage.tag("${env.GIT_COMMIT}")
                }

            } }
        }

        stage ("cmake") {
            steps {
                script {
                    withEnv(["FOO=newbar"]) {
                        echo currentBuild.getBuildVariables()
                    }
                }
                // Run in a custom docker
                catchError(buildResult: 'SUCCESS', message: 'Unstable', stageResult: 'UNSTABLE' ) {
                    sleep 2
                    bat script: "docker raun --rm --env-file test-vals.env -v ${env.WORKSPACE}:/src --workdir /src ${builtImage.id} /bin/bash -c \"/opt/cmake/bin/cmake -Bbuild -S spike && /opt/cmake/bin/cmake --build build\"", label: 'Run `make` in custom mounted folder'
                }
            }
        }
    }
}
