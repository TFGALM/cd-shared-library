package service

import model.CodeVariables
import utils.Constant


class HarborService {
  
  private def script
  
  HarborService(def script) {
    this.script = script
  }
  
  void checkHarborImageVersion(CodeVariables codeVariables) {
    
    String url = ("${script.pipelineConfig.urls.harbor}${script.pipelineConfig.urls.harborApiProjects}"
        + "${codeVariables.getRepoGroup()}/repositories/${codeVariables.getRepoName()}/artifacts/"
        + "${codeVariables.getAppVersion()}/tags")
 
    if (isValidHarborVersion(url) == 'null'){
      printError(Constant.errorAppVersionNotFound)
    }
  }
  
  private String isValidHarborVersion(String url) {
    String tag = script.sh(script: """
        curl -s -k ${url} | jq 'if type == "array" then .[].name elif type == "object" then .name else null end'
      """, returnStdout: true).trim()
    return tag
  }
  
  private void printError(String msg) {
    script.currentBuild.result = 'FAILURE'
    script.error(msg)
  }
}
