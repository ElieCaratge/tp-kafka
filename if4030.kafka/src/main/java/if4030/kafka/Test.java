package if4030.kafka;

import java.io.IOException;

/**
 * Test class to demonstrate the text analysis system.
 * This class provides a simple way to run all three components of the system.
 */
public class Test {

    public static void main(String[] args) {
        System.out.println("TP Kafka - Analyse de texte");
        System.out.println("===========================");
        System.out.println();
        System.out.println("Ce programme démontre l'utilisation du système d'analyse de texte.");
        System.out.println("Pour une utilisation complète, veuillez suivre les instructions dans le README.md.");
        System.out.println();
        System.out.println("Pour tester le système :");
        System.out.println("1. Démarrer le serveur Kafka avec ./server.sh");
        System.out.println("2. Créer les topics avec ./create_topics.sh");
        System.out.println("3. Démarrer les composants dans des terminaux séparés :");
        System.out.println("   - LinesProcessor : java -cp target/tp-kafka-0.0.1-SNAPSHOT.jar if4030.kafka.LinesProcessor");
        System.out.println("   - WordsNormalizer : java -cp target/tp-kafka-0.0.1-SNAPSHOT.jar if4030.kafka.WordsNormalizer");
        System.out.println("   - WordsAnalyzer : java -cp target/tp-kafka-0.0.1-SNAPSHOT.jar if4030.kafka.WordsAnalyzer");
        System.out.println("4. Envoyer un fichier texte avec ./send_text.sh data/balzac/etude-de-femme.txt");
        System.out.println("5. Envoyer la commande END avec ./send_end_command.sh");
        System.out.println();
        
        // Uncomment the following code to run all components in sequence
        // Note: This is not recommended for production use, as it runs all components in the same JVM
        /*
        try {
            // Start LinesProcessor
            System.out.println("Starting LinesProcessor...");
            Thread linesProcessorThread = new Thread(() -> {
                try {
                    LinesProcessor.main(args);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            linesProcessorThread.start();
            
            // Start WordsNormalizer
            System.out.println("Starting WordsNormalizer...");
            Thread wordsNormalizerThread = new Thread(() -> {
                try {
                    WordsNormalizer.main(args);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            wordsNormalizerThread.start();
            
            // Start WordsAnalyzer
            System.out.println("Starting WordsAnalyzer...");
            Thread wordsAnalyzerThread = new Thread(() -> {
                WordsAnalyzer.main(args);
            });
            wordsAnalyzerThread.start();
            
            // Wait for all threads to complete
            linesProcessorThread.join();
            wordsNormalizerThread.join();
            wordsAnalyzerThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }
}
