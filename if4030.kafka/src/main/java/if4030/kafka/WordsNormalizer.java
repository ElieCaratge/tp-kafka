package if4030.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public final class WordsNormalizer {

    public static final String INPUT_TOPIC = "words-stream";
    public static final String OUTPUT_TOPIC = "tagged-words-stream";
    private static final String LEXIQUE_PATH = "/workspaces/Kafka-TP/Lexique383/Lexique383.tsv";

    // Maps to store word -> lemma and word -> category
    private static Map<String, String> lemmaDict = new HashMap<>();
    private static Map<String, String> categoryDict = new HashMap<>();

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
        props.putIfAbsent(StreamsConfig.APPLICATION_ID_CONFIG, "streams-words-normalizer");
        props.putIfAbsent(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.putIfAbsent(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);
        props.putIfAbsent(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.putIfAbsent(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    // Load the lexical database
    static void loadLexique() throws IOException {
        System.out.println("Loading lexical database from " + LEXIQUE_PATH);
        try (BufferedReader reader = new BufferedReader(new FileReader(LEXIQUE_PATH))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 4) {
                    String ortho = parts[0].toLowerCase();
                    String lemme = parts[2].toLowerCase();
                    String cgram = parts[3];
                    
                    lemmaDict.put(ortho, lemme);
                    categoryDict.put(ortho, cgram);
                }
            }
        }
        System.out.println("Loaded " + lemmaDict.size() + " words into the lexical database");
    }

    static void createWordsNormalizerStream(final StreamsBuilder builder) {
        final KStream<String, String> source = builder.stream(INPUT_TOPIC);

        final KStream<String, String> normalizedWords = source
            .map((key, word) -> {
                // Get the lemma and category for the word
                String lemma = lemmaDict.getOrDefault(word, word);
                String category = categoryDict.getOrDefault(word, "UNK"); // Unknown category
                
                // Use the category as the key and the lemma as the value
                return KeyValue.pair(category, lemma);
            });

        // Use peek for debugging if needed
        // normalizedWords.peek((category, lemma) -> System.out.println("category: " + category + " - lemma: " + lemma));

        // Send normalized words to the output topic
        normalizedWords.to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));
    }

    public static void main(final String[] args) throws IOException {
        // Load the lexical database
        loadLexique();
        
        final Properties props = streamsConfig(args);

        final StreamsBuilder builder = new StreamsBuilder();
        createWordsNormalizerStream(builder);
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        final CountDownLatch latch = new CountDownLatch(1);

        // Attach shutdown handler
        Runtime.getRuntime().addShutdownHook(new Thread("streams-words-normalizer-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            System.out.println("WordsNormalizer started.");
            latch.await();
        } catch (final Throwable e) {
            System.err.println("Error starting WordsNormalizer: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("WordsNormalizer stopped.");
        System.exit(0);
    }
}
