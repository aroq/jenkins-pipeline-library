package com.github.aroq.drupipe.actions

import com.github.aroq.drupipe.DrupipeActionWrapper

class Kubectl extends BaseAction {

    def script

    def utils

    DrupipeActionWrapper action

    def scale_replicaset() {
        executeKubectlCommand()
    }

    def scale_down_up() {
        def name = action.pipeline.executeAction(action: 'Kubectl.get_replicaset_name', params: [debugEnabled: true]).stdout
        script.echo "Replicaset name: ${name}"
        action.pipeline.executeAction(action: 'Kubectl.scale_replicaset', params: [name: name, replicas: action.params.replicas_down])
        action.pipeline.executeAction(action: 'Kubectl.scale_replicaset', params: [name: name, replicas: action.params.replicas_up])
    }

    def get_pod_name() {
        executeKubectlCommand()
    }

    def get_pod_logs() {
        executeKubectlCommand()
    }

    def get_loadbalancer_address() {
        [
            url: executeKubectlCommand().stdout,
        ]
    }

    def get_replicaset_name() {
        executeKubectlCommand()
    }

    def get_pods() {
        executeKubectlCommand()
    }

    def copy_from_pod() {
        executeKubectlCommand()
        script.drupipeShell("ls -al", action.params)
    }

    def executeKubectlCommand() {
        script.drupipeShell("${action.params.full_command.join(' ')}", action.params)
    }

}

