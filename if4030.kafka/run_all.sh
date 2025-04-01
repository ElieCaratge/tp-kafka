#!/bin/sh

# This script runs all three components of the text analysis system in sequence.
# It creates the necessary topics, starts the components, and provides instructions
# for sending text files and the END command.

echo "=== TP Kafka - Analyse de texte ==="
echo

# Create topics if they don't exist
echo "Creating Kafka topics..."
./create_topics.sh

# Compile the project if needed
if [ ! -f "target/tp-kafka-0.0.1-SNAPSHOT.jar" ]; then
  echo "Compiling the project..."
  mvn clean package
fi

echo
echo "Starting the components..."
echo "Please open three separate terminals and run the following commands:"
echo
echo "Terminal 1 - LinesProcessor:"
echo "java -cp target/tp-kafka-0.0.1-SNAPSHOT.jar if4030.kafka.LinesProcessor"
echo
echo "Terminal 2 - WordsNormalizer:"
echo "java -cp target/tp-kafka-0.0.1-SNAPSHOT.jar if4030.kafka.WordsNormalizer"
echo
echo "Terminal 3 - WordsAnalyzer:"
echo "java -cp target/tp-kafka-0.0.1-SNAPSHOT.jar if4030.kafka.WordsAnalyzer"
echo
echo "After starting all components, you can send a text file to the system with:"
echo "./send_text.sh data/balzac/etude-de-femme.txt"
echo
echo "And finally, send the END command to display the results:"
echo "./send_end_command.sh"
echo
