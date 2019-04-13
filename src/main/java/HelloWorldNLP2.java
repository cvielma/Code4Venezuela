import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class HelloWorldNLP2 {

    public static void main(String[] args) {

        // creates a StanfordCoreNLP object, with POS tagging, lemmatization,
        // NER, parsing, and coreference resolution

        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner");

        props.setProperty("tokenize.language", "es");
        props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/spanish/spanish-distsim.tagger");
//        props.setProperty("ner.model", "edu/stanford/nlp/models/ner/spanish.ancora.distsim.s512.crf.ser.gz");
        props.setProperty("ner.model", "src/main/resources/ner-model.ser.gz");
        props.setProperty("ner.useSUTime", "0");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // read some text in the text variable
        String text = "rt @grilla_pr: #serviciopublico \uD83D\uDE4C\uD83E\uDD17\uD83D\uDE43@massamorra @mrjaimito_ @_lorenq @famocito12 @davicitocmr @casij \n" +
                "rt anonmundial: ⛑#serviciopublico⛑se busca sistema de gastrotomía infantil modelo mk- key 18 fr cualquier #referencia o #donacion arepi…\n" +
                "vía @yammy2103 #serviciopúblico para paciente en maturin se necesita con urgencia las siguientes #medicinas #sos… \n" +
                "rt @cafaba38: #serviciopublico #aestahora  cafaba invita a los empresarios de la región para que abran oportunidad a este gran pr… \n" +
                "@sebuscasedona #serviciopublico necesito para amigo sin recursos medicamentos hyzaar plus 100/25, concor 2,5;  vytorin 10/80, coraspirina rt\n" +
                "rt @gatawitch: busco #provera de 5mgs o #depoprovera @nakamartinez @fundatiacruz #serviciopublico #medicamentos\n" +
                "atodomomento_:#serviciopúblico | urgente yofre rodríguez / donantes de sangre o+ -  \n" +
                "rt @quimijose: rt caraotadigital: #serviciopúblico bebé con quemadas necesita con urgencia ayuda económica / clínica santa sofía … \n" +
                "rt @emepresley: #serviciopúblico necesito con urgencia exforge hct 5mg 160 mg 12,5mgfavor rt.\n" +
                "rt. #urgente  #serviciopúblico  oncológico meropenem de 1gr  información al 0412.3752697 ccs \n";
        String[] lines = text.split("\n");

        ServicioPublicoTagger tagger = new ServicioPublicoTagger();

       for (String line : lines) {
           Map<ServicioPublicoNerTag, Set<String>> tagsFor = tagger.getTagsFor(line);
           if (!tagsFor.isEmpty()) {
               System.out.println("Classified: '" + line + "', with tags: " + prettyPrint(tagsFor));
           }
       }
    }

    private static String prettyPrint(Map<ServicioPublicoNerTag, Set<String>> tags) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<ServicioPublicoNerTag, Set<String>> entry : tags.entrySet()) {
            sb.append("[" + entry.getKey() + ", " + entry.getValue() + "]");
        }
        return sb.toString();
    }


}

