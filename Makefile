
REPO = gitlab-research.centralesupelec.fr:4567/my-docker-images/docker-openvscode-server-kafka
TAG  = latest

all:
	docker buildx create --use --node new-builder
	docker buildx build --provenance=false --push --platform "linux/amd64","linux/arm64" --tag "${REPO}:${TAG}" .

