package com.github.aroq.drupipe

class DrupipeJob implements Serializable {

    String name

    String type

    String from

    String branch

    def triggers

    def webhooks

    def context

    def pipeline

    DrupipePipeline drupipePipeline

    DrupipeController controller

    def init() {

    }

    def execute(body = null) {
        def script = controller.script
        script.echo "DrupipeJob execute - ${name}"
    }

}
