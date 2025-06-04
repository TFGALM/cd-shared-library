package generator

import model.CodeVariables


class ValuesYamlGenerator {
  
  private def script
  private CodeVariables codeVariables
  
  ValuesYamlGenerator(def script, CodeVariables codeVariables) {
    this.script = script
    this.codeVariables = codeVariables
  }
  
  void createFiles() {
    List<Map<String, String>> filesToWrite = [["resourceFile": "templates/values-template.yaml",
                                               "writeFile"   : "${codeVariables.getEnvironment()}/values.yaml"]]
    
    filesToWrite.each {file ->
      String content = script.libraryResource(file["resourceFile"].toString())
      
      content = content
          .replace("{{appName}}", codeVariables.getRepoName())
          .replace("{{nameSpace}}", codeVariables.getNamespace())
          .replace("{{imgName}}",
              "${script.pipelineConfig.urls.harbor}/${codeVariables.getRepoGroup()}/${codeVariables.getRepoName()}"
                  .replaceFirst("^https://", ""))
          .replace("{{imgTag}}", codeVariables.getAppVersion())
      
        content += """
  service:
    ports:
"""
      if(codeVariables.getCustomSecrets().get("PORTS")) {
        codeVariables.getCustomSecrets().get("PORTS").each {portItem ->
          content += """
      - name: ${portItem["NAME"]}
        port: ${portItem["PORT"]}
        targetPort: ${portItem["TARGET_PORT"]}
        protocol: TCP
"""
        }
      }
        content += """
  secrets:
"""
      content += """
    secretcustom:
      enabled: "true"
      name: secret-custom-${codeVariables.getRepoName()}
      data:
"""
      codeVariables.getCustomSecrets().each { key, value ->
        if (key != "PORTS" && key != "VOLUMES") {
          content += "        ${key}: \"${value}\"\n"
        }
        return
      }
      
      if (codeVariables.getCustomSecrets().get("VOLUMES")){
        content += """
  volumes:
"""
        codeVariables.getCustomSecrets().get("VOLUMES").each { volumenItem ->
          content += """
    - name: ${volumenItem["NAME"]}
"""
        if (volumenItem["MOUNT_TYPE"] == "hostPath") {
          content += """
      hostPath:
        path: ${volumenItem["PATH"]}
        type: ${volumenItem["TYPE"]}
"""
        }else if (volumenItem["MOUNT_TYPE"] == "persistentVolumeClaim") {
          content += """
      persistentVolumeClaim:
        claimName: ${volumenItem["CLAIM_NAME"]}
"""
        }
       if (volumenItem["MOUNT_PATH"]) {
          content += """
  volumeMounts:
    - name: ${volumenItem["NAME"]}
      mountPath: ${volumenItem["MOUNT_PATH"]}
"""
       }
      }
     }
      
      script.writeFile(file: file["writeFile"], text: content)
    }
  }
}
