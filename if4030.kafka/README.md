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

## Résultats

Des résultats sont affichés dans le dossier output à la racine du projet. Comparons par exemple les adjectifs les plus utlisés dans le texte de Balzac et Zola :

### Balzac : Etude de femme

Category: ADJ
---------------------------
tout                 8
carolin              5
vertueux             4
difficile            3
fait                 2
plein                2
lu                   2
pris                 2
immobile             2
modeste              2
social               1
inconvenant          1
magique              1
subit                1
bleu                 1
vrai                 1
matinal              1
fatal                1
cis                  1
atteint              1

### Zola : Chapitre I de Germinal


Category: ADJ
---------------------------
rare                 3
pris                 3
tendu                3
nouvelle             3
tout                 3
joli                 2
plein                2
mis                  2
repu                 1
gigantesque          1
triste               1
connu                1
saignant             1
court                1
immobile             1
bleu                 1
douloureux           1
assis                1
endormi              1
su                   1


### Remarques

On remarque ainsi que Balzac utilise plus d'adjectifs que Zola avec des mots plus recherchés comme "carolin" et "vertueux" alors que Zola utilise des adjectifs plus simples comme "rare" et "tendu".
