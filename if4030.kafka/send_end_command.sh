#!/bin/sh

echo "Sending END command to the command-topic..."
echo "END" | kafka-console-producer.sh --bootstrap-server localhost:9092 \
        --topic command-topic

echo "END command sent successfully."
