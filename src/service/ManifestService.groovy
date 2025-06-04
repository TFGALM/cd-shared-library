package service

import model.CodeVariables
import generator.ValuesYamlGenerator
import generator.ChartYamlGenerator
import generator.ApplicationYamlGenerator

class ManifestService {
  
  private def script
  private CodeVariables codeVariables
  private GitService git
  
  ManifestService(def script, CodeVariables codeVariables) {
    this.script = script
    this.codeVariables = codeVariables
    this.git = new GitService(script)
  }
  
  void process() {
    
    if(!isValidFolder(codeVariables.getEnvironment())) {
      script.sh("mkdir -p ${codeVariables.getEnvironment()}")
    }

    new ValuesYamlGenerator(script, codeVariables).createFiles()
    new ApplicationYamlGenerator(script,codeVariables).createFiles()
    new ChartYamlGenerator(script,codeVariables).createFiles()
  }
  
  void createDeployManifest() {
    git.gitClone("https://forgejo.alopezpa.homelab/alm/bb-cd-deploy-app.git", codeVariables)
    script.sh("""
    cd ${script.env.WORKSPACE}/custom_workspace/
    mkdir -p ${script.env.WORKSPACE}/custom_workspace/charts
    cp ${script.env.WORKSPACE}/${codeVariables.getEnvironment()}/Chart.yaml .
    cp ${script.env.WORKSPACE}/${codeVariables.getEnvironment()}/values.yaml .
    helm package ${script.env.WORKSPACE}/custom_workspace/code/bb-cd-deploy-app --destination ${script.env.WORKSPACE}/custom_workspace/charts/
    helm template . -f values.yaml > manifests.yaml
    cp manifests.yaml ${script.env.WORKSPACE}/${codeVariables.getEnvironment()}/manifests.yaml
    """)
  }
  
  private boolean isValidFolder(String path){
    def folder = script.sh(script: "ls -l | grep '^d.${path}' || true ", returnStdout: true).trim()
    return folder ? true : false
  }
}
