package dto;

import nlp.ServicioPublicoNerTag;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TweetDto {
    String originalTweet;
    String tweetText;
    Map<ServicioPublicoNerTag, Set<String>> tags;

    public TweetDto() {
    }

    public TweetDto(String originalTweet, String tweetText, Map<ServicioPublicoNerTag, Set<String>> tags) {
        this.originalTweet = originalTweet;
        this.tweetText = tweetText;
        this.tags = tags;
    }

    public String getOriginalTweet() {
        return originalTweet;
    }

    public void setOriginalTweet(String originalTweet) {
        this.originalTweet = originalTweet;
    }

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
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
        TweetDto tweetDto = (TweetDto) o;
        return Objects.equals(originalTweet, tweetDto.originalTweet) &&
                Objects.equals(tweetText, tweetDto.tweetText) &&
                Objects.equals(tags, tweetDto.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalTweet, tweetText, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("originalTweet", originalTweet)
                .append("tweetText", tweetText)
                .append("tags", tags)
                .toString();
    }
}
