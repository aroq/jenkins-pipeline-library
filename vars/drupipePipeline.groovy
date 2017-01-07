#!groovy

def call(pipe, body = null) {
    drupipe(pipe.params) { config ->
        node(config.nodeName) {
            if (config.drupipeDocker) {
                withDrupipeDocker(config) {
                    drupipeStages(pipe.stages, config)
                }
            }
            else {
                drupipeStages(pipe.stages, config)
            }
        }
    }
}
