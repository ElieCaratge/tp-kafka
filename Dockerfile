
FROM gitlab-research.centralesupelec.fr:4567/my-docker-images/docker-openvscode-server

## software needed

ARG DEBIAN_FRONTEND="noninteractive"

RUN                                                \
     apt-get update --yes                          \
  && apt-get install -y --no-install-recommends    \
        maven                                      \
        openjdk-21-jdk                             \
  && apt-get clean                                 \
  && rm -rf                                        \
       /tmp/*                                      \
       /var/lib/apt/lists/*                        \
       /var/tmp/*

ARG SCALA_RELEASE="2.13"
ARG KAFKA_RELEASE="3.9.0"
RUN                                                                            \
    curl -o /tmp/kafka_${SCALA_RELEASE}-${KAFKA_RELEASE}.tgz                   \
       "https://dlcdn.apache.org/kafka/${KAFKA_RELEASE}/kafka_${SCALA_RELEASE}-${KAFKA_RELEASE}.tgz" \
  && tar zxf /tmp/kafka_${SCALA_RELEASE}-${KAFKA_RELEASE}.tgz --directory=/opt \
  && rm /tmp/kafka_${SCALA_RELEASE}-${KAFKA_RELEASE}.tgz                       \
  && mv /opt/kafka_${SCALA_RELEASE}-${KAFKA_RELEASE} /opt/kafka                \
  && chown -R abc:abc /opt/kafka                                               \
  && echo "export PATH=/opt/kafka/bin:\$PATH" >>/init-config/.zshrc

# Java extension for VS Code
RUN                                                                 \
    /app/openvscode-server/bin/openvscode-server                    \
        --extensions-dir /init-config/.openvscode-server/extensions \
        --install-extension vscjava.vscode-java-pack

COPY settings.json /init-config/.openvscode-server/data/Machine/
COPY workspace /init-config/workspace

