package generator

import model.CodeVariables


class ChartYamlGenerator {
  
  private def script
  private CodeVariables codeVariables
  
  ChartYamlGenerator(def script, CodeVariables codeVariables) {
    this.script = script
    this.codeVariables = codeVariables
  }
  
  void createFiles() {
    List<Map<String, String>> filesToWrite = [["resourceFile": "templates/chart-template.yaml",
                                               "writeFile"   : "${codeVariables.getEnvironment()}/Chart.yaml"]]
    
    String dockerTag = codeVariables.getAppVersion()
    String chartVersion
    
    if(dockerTag.startsWith("test_")) {
      chartVersion = dockerTag.substring(5) + "-SNAPSHOT"
    } else if (dockerTag.startsWith("hotfix_")) {
      String baseAppVersion = "1.0.0"
      String timestamp = dockerTag.substring(dockerTag.length() - 19)
      String hotfixIdentifier = "HOTFIX-" + timestamp.replace("_","-")
      chartVersion = baseAppVersion + "-" + hotfixIdentifier
    } else {
      chartVersion = dockerTag
    }
    
    filesToWrite.each {file ->
      String content = script.libraryResource(file["resourceFile"].toString())
      
      content = content
          .replace("{{appName}}", codeVariables.getRepoName())
          .replace("{{appVersion}}", chartVersion)
      
      script.writeFile(file: file["writeFile"], text: content)
    }
  }
}