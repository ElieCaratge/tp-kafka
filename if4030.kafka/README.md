# TP Kafka - Analyse de texte

Ce projet implémente un système d'analyse de texte basé sur Kafka Streams et Consumer API. Il permet de trouver les mots les plus utilisés par catégorie grammaticale dans un texte.

## Architecture

Le système est composé de trois composants principaux :

1. **LinesProcessor** : Lit les lignes du topic `lines-stream`, extrait les mots (caractères alphabétiques et tirets), les convertit en minuscules, et les envoie au topic `words-stream`.

2. **WordsNormalizer** : Lit les mots du topic `words-stream`, normalise les mots en utilisant la base de données lexicale Lexique383, et les envoie au topic `tagged-words-stream` avec la catégorie grammaticale comme clé.

3. **WordsAnalyzer** : Lit les mots du topic `tagged-words-stream`, compte les occurrences par catégorie, et affiche les 20 mots les plus utilisés par catégorie lorsqu'il reçoit la commande "END" sur le topic `command-topic`.

## Prérequis

- Java 21
- Apache Kafka
- Maven

## Installation

1. Cloner le dépôt
2. Compiler le projet avec Maven :
   ```
   cd if4030.kafka
   mvn clean package
   ```

## Utilisation

### 1. Démarrer le serveur Kafka

```
./server.sh
```

### 2. Créer les topics nécessaires

```
./create_topics.sh
```

### 3. Démarrer les composants dans des terminaux séparés

Terminal 1 - LinesProcessor :
```
java -cp target/tp-kafka-0.0.1-SNAPSHOT.jar if4030.kafka.LinesProcessor
```

Terminal 2 - WordsNormalizer :
```
java -cp target/tp-kafka-0.0.1-SNAPSHOT.jar if4030.kafka.WordsNormalizer
```

Terminal 3 - WordsAnalyzer :
```
java -cp target/tp-kafka-0.0.1-SNAPSHOT.jar if4030.kafka.WordsAnalyzer
```

### 4. Envoyer un fichier texte au système

```
./send_text.sh data/balzac/etude-de-femme.txt
```

### 5. Envoyer la commande END pour afficher les résultats

```
./send_end_command.sh
```

## Exemples de fichiers texte

Le dossier `data` contient des exemples de fichiers texte de Balzac et Zola que vous pouvez utiliser pour tester le système.

## Base de données lexicale

Le système utilise la base de données lexicale Lexique383 pour normaliser les mots (convertir les pluriels en singuliers, les verbes conjugués en infinitifs, etc.) et déterminer leur catégorie grammaticale.

## Catégories grammaticales

Les catégories grammaticales utilisées sont celles de Lexique383 :
- NOM : Noms
- VER : Verbes
- ADJ : Adjectifs
- ADV : Adverbes
- etc.

Les catégories suivantes sont ignorées dans l'analyse :
- DET : Déterminants
- PRO : Pronoms
- PRP : Prépositions
- CON : Conjonctions
- UNK : Catégorie inconnue
