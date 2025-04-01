package if4030.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

public final class LinesProcessor {

    public static final String INPUT_TOPIC = "lines-stream";
    public static final String OUTPUT_TOPIC = "words-stream";
    // Pattern to match words composed of alphabetic characters and hyphens
    private static final Pattern WORD_PATTERN = Pattern.compile("\\p{IsAlphabetic}+(-\\p{IsAlphabetic}+)*");

    static Properties streamsConfig(final String[] args) throws IOException {
        final Properties props = new Properties();
        if (args != null && args.length > 0) {
            try (final FileInputStream fis = new FileInputStream(args[0])) {
                props.load(fis);
            }
            if (args.length > 1) {
                System.out.println("Warning: Some command line arguments were ignored. This demo only accepts an optional configuration file.");
            }
        }
        // Use a unique application ID for this component
        props.putIfAbsent(StreamsConfig.APPLICATION_ID_CONFIG, "streams-lines-processor");
        props.putIfAbsent(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.putIfAbsent(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);
        props.putIfAbsent(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.putIfAbsent(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    static void createLinesToWordsStream(final StreamsBuilder builder) {
        final KStream<String, String> source = builder.stream(INPUT_TOPIC);

        final KStream<String, String> words = source
            // Split lines into potential words based on non-word characters
            .flatMapValues(value -> Arrays.asList(value.toLowerCase(Locale.getDefault()).split("\\W+")))
            // Filter out empty strings and non-matching words
            .filter((key, word) -> !word.isEmpty() && WORD_PATTERN.matcher(word).matches());

        // Use peek for debugging if needed
        // words.peek((key, word) -> System.out.println("key: " + key + " - word: " + word));

        // Send valid words to the output topic
        words.to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));
    }

    public static void main(final String[] args) throws IOException {
        final Properties props = streamsConfig(args);

        final StreamsBuilder builder = new StreamsBuilder();
        createLinesToWordsStream(builder);
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        final CountDownLatch latch = new CountDownLatch(1);

        // Attach shutdown handler
        Runtime.getRuntime().addShutdownHook(new Thread("streams-lines-processor-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            System.out.println("LinesProcessor started.");
            latch.await();
        } catch (final Throwable e) {
            System.err.println("Error starting LinesProcessor: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("LinesProcessor stopped.");
        System.exit(0);
    }
}
