package flink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

/**
 * Executes the Flink pipeline with a Kafka source.
 * @see twitter.TwitterConnectUtil for Kafka input.
 */
public class AppStream {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "streamapp");

        DataStream<String> streamSource = env
                .addSource(new FlinkKafkaConsumer<>("test-topic", new SimpleStringSchema(), properties));

        Pipeline.process(streamSource);

        env.execute();
    }
}

