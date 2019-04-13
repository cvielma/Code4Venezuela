import java.util.Optional;

public enum ServicioPublicoNerTag {

    SICK("SICK"),
    NEEDS("NEEDS"),
    OFFERS("OFFERS"),
    MED("MED"),
    LOC("LOC"),
    CONTACT("CONTACT"),
    BNAME("B-NAME"),
    INAME("I-NAME");

    private final String tag;

    ServicioPublicoNerTag(String s) {
        this.tag = s;
    }

    public static Optional<ServicioPublicoNerTag> getTag(String s) {
        for (ServicioPublicoNerTag tag : ServicioPublicoNerTag.values()) {
            if (tag.tag.equals(s)) {
                return Optional.of(tag);
            }
        }
        return Optional.empty();
    }

}
