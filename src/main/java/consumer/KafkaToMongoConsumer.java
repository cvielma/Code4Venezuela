package consumer;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import kafka.KafkaConstants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.bson.Document;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import static kafka.KafkaConstants.OUTPUT_TOPIC;
import static org.bson.Document.parse;

/**
 * This class consumes the stream of data from Kafka and saves it to the MongoDB.
 */
public class KafkaToMongoConsumer {



    private static KafkaConsumer<String, String> newKafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.KAFKA_BROKERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "mongodbexporter");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return new KafkaConsumer<>(props);

    }

    private static MongoCollection getCollection() {
        MongoClientURI uri = new MongoClientURI("mongodb://root:root@localhost/?authSource=admin");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("admin");
        return database.getCollection("processedTweets");
    }

    private static void insertToCollection(MongoCollection collection, String value) {
        collection.insertOne(parse(value));
    }

    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = newKafkaConsumer();
        consumer.subscribe(Arrays.asList(OUTPUT_TOPIC));
        MongoCollection collection = getCollection();
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                insertToCollection(collection, record.value());
            }
        }
    }

}
