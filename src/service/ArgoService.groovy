package service

import model.CodeVariables
import utils.Constant


class ArgoService {
  
  private def script
  private String url
  private String projectUrl
  
  ArgoService(def script) {
    this.script = script
    this.url = "${script.pipelineConfig.urls.argocd}${script.pipelineConfig.urls.argoApiApplications}"
    this.projectUrl = "${script.pipelineConfig.urls.argocd}${script.pipelineConfig.urls.argoApiProjects}"
  }
  
  void checkAndCreateApplication(CodeVariables codeVariables) {
    
    String projectName = "${projectUrl}/${codeVariables.getDestination()}"
    if (checkArgoProject(projectName) == "null") {
      printError(Constant.errorArgoProjectNotFound)
    }
    
    String applicationName = "${codeVariables.getEnvironment()}-${codeVariables.getRepoName()}"
    if (isArgoApplicationCreated(applicationName)) {
      createArgoApplication(codeVariables)
    } else {
      syncArgoApplication(applicationName)
    }
  }
  
  private void syncArgoApplication(String applicationName) {
    def response = script.sh(script: """
        curl -s -o /dev/null -w "%{http_code}" -k -XPOST --url ${url}/${applicationName}/sync \\
        -H 'Authorization: Bearer ${script.env.ARGOCD_CREDS.toString()}' \\
        -H 'content-type: application/json' --data '{}'
""",returnStdout: true).trim()
    def statusCode = response.toInteger()
    if(statusCode != 200){
      printError(Constant.errorArgoSyncApplication)
    }
    
  }
  
  private boolean isArgoApplicationCreated(String applicationName){
    String response = script.sh(script: """
                curl -s -k -H 'Authorization: Bearer ${script.env.ARGOCD_CREDS.toString()}' \\
                  "${url}?name=${applicationName}" | jq '.items | length'
            """, returnStdout: true).trim()
    return response == "0"
  }
  
  private String checkArgoProject(String projectUrl) {
    String project = script.sh(script: """
        curl -s -k -H 'Authorization: Bearer ${script.env.ARGOCD_CREDS.toString()}' \\
          ${projectUrl} |  jq '.metadata.name'
      """, returnStdout: true).trim()
    return project
  }
  
  private void createArgoApplication(CodeVariables codeVariables){
    def response = script.sh(script: """
      curl -s -o /dev/null -w "%{http_code}" -k -XPOST --url ${url} \\
        -H 'Authorization: Bearer ${script.env.ARGOCD_CREDS.toString()}' \\
        -H 'content-type: application/json' \\
        --data '{
          "metadata": {
            "name": "${codeVariables.getEnvironment()}-${codeVariables.getRepoName()}",
            "namespace": "argocd",
            "labels": {
              "App": "${codeVariables.getRepoName()}",
              "Version": "${codeVariables.getAppVersion()}",
              "Project": "${codeVariables.getDestination()}"
            }
          },
          "spec": {
            "project": "${codeVariables.getDestination()}",
            "source": {
              "repoURL": "${codeVariables.getArgoRepoUrl()}",
              "targetRevision": "main",
              "path": "${codeVariables.getEnvironment()}"
            },
            "destination": {
              "name": "${codeVariables.getDestination()}",
              "namespace": "${codeVariables.getNamespace()}"
            },
            "syncPolicy": {
              "automated": {
                "prune": true,
                "selfHeal": true
              },
               "syncOptions": [
                  "CreateNamespace=true",
                  "Replace=true",
                  "Force=true"
               ]
            }
          }
        }'
    """, returnStdout: true).trim()
    def statusCode = response.toInteger()
    if(statusCode != 200){
      printError(Constant.errorArgoCreateApplication)
    }
  }
  
  private void printError(String msg) {
    script.currentBuild.result = 'FAILURE'
    script.error(msg)
  }
}