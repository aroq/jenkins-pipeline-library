import com.github.aroq.DocmanConfig

// Init params
def params = [:]
configFilePath = 'config/docroot.config'
def config

if (!System.properties.docrootConfigJsonPath) {
    BUILD_NUMBER = 'pipeline'
}

// Determine current environment.
try {
    BUILD_NUMBER
    params.environment = 'jenkins'
    config = ConfigSlurper.newInstance(params.environment).parse(readFileFromWorkspace(configFilePath))
    docrootConfigJson = readFileFromWorkspace(config.docrootConfigJsonPath)
    deployPipeline = readFileFromWorkspace(config.pipeline)
    triggerPipeline = readFileFromWorkspace(config.triggerPipeline)
}
catch (MissingPropertyException mpe) {
    params.environment = 'local'
    config = ConfigSlurper.newInstance(params.environment).parse(new File(configFilePath).text)
    docrootConfigJson = new File(System.properties.docrootConfigJsonPath).text
    deployPipeline = new File(config.pipeline).text
    triggerPipeline = new File(config.triggerPipeline).text
}

folder("${config.baseFolder}")
//folder("${config.baseFolder}/tools")

// Retrieve Docman config from json file (prepared by "docman info" command).
def docmanConfig = new DocmanConfig(docrootConfigJson: docrootConfigJson)

// Create pipeline jobs for each state defined in Docman config.
docmanConfig.states?.each { state ->
    pipelineJob("${config.baseFolder}/${state.key}") {
        concurrentBuild(false)
        logRotator(-1, 30)
        parameters {
            stringParam('executeCommand', 'deployFlow')
            stringParam('projectName', 'common')
            stringParam('environment', state.value)
            stringParam('debug', '0')
            stringParam('simulate', '0')
            stringParam('docrootDir', 'docroot')
            stringParam('config_repo', config.configRepo)
            stringParam('type', 'branch')
            stringParam('version', '')
            stringParam('force', '0')
            stringParam('skip_stage_build', '0')
            stringParam('skip_stage_operations', '0')
            stringParam('skip_stage_test', '0')
        }
        definition {
            cps {
                // See the pipeline script.
                script(deployPipeline)
                sandbox()
            }
        }
    }
}

pipelineJob("${config.baseFolder}/trigger") {
    concurrentBuild(false)
    logRotator(-1, 30)
    definition {
        cps {
            // See the pipeline script.
            script(triggerPipeline)
            sandbox()
        }
    }
}

