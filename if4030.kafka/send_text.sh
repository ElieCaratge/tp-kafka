#!/bin/sh

# Check if a file path is provided
if [ $# -lt 1 ]; then
  echo "Usage: $0 <file_path>"
  echo "Example: $0 data/balzac/etude-de-femme.txt"
  exit 1
fi

FILE_PATH=$1

# Check if the file exists
if [ ! -f "$FILE_PATH" ]; then
  echo "Error: File '$FILE_PATH' not found."
  exit 1
fi

echo "Sending file '$FILE_PATH' to the lines-stream topic..."
cat "$FILE_PATH" | kafka-console-producer.sh --bootstrap-server localhost:9092 \
        --topic lines-stream

echo "File sent successfully."
