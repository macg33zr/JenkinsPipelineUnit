package com.lesfurets.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.internal.TaskInternal
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class GitReleaseCommitTask extends DefaultTask {

    @Input
    @Optional
    String message = "Release v%s"

    @Override
    Spec<? super TaskInternal> getOnlyIf() {
        return {
            try {
                println "verify to commit"
                project.exec {
                    executable 'git'
                    args 'diff-index', '--quiet', 'HEAD', '--', "${project.buildFile.name}"
                }
            } catch (e) {
                return true
            }
            return false
        }
    }

    @TaskAction
    def action() {
        if (getOnlyIf().isSatisfiedBy(this)) {
            project.exec {
                executable 'git'
                args "commit", "-m", "${String.format(message, project.version)}", "--", "${project.buildFile.name}"
            }
        } else {
            println "Nothing to commit"
        }
    }

}
