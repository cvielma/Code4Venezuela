package nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

import static java.util.Collections.singletonList;

public class ServicioPublicoTagger {

    private final StanfordCoreNLP pipeline;

    public ServicioPublicoTagger() {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner");

        props.setProperty("tokenize.language", "es");
        props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/spanish/spanish-distsim.tagger");
        props.setProperty("ner.model", "src/main/resources/ner-model.ser.gz");
        props.setProperty("ner.useSUTime", "0");

        this.pipeline = new StanfordCoreNLP(props);
    }

    public Map<ServicioPublicoNerTag, Set<String>> getTagsFor(String text) {
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);

        // these are all the sentences in this document
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        Map<ServicioPublicoNerTag, Set<String>> nerTags = new HashMap<>();

        for (CoreMap sentence : sentences) {

            // traversing the words in the current sentence
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);

                // this is the NER label of the token
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                ServicioPublicoNerTag.getTag(ne).ifPresent(
                        t -> {
                            if (nerTags.containsKey(t)) {
                                nerTags.get(t).add(word);
                            } else {
                                nerTags.put(t, new HashSet<>(singletonList(word)));
                            }
                        });
            }
        }
        return nerTags;
    }
}
