#!/bin/sh
kafka-topics.sh --create --bootstrap-server localhost:9092 \
        --replication-factor 1 --partitions 1 \
        --topic streams-wordcount-output
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
        --topic streams-wordcount-output --from-beginning \
        --formatter org.apache.kafka.tools.consumer.DefaultMessageFormatter --property print.key=true \
        --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
        --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
