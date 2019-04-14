package dto;

import nlp.ServicioPublicoNerTag;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TagsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<ServicioPublicoNerTag, Set<String>> tags;

    public TagsDto(Map<ServicioPublicoNerTag, Set<String>> tags) {
        this.tags = tags;
    }

    public TagsDto() {
    }

    public Map<ServicioPublicoNerTag, Set<String>> getTags() {
        return tags;
    }

    public void setTags(Map<ServicioPublicoNerTag, Set<String>> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagsDto tagsDto = (TagsDto) o;
        return Objects.equals(tags, tagsDto.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tags);
    }

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this)
                .append("tags", tags)
                .toString();
    }
}
