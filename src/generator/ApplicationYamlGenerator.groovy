package generator

import model.CodeVariables

class ApplicationYamlGenerator {
  
  private def script
  private CodeVariables codeVariables
  
  ApplicationYamlGenerator(def script, CodeVariables codeVariables) {
    this.script = script
    this.codeVariables = codeVariables
  }
  
  void createFiles() {
    List<Map<String, String>> filesToWrite = [["resourceFile": "templates/application-template.yaml",
                                               "writeFile"   : "${codeVariables.getEnvironment()}/application.yaml"]]
    
    filesToWrite.each { file ->
      String content = script.libraryResource(file["resourceFile"].toString())
      
      content = content
          .replace("{{appName}}", codeVariables.getRepoName())
          .replace("{{nameSpace}}", codeVariables.getNamespace())
          .replace("{{project}}", codeVariables.getRepoGroup())
          .replace("{{appVersion}}", codeVariables.getAppVersion())
          .replace("{{gitRepoUrl}}", codeVariables.getArgoRepoUrl())
          .replace("{{environment}}", codeVariables.getEnvironment())
          .replace("{{argoProject}}", codeVariables.getDestination())
          .replace("{{clusterDestination}}", codeVariables.getDestination())
      
      script.writeFile(file: file["writeFile"], text: content)
    }
  }
}