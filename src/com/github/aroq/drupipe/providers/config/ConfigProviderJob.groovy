package com.github.aroq.drupipe.providers.config

class ConfigProviderJob extends ConfigProviderBase {

    def provide() {
        def result = [:]
        if (drupipeConfig.config.jobs) {
            controller.archiveObjectJsonAndYaml(drupipeConfig.config, 'context_unprocessed')

            if (drupipeConfig.config.config_version > 1) {
                utils.log "Initialising drupipeProcessorsController"
                controller.drupipeProcessorsController = controller.drupipeConfig.initProcessorsController(this, drupipeConfig.config.processors)
            }

            // Performed here as needed later for job processing.
            utils.jsonDump(drupipeConfig.config, drupipeConfig.config, 'CONFIG - BEFORE processJobs', true)
            controller.drupipeConfig.process()
            utils.log "AFTER ConfigProviderJob() .DrupipeConfig.process()"

            utils.jsonDump(drupipeConfig.config, drupipeConfig.config.jobs, 'CONFIG JOBS PROCESSED - BEFORE processJobs', true)

            drupipeConfig.config.jobs = processJobs(drupipeConfig.config.jobs)

            utils.jsonDump(drupipeConfig.config, drupipeConfig.config.jobs, 'CONFIG JOBS PROCESSED - AFTER processJobs', true)

            result.job = (drupipeConfig.config.env.JOB_NAME).split('/').drop(1).inject(drupipeConfig.config, { obj, prop ->
                obj.jobs[prop]
            })

            if (result.job) {
                if (result.job.context) {
                    result = utils.merge(result, result.job.context)
                }
            }
            else {
                throw new Exception("ConfigProviderJob->provide: No job is defined.")
            }
        }
        else {
            throw new Exception("ConfigProviderJob->provide: No config.jobs are defined")
        }
        result
    }

    def processJobs(jobs, parentParams = [:]) {
        def result = jobs
        for (job in jobs) {
            // For compatibility with previous config versions.
            if (job.value.children) {
                job.value.jobs = job.value.remove('children')
            }
            if (job.value.jobs) {
                def params = job.value.clone()
                params.remove('jobs')
                job.value.jobs = processJobs(job.value.jobs, params)
            }
            if (parentParams) {
                result[job.key] = utils.merge(parentParams, job.value)
            }
            else {
                result[job.key] = job.value
            }
        }
        result
    }

}
