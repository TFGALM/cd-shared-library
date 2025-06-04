package model

class CodeVariables implements Serializable {
  
  private String repoGroup
  private String repoName
  private String appVersion
  private String gitUsrCred
  private String gitPassCred
  private String namespace
  private String argoRepoUrl
  private Map<String, Object> customSecrets
  private String customSecretRepoName
  private String destination
  private String environment
  
  CodeVariables(repoGroup, repoName, appVersion, namespace, argoRepoUrl, customSecretRepoName, destination, environment) {
    this.repoGroup = repoGroup
    this.repoName = repoName
    this.appVersion = appVersion
    this.namespace = namespace
    this.argoRepoUrl = argoRepoUrl
    this.customSecrets = new HashMap<String, Object>()
    this.customSecretRepoName = customSecretRepoName
    this.destination = destination
    this.environment = environment
  }
  
  String getRepoGroup() {
    return repoGroup
  }
  
  void setRepoGroup(String repoGroup) {
    this.repoGroup = repoGroup
  }
  
  String getRepoName() {
    return repoName
  }
  
  void setRepoName(String repoName) {
    this.repoName = repoName
  }
  
  String getAppVersion() {
    return appVersion
  }
  
  void setAppVersion(String appVersion) {
    this.appVersion = appVersion
  }
  
  String getGitUsrCred() {
    return gitUsrCred
  }
  
  void setGitUsrCred(String gitUsrCred) {
    this.gitUsrCred = gitUsrCred
  }
  
  String getGitPassCred() {
    return gitPassCred
  }
  
  void setGitPassCred(String gitPassCred) {
    this.gitPassCred = gitPassCred
  }
  
  String getNamespace() {
    return namespace
  }
  
  void setNamespace(String namespace) {
    this.namespace = namespace
  }
  
  String getArgoRepoUrl() {
    return argoRepoUrl
  }
  
  void setArgoRepoUrl(String argoRepoUrl) {
    this.argoRepoUrl = argoRepoUrl
  }
  
  Map<String, Object> getCustomSecrets() {
    return customSecrets
  }
  
  void setCustomSecrets(Map<String, Object> customSecrets) {
    this.customSecrets = customSecrets
  }
  
  String getCustomSecretRepoName() {
    return customSecretRepoName
  }
  
  void setCustomSecretRepoName(String customSecretRepoName) {
    this.customSecretRepoName = customSecretRepoName
  }
  
  String getDestination() {
    return destination
  }
  
  void setDestination(String destination) {
    this.destination = destination
  }
  
  String getEnvironment() {
    return environment
  }
  
  void setEnvironment(String environment) {
    this.environment = environment
  }
  
  
  
  @Override
  public String toString() {
    return "CodeVariables{" + "repoGroup='" +
        repoGroup + '\'' + ", repoName='" +
        repoName + '\'' + ", appVersion='" +
        appVersion + '\'' + ", gitUsrCred='" +
        gitUsrCred + '\'' + ", gitPassCred='" +
        gitPassCred + '\'' + ", namespace='" +
        namespace + '\'' + ", argoRepoUrl='" +
        argoRepoUrl + '\'' + ", customSecrets=" +
        customSecrets + ", customSecretRepoName='" +
        customSecretRepoName + '\'' + ", destination='" +
        destination + '\'' + ", environment='" + environment + '\'' + '}';
  }
}
