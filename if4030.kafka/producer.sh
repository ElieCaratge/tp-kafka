#!/bin/sh
kafka-topics.sh --create --bootstrap-server localhost:9092 \
        --replication-factor 1 --partitions 1 \
        --topic streams-plaintext-input
kafka-console-producer.sh --bootstrap-server localhost:9092 \
        --topic streams-plaintext-input
