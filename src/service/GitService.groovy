package service

import model.CodeVariables
import utils.Constant

class GitService {
  
  private def script
  
  GitService(script) {
    this.script = script
  }
  
  void gitClone(String repoUrl , CodeVariables codeVariables) {
    setSslVerify()
    String gitLoginWithUrl =
        getLoginWithUrl(repoUrl, codeVariables.getGitUsrCred(),
            codeVariables.getGitPassCred())
    script.sh("""
      rm -rf {,.[!.],..?}*
      git clone ${gitLoginWithUrl} .
    """)
  }
  
  void pushManifest() {
    setSslVerify()
    script.sh("""
      git config --global user.email "Jenkins@alopezpa.homelab"
      git config --global user.name "Jenkins bot"
      git add .
      git commit -m 'ArgoCD Manifest added.' || true
      git push origin main
      """)
  }
  
  private String getLoginWithUrl(String repoUrl, String gitUsrCred, String gitPassCred) {
    String login = 'https://' + gitUsrCred + ':' + gitPassCred + '@'
    
    if (repoUrl.startsWith('https://')) {
      return repoUrl.replaceFirst('https://', login)
    } else {
      return login + repoUrl
    }
  }
  
  private void setSslVerify() {
    script.sh(Constant.caCertificate + """
        git config --global http.https://forgejo.alopezpa.homelab/.sslcainfo /tmp/certificate/ca-alopezpa-homelab-raiz.crt
      """)
  }
}
