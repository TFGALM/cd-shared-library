import config.EnvironmentVariables
import model.CodeVariables
import service.ArgoService
import service.GitService
import service.HarborService
import service.ConfigurationService
import service.ManifestService

def call(body) {
  
  config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()
  pipelineConfig = readYaml(text: libraryResource('config/dev.yml'))
  EnvironmentVariables environmentVariables = new EnvironmentVariables(this)
  CodeVariables codeVariables = environmentVariables.buildCodeVariables()
  
  pipeline {
    agent {
      kubernetes {
        yaml "${libraryResource 'config/builder.yaml'}"
      }
    }
    options {
      buildDiscarder(logRotator(numToKeepStr: '50'))
      disableConcurrentBuilds(abortPrevious: false)
      timeout(time: 20, unit: 'MINUTES')
    }
    stages {
      stage("Check Application Version") {
        steps {
          script {
            container('builder') {
              new HarborService(this).checkHarborImageVersion(codeVariables)
            }
          }
        }
      }
      stage("Get Applications Variables") {
        environment {
          GIT_CREDS = credentials("${pipelineConfig.git.credentials.id}")
          FORGEJO_CREDS = credentials("${pipelineConfig.forgejo.credentials.id}")
        }
        steps {
          script {
            container('builder') {
              new ConfigurationService(this).execute(codeVariables)
            }
          }
        }
      }
      stage("Clone Deployment Repository") {
        environment {
          GIT_CREDS = credentials("${pipelineConfig.git.credentials.id}")
        }
        steps {
          script {
            container('builder') {
              new GitService(this).gitClone(codeVariables.getArgoRepoUrl(), codeVariables)
            }
          }
        }
      }
      stage("Create Deployment") {
        steps {
          script {
            container('builder') {
              new ManifestService(this, codeVariables).process()
            }
          }
        }
      }
      stage("Create deploy manifest") {
        steps {
          script {
            container('builder') {
              dir("custom_workspace/code/bb-cd-deploy-app") {
                new ManifestService(this, codeVariables).createDeployManifest()
              }
            }
          }
        }
        post {
          always {
            sh "rm -rf custom_workspace/"
          }
        }
      }
      stage("Push Deployment") {
        environment {
          GIT_CREDS = credentials("${pipelineConfig.git.credentials.id}")
        }
        steps {
          script {
            container('builder') {
              new GitService(this).pushManifest()
            }
          }
        }
      }
      stage("Synchronize With ArgoCD") {
        environment {
          ARGOCD_CREDS = credentials("${pipelineConfig.argocd.credentials.id}")
        }
        steps {
          script {
            container('builder') {
              new ArgoService(this).checkAndCreateApplication(codeVariables)
            }
          }
        }
      }
    }
  }
}