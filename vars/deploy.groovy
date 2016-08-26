def call(body) {
    def params = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = params
    body()

    node {
//        HashMap pipeline = [
//                'init' : ['Config.perform'],
//                'build': ['Docman.deploy', 'Docman.info'],
//                'ops'  : ['Druflow.deployFlow']
//        ]
        pipeline = jsonParse('{"init": ["Config.perform"], "build": ["Docman.deploy", "Docman.info"]}')
        echo "Pipeline class:${pipeline.getClass()}"

        for (s in pipeline) {
            echo "Class: ${s.value.getClass()}"
            String stageName = s.key
//            ArrayList actionsList = s.value
//            echo "LIST CLASS: " + ["Docman.deploy", "Docman.info"].getClass()
//            s = null

//            actionList = null
            params << executeStage(stageName) {
                p = params
                actions = ["Config.perform"]
            }
            s = null
            stageName = null
        }

//        params << executeStage('init') {
//            p = params
//            actions = ['Config.perform']
//        }
//
//        params << executeStage('build') {
//            p = params
//            actions = ['Docman.deploy', 'Docman.info']
//        }
//
//        params << executeStage('ops') {
//            p = params
//            actions = ['Druflow.deployFlow']
//        }
    }
}

@NonCPS
def jsonParse(String jsonText) {
    slurper = new groovy.json.JsonSlurper()
    json = slurper.parseText(jsonText)
    HashMap result = [:]
    for (item in json) {
//        actions = []
//        for (action in item.value) {
//            actions << action
//        }
        result[item.key] = ['Config.perform']
    }
    json = null
    slurper = null
    result
}

@NonCPS
List<List<Object>> get_map_entries(map) {
    map.collect {k, v -> [k, v]}
}
