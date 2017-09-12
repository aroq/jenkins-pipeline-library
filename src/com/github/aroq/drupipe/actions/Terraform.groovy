package com.github.aroq.drupipe.actions

import com.github.aroq.drupipe.DrupipeAction

class Terraform extends BaseAction {

    def context

    def script

    def utils

    def DrupipeAction action

    String terraformExecutable = 'terraform'

    def init() {
        def sourceDir = utils.sourceDir(context, action.params.infraSourceName)

        def creds = script.string(credentialsId: 'CONSUL_ACCESS_TOKEN', variable: 'CONSUL_ACCESS_TOKEN')
        this.script.withCredentials([creds]) {
            this.script.drupipeShell("""
            cd ${sourceDir}
            ${terraformExecutable} init -input=false -backend-config="address=${this.context.env.TF_VAR_consul_address}" -backend-config="access_token=\${CONSUL_ACCESS_TOKEN}"

            """, this.context)
        }
    }

    def state() {
        script.drupipeShell("""
            /usr/bin/terraform-inventory --list > ${action.params.stateFile}
            """, context)
        this.script.stash name: 'terraform-state}', includes: "${action.params.stateFile}"
    }

    def plan() {
        def sourceDir = utils.sourceDir(context, action.params.infraSourceName)
        def creds = [script.string(credentialsId: 'CONSUL_ACCESS_TOKEN', variable: 'CONSUL_ACCESS_TOKEN'), script.string(credentialsId: 'DO_TOKEN', variable: 'DIGITALOCEAN_TOKEN')]
        script.withCredentials(creds) {
            this.script.drupipeShell("""
            cd ${sourceDir}
            ${this.terraformExecutable} plan -var-file=terraform/dev/terraform.tfvars -var-file=terraform/dev/secrets.tfvars
            """, this.context)
        }
    }

    def apply() {
        def sourceDir = utils.sourceDir(context, action.params.infraSourceName)
        def creds = [script.string(credentialsId: 'CONSUL_ACCESS_TOKEN', variable: 'CONSUL_ACCESS_TOKEN'), script.string(credentialsId: 'DO_TOKEN', variable: 'DIGITALOCEAN_TOKEN')]
        script.withCredentials(creds) {
            this.script.drupipeShell("""
            cd ${sourceDir}
            TF_VAR_consul_access_token=\$CONSUL_ACCESS_TOKEN ${this.terraformExecutable} apply -auto-approve=true -input=false -var-file=terraform/dev/terraform.tfvars -var-file=terraform/dev/secrets.tfvars
            """, this.context)
        }
    }

    def destroy() {
        def sourceDir = utils.sourceDir(context, action.params.infraSourceName)
        def creds = [script.string(credentialsId: 'CONSUL_ACCESS_TOKEN', variable: 'CONSUL_ACCESS_TOKEN'), script.string(credentialsId: 'DO_TOKEN', variable: 'DIGITALOCEAN_TOKEN')]
        script.withCredentials(creds) {
            this.script.drupipeShell("""
            cd ${sourceDir}
            ${this.terraformExecutable} destroy -force=true -approve=true -input=false -var-file=terraform/dev/terraform.tfvars -var-file=terraform/dev/secrets.tfvars
            """, this.context)
        }
    }

}
