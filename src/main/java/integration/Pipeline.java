package integration;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.TweetDto;
import nlp.ServicioPublicoTagger;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.io.jdbc.JDBCOutputFormat;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import java.sql.Types;

public class Pipeline {


    private static final JDBCOutputFormat JDBC_OUTPUT_FORMAT = JDBCOutputFormat.buildJDBCOutputFormat()
            .setDrivername("org.postgresql.Driver")
            .setDBUrl("jdbc:postgresql://db:5432/postgres")
            .setQuery("INSERT INTO public.test VALUES (?)")
            .setSqlTypes(new int[] {Types.VARCHAR})
            .setBatchInterval(2)
            .finish();

    public static void process(DataStream<String> streamSource) {

        DataStream<TweetDto> filteredStream = filter(streamSource)
                .flatMap(new TaggerFlatmap(new ObjectMapper(), new ServicioPublicoTagger()));

        filteredStream.print();

        //outPutToDB(filteredStream);
    }

    private static DataStream<String> filter(DataStream<String> streamSource) {
        return streamSource.filter(string -> !string.isEmpty())
                .filter(string -> !string.matches("[0-9]>"));
    }

    private static void outPutToDB(DataStream<String> streamSource) {
        DataStream<Row> rows = streamSource
                .map(string ->{
                    Row row = new Row(1);
                    row.setField(0, string);
                    return row;
                });

        rows.writeUsingOutputFormat(JDBC_OUTPUT_FORMAT);

        rows.print();
    }

    public static class TaggerFlatmap implements FlatMapFunction<String, TweetDto> {
        private static final long serialVersionUID = 1L;
        private transient ServicioPublicoTagger tagger;
        private transient ObjectMapper jsonParser;

        public TaggerFlatmap(ObjectMapper jsonParser, ServicioPublicoTagger tagger) {
            this.jsonParser = jsonParser;
            this.tagger = tagger;
        }

        /**
         * Select the language from the incoming JSON text
         */
        @Override
        public void flatMap(String value, Collector<TweetDto> out) throws Exception {
            if(jsonParser == null) {
                jsonParser = new ObjectMapper();
            }

            if (tagger == null) {
                tagger = new ServicioPublicoTagger();
            }
            JsonNode jsonNode = jsonParser.readValue(value, JsonNode.class);
            String text = jsonNode.get("text").asText();
            TweetDto dto = new TweetDto();
//            dto.setOriginalTweet(value);
            dto.setTweetText(text);
            dto.setTags(tagger.getTagsFor(text));
            out.collect(dto);
        }
    }

}
