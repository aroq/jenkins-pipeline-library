package com.github.aroq.drupipe.processors

class DrupipeParamProcessor implements Serializable {

    com.github.aroq.drupipe.Utils utils

    @NonCPS
    def interpolateCommand(String command, action, context, debugFlag = 'false') {
        def prepareFlags = { flags ->
            prepareFlags(flags)
        }

        if (debugFlag) {
//            utils.echoMessage(command)
        }

        def binding = [context: context, actions: context.actions ? context.actions : [:], action: action, prepareFlags: prepareFlags]
        def engine = new groovy.text.SimpleTemplateEngine()
        def template = engine.createTemplate(command).make(binding)
        template.toString()
    }

//    @NonCPS
    def processActionParams(action, context, ArrayList prefixes, ArrayList path = [], String mode = 'pre_process') {
        def params
        if (path) {
            params = path.inject(action.params, { obj, prop ->
                if (obj && obj[prop]) {
                    obj[prop]
                }
            })
        }
        else {
            params = action.params
        }

        for (param in params) {
            // TODO: Refactor it.
            def processParamFlag = true
            if (params.containsKey('params_processing') && params.params_processing.containsKey(param.key)) {
                if (!params.params_processing[param.key].contains(mode)) {
                    utils.echo "Disable param processing: ${param.key}"
                    processParamFlag = false
                }
            }
            if (mode != 'pre_process') {
                if (!(params.containsKey('params_processing') && params.params_processing.containsKey(param.key))) {
                    processParamFlag = false
                }
            }
            if (processParamFlag) {
                if (param.value instanceof CharSequence) {
                    param.value = overrideWithEnvVarPrefixes(params[param.key], context, prefixes.collect {
                        [it, param.key.toUpperCase()].join('_')
                    })
                    param.value = interpolateCommand(param.value, action, context)
                } else if (param.value instanceof Map) {
                    processActionParams(action, context, prefixes.collect {
                        [it, param.key.toUpperCase()].join('_')
                    }, path + param.key, mode)
                } else if (param.value instanceof List) {
                    for (def i = 0; i < param.value.size(); i++) {
                        param.value[i] = interpolateCommand(param.value[i], action, context)
                    }
                }
            }
        }
    }

    @NonCPS
    def overrideWithEnvVarPrefixes(param, context, prefixes) {
        def result = param
        prefixes.each {
            overrideWithEnvVar(param, context, it)
        }

        result
    }

    def overrideWithEnvVar(param, context, String envVarName) {
        def result = param
        if (context.env && context.env?.containsKey(envVarName)) {
            result = context.env[envVarName]
        }
        result
    }

    @NonCPS
    def prepareFlags(flags) {
        flags.collect { k, v ->
            v.collect { subItem ->
                "${k} ${subItem}".trim()
            }.join(' ').trim()
        }.join(' ')
    }

}
