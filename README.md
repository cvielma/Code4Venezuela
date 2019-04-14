# Description
This repository is for the participation in the Code4Venezuela Hackathon. https://www.codeforvenezuela.org/sf-hackathon

We participated specifically in the Data Ingestion Pipeline and Data Enrichment for the project: [MVP-INF Using Twitter Data to Help Public Health](https://github.com/code-for-venezuela/2019-april-codeathon/tree/master/challenges/MPV-INF).

# What the code does
This code has different parts:
- Twitter data extraction 
- Stream Processing (via Flink)
- NLP processing (using CoreNLP)
- Storage to database (PostgreSQL)

We provide 2 ways to get data from Twitter pending approval for Premium API access. 
One is in a polling way using current free apis, and the other via the Flink connector. Since the main solution is based on Flink, 
what we do is to use the first (poll) and publish to Kafka which is then consumed by Flink (AppKafka.java), the other (streaming) uses Flink's Twitter
Connector (AppStream). Both use the same Flink Pipeline (Pipeline.java)

We have trained a simple model (in the resources/train_data folder) in Spanish, using existing tweets from the initial data set in the project, 
as well as other sources, and were able to tag based on: 
- NEEDS: people needing medicine
- MED: medicine names
- OFFERS: people offering medicine
- LOC: to indicate location
- CONTACT: contact information
- SICK: sickness or diseases

The Pipeline then extracts the text from the tweet, processes it using the NLP model and stores it in the DB. 

The project includes more things, and we might expand it in the future to include things like: deduplication, a better tagging model,
storing geolocalization data, and more things that could help AI and data mining.

# How to run?
1. docker-compose up (needed to have local Kafka running) .
2. Start AppKafka.java (just run the class which has a main method).
3. Once AppKafka is started, run TwitterConnectUtil (once again run main method) to feed the stream.

You can uncomment the output via console line to see how the ingested tweets are
tagged.

# Extra

Dr.Julio Castro's data has been de-duplicated. In order to download it, follow this link: https://drive.google.com/open?id=1tbuw0KfmNMxuwLTmRISec7k9pghIU-9e

The python notebook provided in this repo removes duplicates from Dr. Castro's data.

The python server provided in this repo is for real time classification of new tweets, existing tweets, and near-duplicates (This is to address tweets that are just retweets of other tweets and other cases). By default, the server is started on localhost:30303. However, you can pass arguments to change the port. To run it, run

python server.py

To test it you can run the following command:

curl -X POST -d '{"text":"juan luis necesita medicinas en el tachira"}' http://localhost:30303

It will return a json with a response indicating whether or not the message is already in the corpus.

Is the near-duplicate thing actually working? clone the repo, and run this command and see what comes out!:

curl -X POST -d '{"text":"RT @juan RT @AgenciaCN: #3Feb #ServicioPúblico Se solicitan donantes de sangre ORH+ https://t.co/eE8twKWIut #ACN https://t.co/zK5nhyydUJ"}' http://localhost:30303

# License
CoreNLP is distributed under GPL so we are obliged to do so as well. If you don't use any of the CoreNLP then you can consider the
rest of the code MIT licensed (unless we have missed any other restrictions on dependant libraries). 

# The Team
- Christian Vielma
- Manuel Salgado
- Marcos Grillo
- Karina Celis
- Juan Lopez Marcano

# Helpful Links
- http://data.cervantesvirtual.com/blog/2017/07/17/libreria-corenlp-de-stanford-de-procesamiento-lenguage-natural-reconocimiento-entidades/
- https://stanfordnlp.github.io/CoreNLP/api.html
- https://pradeepprabakar.wordpress.com/2012/05/30/near-duplicate-detection-in-streaming-twitter-data/
