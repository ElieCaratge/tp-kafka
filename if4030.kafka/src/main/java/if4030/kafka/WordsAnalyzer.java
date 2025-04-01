package if4030.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

public final class WordsAnalyzer {

    public static final String WORDS_TOPIC = "tagged-words-stream";
    public static final String COMMAND_TOPIC = "command-topic";
    private static final String END_COMMAND = "END";
    
    // Categories to ignore (articles, pronouns, etc.)
    private static final Set<String> CATEGORIES_TO_IGNORE = new HashSet<>(
        Arrays.asList("DET", "PRO", "PRP", "CON", "UNK")
    );
    
    // Map to store word counts by category
    private static final Map<String, Map<String, Integer>> wordCountsByCategory = new HashMap<>();
    
    static Properties consumerConfig() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "words-analyzer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
    
    // Process a word record
    static void processWordRecord(String category, String word) {
        // Skip ignored categories
        if (CATEGORIES_TO_IGNORE.contains(category)) {
            return;
        }
        
        // Get or create the word count map for this category
        Map<String, Integer> wordCounts = wordCountsByCategory.computeIfAbsent(category, k -> new HashMap<>());
        
        // Increment the count for this word
        wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
    }
    
    // Display the top words by category
    static void displayTopWords(int topN) {
        System.out.println("\n===== TOP " + topN + " WORDS BY CATEGORY =====\n");
        
        for (String category : wordCountsByCategory.keySet()) {
            Map<String, Integer> wordCounts = wordCountsByCategory.get(category);
            
            // Create a priority queue to sort words by count
            PriorityQueue<Entry<String, Integer>> topWords = 
                new PriorityQueue<>(Comparator.comparing(Entry<String, Integer>::getValue, Comparator.reverseOrder()));
            topWords.addAll(wordCounts.entrySet());
            
            System.out.println("Category: " + category);
            System.out.println("---------------------------");
            
            // Display the top N words
            for (int i = 0; i < topN && !topWords.isEmpty(); i++) {
                Entry<String, Integer> entry = topWords.poll();
                System.out.printf("%-20s %d%n", entry.getKey(), entry.getValue());
            }
            
            System.out.println();
        }
    }
    
    public static void main(final String[] args) {
        final Consumer<String, String> consumer = new KafkaConsumer<>(consumerConfig());
        
        // Subscribe to both topics
        consumer.subscribe(Arrays.asList(WORDS_TOPIC, COMMAND_TOPIC));
        
        // Add shutdown hook
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Detected a shutdown, closing consumer...");
                consumer.wakeup();
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        System.out.println("WordsAnalyzer started. Listening for words and commands...");
        System.out.println("Send 'END' to the command-topic to display results and exit.");
        
        try {
            boolean running = true;
            
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                
                for (ConsumerRecord<String, String> record : records) {
                    String topic = record.topic();
                    String key = record.key();
                    String value = record.value();
                    
                    if (COMMAND_TOPIC.equals(topic) && END_COMMAND.equals(value)) {
                        // Received END command
                        System.out.println("Received END command. Displaying results...");
                        displayTopWords(20);
                        running = false;
                        break;
                    } else if (WORDS_TOPIC.equals(topic)) {
                        // Process word record
                        processWordRecord(key, value);
                    }
                }
            }
        } catch (WakeupException e) {
            // Ignore, we're closing
        } catch (Exception e) {
            System.err.println("Error in WordsAnalyzer: " + e.getMessage());
            e.printStackTrace();
        } finally {
            consumer.close();
            System.out.println("WordsAnalyzer stopped.");
        }
    }
}
