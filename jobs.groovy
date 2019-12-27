job("LAB9-sromanchenko-main-build-job") {
    description()
    keepDependencies(false)
    parameters {
        choiceParam('BRANCH_NAME', ['sromanchenko', 'master'], 'Branch choice')
        activeChoiceParam('BUILD_TRIGGER') {
            choiceType('CHECKBOX')
            groovyScript {
                script('''return ["LAB9-sromanchenko-child1-build-job","LAB9-sromanchenko-child1-build-job","LAB9-sromanchenko-child1-build-job","LAB9-sromanchenko-child1-build-job"]''')
                fallbackScript('"fallback choice"')
            }
        }
        steps {
            downstreamParameterized {
                trigger('$BUILD_TRIGGER') {
                    block {
                        buildStepFailure('FAILURE')
                        failure('FAILURE')
                        unstable('UNSTABLE')
                    }
                    parameters {
                        predefinedProp('BRANCH_NAME', '$BRANCH_NAME')
                    }
                }
            }
        }
    }
    disabled(false)
    concurrentBuild(false)
}
for(i in 1..4) {
    job("LAB9-sromanchenko-child${i}-build-job") {

        wrappers {
            preBuildCleanup()
        }
        parameters {
            gitParam('BRANCH_NAME') {
                description('Get branch info')
                type('BRANCH')
            }
        }
        scm {
            git {
                remote {
                    github("MNT-Lab/d323dsl", "https")
                }
                branch('$BRANCH_NAME')
            }
        }
        steps {
            shell('bash script.sh > output.txt')
            shell('BRANCH_NAME="$(cut -d\'/\' -f2 <<<"$BRANCH_NAME")"; tar cvf $BRANCH_NAME\\_dsl_script.tar.gz jobs.groovy')
        }
        publishers {
            archiveArtifacts {
                pattern('output.txt, ${BRANCH_NAME}_dsl_script.tar.gz')
                allowEmpty(false)
                onlyIfSuccessful(false)
                fingerprint(false)
                defaultExcludes(true)
            }

        }
    }
}