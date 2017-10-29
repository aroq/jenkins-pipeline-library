package com.github.aroq.drupipe.actions

import com.github.aroq.drupipe.DrupipeActionConroller

class Nexus extends BaseAction {

    def context

    def script

    def utils

    def DrupipeActionConroller action

    def uploadArtifact() {
        def builder = context.builder
        def version = builder['version']
        def artifactId = builder['buildName']
        def fileName = builder['artifactFileName']
        def groupId = builder['groupId']

        if (artifactId && groupId && version && fileName) {
            // Upload artifact.
            script.nexusArtifactUploader(
                groupId: groupId,
                credentialsId: action.params.nexusCredentialsId,
                nexusUrl: action.params.nexusUrl,
                nexusVersion: action.params.nexusVersion,
                protocol: action.params.nexusProtocol,
                repository: action.params.nexusRepository,
                version: version,
                artifacts: [
                    [
                        artifactId: artifactId,
                        classifier: '',
                        file: fileName,
                        type: action.params.nexusFileType
                    ]
                ]
            )

            // Remove artifact.
            script.drupipeShell(
                "rm -f ${fileName}", action.params
            )
        }

        context
    }
}


