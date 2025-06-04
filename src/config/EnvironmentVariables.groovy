package config

import model.CodeVariables


class EnvironmentVariables implements Serializable {
  
  private def script
  
  EnvironmentVariables(script) {
    this.script = script
  }
  
  CodeVariables buildCodeVariables() {
    String jobName = script.env.JOB_NAME
    String[] parts = jobName.split('/')
    String repoGroup = parts[1]
    String repoName = parts[2]
    String appVersion = script.params.applicationVersion
    String namespace = script.params.namespace
    String argoRepoUrl = "${script.pipelineConfig.urls.forgejo}/devopsdeploy/cd-${repoName}.git"
    String customSecretRepoName = "vars-${repoName}"
    String destination = "${script.params.project}-${script.params.environment}"
    String environment = script.params.environment
    
    return new CodeVariables(repoGroup, repoName, appVersion, namespace, argoRepoUrl, customSecretRepoName, destination, environment)
  }
}
