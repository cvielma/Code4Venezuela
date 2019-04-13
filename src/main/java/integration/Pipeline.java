package integration;


import nlp.ServicioPublicoTagger;
import org.apache.flink.api.java.io.jdbc.JDBCOutputFormat;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.sql.Types;

public class Pipeline {

    private ServicioPublicoTagger tagger;

    public Pipeline(ServicioPublicoTagger tagger) {
        this.tagger = tagger;
    }

    private static final JDBCOutputFormat JDBC_OUTPUT_FORMAT = JDBCOutputFormat.buildJDBCOutputFormat()
            .setDrivername("org.postgresql.Driver")
            .setDBUrl("jdbc:postgresql://db:5432/postgres")
            .setQuery("INSERT INTO public.test VALUES (?)")
            .setSqlTypes(new int[] {Types.VARCHAR})
            .setBatchInterval(2)
            .finish();

    public void process(DataStream<String> streamSource) {

        DataStream<String> filteredStream = filter(streamSource);

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

}

