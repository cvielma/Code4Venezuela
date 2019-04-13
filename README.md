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
what we do is to use the first (poll) to export to a file which is then used for Flink (AppFile.java), the other (streaming) uses Flink's Twitter
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
The easiest way is to run the AppFile.java class. You can uncomment the output via console line to see how the ingested tweets are
tagged.

# License
CoreNLP is distributed under GPL so we are obliged to do so as well. If you don't use any of the CoreNLP then you can consider the
rest of the code MIT licensed (unless we have missed any other restrictions on the licenses of the project, keeping in mind
we did it without much previous knowledge on Twitter APIs, NLP or even Flink and during a Hackathon). 

# The Team
Christian Vielma
Manuel Salgado
Marcos Grillo
Karina Celis

