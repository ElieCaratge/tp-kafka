# TP Kafka

Elie Caratgé & Antoine Chéneau

## Word processing documentation

See [if3040.kafka/README](./if4030.kafka/README.md)

## What

This image is based on [docker-openvscode-server](https://gitlab-research.centralesupelec.fr/my-docker-images/docker-openvscode-server),
it adds some needed tools for the Kafka exercise of the course 3IF4030.

Included tools are:
- openjdk-21-jdk
- maven
- kafka

The initial project is also included.

## Details

- The exposed port is 3000
- The user folder is `/config`
- the user and sudo password is `abc`
- If docker (or podman) is installed on your computer, you can run (`amd64` or `arm64` architecture) this 
  image, assuming you are in a specific folder that will be shared with the container at 
  `/config`, with:
  
  `docker run -p 3000:3000 -v "$(pwd):/config"
    gitlab-research.centralesupelec.fr:4567/my-docker-images/docker-openvscode-server-kafka`
