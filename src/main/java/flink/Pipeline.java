package flink;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import dto.TagsDto;
import dto.TweetDto;
import kafka.KafkaConstants;
import nlp.ServicioPublicoTagger;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

public class Pipeline {

    private static final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    public static void process(DataStream<String> streamSource) {

        DataStream<TweetDto> filteredStream = filter(streamSource)
                .flatMap(new TaggerFlatmap(new ObjectMapper(), new ServicioPublicoTagger()));

//        filteredStream.print();

        filteredStream.map(dto -> ow.writeValueAsString(dto)).addSink(createProducer());
    }

    private static FlinkKafkaProducer<String> createProducer() {
        return new FlinkKafkaProducer<>(
                "127.0.0.1:9092",
                KafkaConstants.OUTPUT_TOPIC,
                new SimpleStringSchema());
    }

    private static DataStream<String> filter(DataStream<String> streamSource) {
        return streamSource.filter(string -> !string.isEmpty())
                .filter(string -> !string.matches("[0-9]>"));
    }

    // This could be probably more efficient
    public static class TaggerFlatmap implements FlatMapFunction<String, TweetDto> {
        private static final long serialVersionUID = 1L;
        private transient ServicioPublicoTagger tagger;
        private transient ObjectMapper jsonParser;

        public TaggerFlatmap(ObjectMapper jsonParser, ServicioPublicoTagger tagger) {
            this.jsonParser = jsonParser;
            this.tagger = tagger;
        }

        @Override
        public void flatMap(String value, Collector<TweetDto> out) throws Exception {
            if(jsonParser == null) {
                System.out.println("Creating jsonParser");
                jsonParser = new ObjectMapper();
            }

            if (tagger == null) {
                System.out.println("Creating tagger");
                tagger = new ServicioPublicoTagger();
            }
            JsonNode jsonNode = jsonParser.readValue(value, JsonNode.class);
            String text = jsonNode.get("text").asText();
            TweetDto dto = new TweetDto();
            dto.setOriginalTweet(value);
            dto.setTweetText(text);
            dto.setTags(new TagsDto(tagger.getTagsFor(text)));
            out.collect(dto);
        }
    }

}
