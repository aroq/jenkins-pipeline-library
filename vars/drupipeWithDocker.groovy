def call(context = [:], body) {
    if (context.dockerfile) {
        image = docker.build(context.dockerfile, context.projectConfigPath)
    }
    else {
        image = docker.image(context.dockerImage)
        image.pull()
    }
    def drupipeDockerArgs = context.drupipeDockerArgs
    image.inside(drupipeDockerArgs) {
        context.workspace = pwd()
        sshagent([context.credentialsId]) {
            body(context)
        }
    }
}
