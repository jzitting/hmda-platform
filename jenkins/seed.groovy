projects = [
    [name: "hmda-platform", repo: "hmda-platform", jenkinsfilePath: "hmda/Jenkinsfile"],
    [name: "check-digit", repo: "hmda-platform", jenkinsfilePath: "check-digit/Jenkinsfile"],
    [name: "institutions-api", repo: "hmda-platform", jenkinsfilePath: "institutions-api/Jenkinsfile"],
    [name: "keycloak", repo: "hmda-platform", jenkinsfilePath: "kubernetes/keycloak/Jenkinsfile"],
    [name: "hmda-help", repo: "hmda-help", jenkinsfilePath: "Jenkinsfile"],
    [name: "hmda-pub-ui", repo: "hmda-pub-ui", jenkinsfilePath: "Jenkinsfile"],
    [name: "hmda-platform-tools", repo: "hmda-platform-tools", jenkinsfilePath: "Jenkinsfile"],
    [name: "hmda-homepage", repo: "hmda-homepage", jenkinsfilePath: "Jenkinsfile"],
]

projects.each { project ->
    multibranchPipelineJob(project.name) {
        branchSources {
            github {
                repoOwner('cfpb')
                repository(project.repo)
                scanCredentialsId('github')
            }
            factory {
                workflowBranchProjectFactory {
                    scriptPath(project.jenkinsfilePath)
                }
            }
        }
    }
}