debugEnabled = false
docrootDir = 'docroot'
actionParams = [
    // TODO: add params subsections (that will be containerized inside common config).
    Config: [
    ],
    // TODO: add private (that will not go into common config) params section.
    Source_loadConfig: [
    ],
    GroovyFileConfig: [
    ],
    Docman_config: [
        docmanConfigPath: 'docroot/config',
        docmanConfigFile: 'docroot.config',
    ],
    Docman_info: [
        debugEnabled: true,
    ],
    JobDslSeed: [
        removedJobAction: 'DELETE',
        removedViewAction: 'DELETE',
        lookupStrategy: 'SEED_JOB',
        additionalClasspath: ['library/src']
    ]
]
