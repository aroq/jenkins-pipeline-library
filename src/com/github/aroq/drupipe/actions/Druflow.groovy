package com.github.aroq.drupipe.actions

class Druflow extends BaseAction {

    def operations() {
        deployFlow()
        [:]
    }

    def deployFlow() {
        def executeEnvironment = action.params.executeEnvironment ? action.params.executeEnvironment : action.pipeline.context.environment
        def projectName = action.params.projectName ? action.params.projectName : action.pipeline.context.projectName
        executeDruflowCommand([env: executeEnvironment, projectName: projectName])
    }

    def deploy() {
        def site = action.params.site ? action.params.site : 'default'
        String reference
        if (action.params.reference) {
            reference = action.params.reference
        }
        else {
            if (action.pipeline.context.jenkinsParams.release) {
                reference = action.pipeline.context.jenkinsParams.release
            }
            else {
                reference = action.pipeline.context.environmentParams.git_reference
            }
        }
        executeDruflowCommand([argument: "tags/${reference}", site: site, env: action.pipeline.context.environment, projectName: action.pipeline.context.projectName])
    }

    def executeDruflowCommand(overrides = [:]) {
        def docrootConfigDir = action.params.docrootConfigDir ? action.params.docrootConfigDir : action.pipeline.drupipeConfig.drupipeSourcesController.sourceDir(action.pipeline.drupipeConfig.config, 'project')
        def defaultParams = [
            debug: debugFlag(),
            executeCommand: action.params.executeCommand,
            workspace: action.pipeline.context.workspace,
            // TODO: review this parameter handling.
            docrootDir: action.params.docrootDir ? action.params.docrootDir : action.pipeline.context.docrootDir,
            docrootConfigDir: docrootConfigDir,
        ]
        // TODO: review it.
        if (action.pipeline.context.env.operationsMode) {
            defaultParams['flowType'] = action.pipeline.context.env.operationsMode
        }
        else {
            defaultParams['flowType'] = 'full'
        }
        def commandParams = defaultParams
        commandParams << overrides

        def options = ''
        options += getOptions(commandParams)
        if (action.params.propertiesFile && script.fileExists(file: action.params.propertiesFile)) {
            options += getOptions(getProperties())
        }

        def druflowCommand = "cd ${action.params.druflowDir} && ./gradlew app ${options}"

        if (action.params.installDruflow) {
            druflowGet()
        }

        script.drupipeShell(druflowCommand, action.params)
    }

    def druflowGet() {
        if (script.fileExists(action.params.druflowDir)) {
            utils.removeDir(action.params.druflowDir, action.pipeline.context)
        }
        script.drupipeShell("git clone ${action.params.druflowRepo} --branch ${action.params.druflowGitReference} --depth 1 ${action.params.druflowDir}", action.params)
    }

    def copySite() {
        for (db in getDbs()) {
            executeDruflowCommand([argument: "'${db} ${action.params.toEnvironment}'", env: action.params.executeEnvironment, site: 'default'])
        }
    }

    def dbBackupSite() {
        for (db in getDbs()) {
            executeDruflowCommand([argument: db, env: action.params.executeEnvironment, site: 'default'])
        }
    }

    def getGitRepo() {
        def executeEnvironment = action.params.executeEnvironment ? action.params.executeEnvironment : action.pipeline.context.environment
        executeDruflowCommand([env: executeEnvironment, projectName: action.pipeline.context.projectName])
    }

    def getProperties() {
        script.readProperties(file: action.params.propertiesFile)
    }

    def getDbs() {
        def dbs = []
        if (action.params.db instanceof java.lang.String) {
            dbs << action.params.db
        }
        else {
            dbs = action.params.db
        }
        dbs
    }

    @NonCPS
    def getOptions(props) {
        def result = ''
        for (prop in props) {
            result += " -D${prop.key}=${prop.value}"
        }
        result
    }

    def debugFlag() {
        action.pipeline.context.debugEnabled && action.pipeline.context.debugEnabled != '0' ? '1' : '0'
    }
}
