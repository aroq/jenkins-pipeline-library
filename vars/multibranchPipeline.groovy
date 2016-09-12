def call(body) {
    def params = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = params
    body()

    node {
        properties(
            [
                [
                    $class: 'ParametersDefinitionProperty',
                    parameterDefinitions: [
                        [
                            name : 'force',
                            $class: 'StringParameterDefinition',
                            defaultValue: '0',
                            description: 'Force mode',
                        ],
                        [
                            name : 'debug',
                            $class: 'StringParameterDefinition',
                            defaultValue: '0',
                            description: 'Debug mode',
                        ]
                    ]
                ]
            ]
        )
        executePipeline {
            checkoutSCM = true
            pipeline = [
                'init': [
                    [
                        action: 'Source.add',
                        params: [
                            source: [
                                name: 'root',
                                type: 'dir',
                                path: './',
                            ]
                        ]
                    ],
                    [
                        action: 'Config.perform',
                        params: [
                            configProviders: [
                                action: 'Source.loadConfig',
                                params: [
                                    sourceName: 'root',
                                    configType: 'groovy',
                                ]
                            ]
                        ]
                    ]
                ],
                'deploy': [
                [
                    action: 'deployBash.perform',
                ],
                ],
            ]
            p = params
        }
        dump(params, 'pipeline result')
        params
    }
}
