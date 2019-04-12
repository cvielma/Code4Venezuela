// (C) king.com Ltd 2019

import com.twitter.hbc.core.endpoint.RawEndpoint;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.twitter.TwitterSource;

import java.io.Serializable;

/**
 * Usage [--twitter-source.consumerKey <key> --twitter-source.consumerSecret <secret> --twitter-source.token <token> --twitter-source.tokenSecret <tokenSecret>]
 */
public class App {

    public static void main(String[] args) throws Exception {

        final ParameterTool params = ParameterTool.fromArgs(args);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();


        DataStream<String> streamSource = null;
        if (params.has(TwitterSource.CONSUMER_KEY) &&
                params.has(TwitterSource.CONSUMER_SECRET) &&
                params.has(TwitterSource.TOKEN) &&
                params.has(TwitterSource.TOKEN_SECRET)) {
            final TwitterSource twitterSource = new TwitterSource(params.getProperties());
            //twitterSource.setCustomEndpointInitializer(new CustomEndPoint());
            streamSource = env.addSource(twitterSource);
        } else {
            System.out.println("missing params");
        }

        streamSource.print();
        env.execute();
    }


    //TODO pending to get it working with custom endpoint to filter by hashtag
    public static class CustomEndPoint implements TwitterSource.EndpointInitializer, Serializable {
        @Override
        public StreamingEndpoint createEndpoint() {
            return new RawEndpoint("https://stream.twitter.com/1.1/statuses/filter.json?track=%23ServicioPublico", "POST");
        }}

}
