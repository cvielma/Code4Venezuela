package kafka;

public class KafkaConstants {

    public static final String KAFKA_BROKERS = "127.0.0.1:9092";
    public static final String ACKS = "all";
    public static final int RETRIES = 1;
    public static final int BATCH_SIZE = 16384;
    public static final int LINGER_MS = 1;
    public static final int BUFFER_MEMORY = 33554432;
    public static final String INPUT_TOPIC = "source-topic";
    public static final String OUTPUT_TOPIC = "sink-topic";
}
