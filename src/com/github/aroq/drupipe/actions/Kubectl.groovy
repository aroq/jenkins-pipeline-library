package com.github.aroq.drupipe.actions

import com.github.aroq.drupipe.DrupipeAction

class Kubectl extends BaseAction {

    def context

    def script

    def utils

    DrupipeAction action

    def scale_replicaset() {
        executeKubectlCommand()
    }

    def scale_down_up() {
        def name = script.drupipeAction([action: "Kubectl.get_replicaset_name", params: action.params], context).drupipeShellResult
        script.echo "NAME: ${name}"
        script.drupipeAction([action: "Kubectl.scale_replicaset", params: action.params << [name: name, replicas: action.params.replicas_down]], context)
        script.drupipeAction([action: "Kubectl.scale_replicaset", params: action.params << [name: name, replicas: '1']], context)
    }

    def get_secret() {
        executeKubectlCommand()
    }

    def get_pod_name() {
        executeKubectlCommand()
    }

    def get_replicaset_name() {
        executeKubectlCommand()
    }

    def create_secret() {
        try {
            drupipeAction([action: "Kubectl.get_secret", params: action.params], context)
        }
        catch (e) {
            executeKubectlCommand()
        }
    }

    def getPods() {
        executeKubectlCommand()
    }

    def executeKubectlCommand() {
        script.drupipeShell(
            "${action.params.full_command.join(' ')}",
            context,
            action.params << [shellCommandWithBashLogin: false]
        )
    }

}

