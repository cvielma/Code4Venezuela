package twitter;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Queries twitter Search API with the search criteria and publishes results to Kafka
 * to be processed by {@link flink.AppStream}
 */
public class TwitterConnectUtil {

    private static final String PROTECTED_RESOURCE_URL = "https://api.twitter.com/1.1/search/tweets.json?q=%23ServicioPublico&geocode=5.1633,-69.4147,700km&count=50&result_type=recent";

    @SuppressWarnings("PMD.SystemPrintln")
    public static void main(String... args) throws IOException, InterruptedException, ExecutionException {

        final OAuth10aService service = new ServiceBuilder("aqjymKONgAoTRBfZ7pFHBEHp1")
                .apiSecret("3WX5q2gNWEx3u7PiUXWR2OUkvETE19YENCsXdJ3Ydbefni0bi9")
                .build(TwitterApi.instance());

        final OAuth1AccessToken accessToken = new OAuth1AccessToken("65368099-1uHdzq1UT0EGNLqslks01WxFIYenPfmqgWSAMwAVy", "FzzJut9Xb1tS3SXuiuEuTeimCk6nSq04NrMW0szCrqYnZ", "oauth_token=65368099-1uHdzq1UT0EGNLqslks01WxFIYenPfmqgWSAMwAVy&oauth_token_secret=FzzJut9Xb1tS3SXuiuEuTeimCk6nSq04NrMW0szCrqYnZ&user_id=65368099&screen_name=cv13lm4");

        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);

        final Response response = service.execute(request);
        String json = response.getBody();

        JSONObject obj = new JSONObject(json);

        JSONArray arr = obj.getJSONArray("statuses");

        Producer<Long, String> producer = createProducer();

        for (int i = 0; i < arr.length(); i++) {
            String status = arr.getJSONObject(i).toString();
            System.out.println(status);
            producer.send(new ProducerRecord<>("source-topic", Long.valueOf(i), status));
        }
    }

    public static Producer<Long, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.KAFKA_BROKERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, KafkaConstants.ACKS);
        props.put(ProducerConfig.RETRIES_CONFIG, KafkaConstants.RETRIES);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, KafkaConstants.BATCH_SIZE);
        props.put(ProducerConfig.LINGER_MS_CONFIG, KafkaConstants.LINGER_MS);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, KafkaConstants.BUFFER_MEMORY);

        return new KafkaProducer<>(props);
    }
}
