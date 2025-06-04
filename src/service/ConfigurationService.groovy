package service

import model.CodeVariables


class ConfigurationService {
  
  private def script
  private GitService git
  
  ConfigurationService(script) {
    this.script = script
    this.git = new GitService(script)
  }
  
  void execute(CodeVariables codeVariables) {
    
    setGitCredentials(codeVariables)
    setCustomVariable(codeVariables)
  }
  
  private void setCustomVariable(CodeVariables codeVariables) {
    String url = ("${script.pipelineConfig.urls.forgejo}${script.pipelineConfig.urls.forgejoApiRepos}"
        + "${codeVariables.getRepoGroup()}/${codeVariables.getCustomSecretRepoName()}")
    if (getForgejoRepo(url) != "null") {
      String repoUrl = ("${script.pipelineConfig.urls.forgejo}/${codeVariables.getRepoGroup()}/"
          + "${codeVariables.getCustomSecretRepoName()}.git")
      git.gitClone(repoUrl, codeVariables)
      readCustomSecrets(codeVariables)
    }
  }
  
  private String getForgejoRepo(String url) {
    return script.sh(script: """
                curl -s -k -X 'GET' '${url}' \\
                -H 'accept: application/json' \\
                -H 'Authorization: Bearer ${script.env.FORGEJO_CREDS.toString()}' \\
                | jq 'if type == "array" then .[].name elif type == "object" then .name else null end'
      """, returnStdout: true).trim();
  }
  
  private void readCustomSecrets(CodeVariables codeVariables) {
    def customSecrets = script.readYaml(file: "${codeVariables.getEnvironment()}/vars.yml")
    if (customSecrets instanceof Map) {
      codeVariables.setCustomSecrets(customSecrets)
    }
  }
  
  private void setGitCredentials(CodeVariables codeVariables) {
    codeVariables.setGitUsrCred("${script.env.GIT_CREDS_USR}")
    codeVariables.setGitPassCred("${script.env.GIT_CREDS_PSW}")
  }
}
