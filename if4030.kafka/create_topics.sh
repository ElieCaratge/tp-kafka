#!/bin/sh

# Check if topics already exist and create them if not
TOPICS="lines-stream words-stream tagged-words-stream command-topic"
EXISTING_TOPICS=$(kafka-topics.sh --bootstrap-server localhost:9092 --list)

for TOPIC in $TOPICS
do
  if ! echo "$EXISTING_TOPICS" | grep -q "^${TOPIC}$"; then
    echo "Creating topic: $TOPIC"
    kafka-topics.sh --create --bootstrap-server localhost:9092 \
            --replication-factor 1 --partitions 1 \
            --topic $TOPIC
  else
    echo "Topic $TOPIC already exists."
  fi
done

echo "Topic creation process finished."
