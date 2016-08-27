def call(name, body) {
    def params = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = params
    body()

    if (params.p) {
        params << params.p
        params.remove('p')
    }

    stage params.stage.name

    try {
        for (action in params.stage.actionList) {
            params << executeAction(action) {
                p = params
            }
        }
        params.remove('stage')
        params
    }
    catch (err) {
        echo err.toString()
        throw err
    }
}
