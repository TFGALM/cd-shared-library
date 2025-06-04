# bb-cd-shared-library

Librería para Jenkins que se encarga de gestionar qué versión de nuestro software se despliega en cada clúster.

## Funcionamiento (a alto nivel)

 - Cada aplicación tendrá un repositorio exclusivo con el resultado de la pipeline de Jenkins que genera los siguentes ficheros:
 - Chart.yaml --> con la definicíon del chart template que usamos para los despliegues y se encuentra alojado en Nexus.
 - values.yaml --> fichero con los detalles del imagen y el tag correspondiente y los valores que el equipo de desarollo quiera pasarle al deployment.
 - application.yaml --> fichero con la definicion de la aplicacíon desplegada en ArgoCD donde se indica el repositorio, el cluster y todos los detalles necesario para que ArgoCD depliegue en automatico la applicacion.
 - En Jenkins habrá un proyecto para cada repositorio, y este, al ejecutarse, deberá pedir qué versión desplegar en que cluster y en que namespace (por defecto se crear un namespace con el mismo nombre del repositorio).
 - Jenkins se encargará de validar que todo esté ingresado correctamente y realizará un commit en el repositorio de CD con el número de versión a desplegar.

## Notas sobre como pasar las variables personalizadas desde el equipo de desarollo

El equipo de desarollo va a tener un repositorio dedicado con la siguente sintaxy vars-<nombre_del_repositorio> en el cual deberian crear una estructura de directorios donde indican el nombre del cluster , el environment (dev,qa,prod) y el namespace donde quieren desplegar
En el fichero vars.yaml pueden indicar las variables necesarias, si se quiere configurar un service es necesario usar la siguente sintaxi en la definicion de las variables:
````yaml
PORTS:
  - PORT: 8080
    TARGET_PORT: 8080
    NAME: http
````

Mientras si el contenedor necesita mapear algun volumen (antes de definir las variables avisar el equipo de DevOps para que confirme que los volumenes esten creados y disponibles )
Si en un mount de tipo hostPath hay que utilizar la siguente sintaxi
````yaml
VOLUMES:
  - NAME: my-volume
    MOUNT_TYPE: hostPath
    PATH: "/opt/host-path"
    TYPE: Directory
    MOUNT_PATH: "/opt/pod-path"
````
Mientras si es el volumen es de tipo PVC hay que usar la siguente sintaxi
````yaml
VOLUMES:
  - NAME: my-volume
    MOUNT_TYPE: persistentVolumeClaim
    CLAIM_NAME: pvc-claim-name
    MOUNT_PATH: "/opt/pod-path"
````

## Variables de entorno de Jenkins 

Ejecutando el comando 'env|sort' nos devuelve las variables que estan disponible en la ejecucion de Jenkins y que se pueden usar en nuesta pipeline.
````yaml
  stage("Print Jenkins Variables") {
        steps {
          script {
            container('builder') {
              echo sh(script: 'env|sort', returnStdout: true)
            }
          }
        }
      }
````
