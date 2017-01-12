def call(stages, config) {
    utils = new com.github.aroq.drupipe.Utils()

    try {
        utils.pipelineNotify(config)
        config << config.pipeline.executeStages(stages, config)
    }
    catch (e) {
        currentBuild.result = "FAILED"
        throw e
    }
    finally {
        utils.pipelineNotify(config, currentBuild.result)
        config
    }
}

