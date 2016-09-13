def call(body) {
    def params = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = params
    body()

    executePipeline {
        checkoutSCM = true
        pipeline = [
            'init': [
                [
                    action: 'Docman.config',
                    params: [
                        docmanConfigType: 'dir',
                    ]
                ],
            ],
            'seed': [
                [
                    action: 'JobDslSeed.perform',
                ],
            ],
        ]
        p = params
    }
}
