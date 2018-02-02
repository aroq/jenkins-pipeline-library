package com.github.aroq.drupipe

class DrupipeJob extends DrupipeBase {

    String name

    String type

    String from

    String branch

    String state

    String env

    String configRepo

    HashMap source

    def pattern

    def triggers

    def trigger

    def notify

    def webhooks

    def context

    HashMap params

    DrupipePipeline pipeline

    DrupipeController controller

    def execute(body = null) {
        controller.drupipeLogger.trace "DrupipeJob execute - ${name}"
        pipeline.controller = controller
        pipeline.execute()
    }

}
