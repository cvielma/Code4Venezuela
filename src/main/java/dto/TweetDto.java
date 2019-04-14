package dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Objects;

public class TweetDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String originalTweet;
    private String tweetText;
    private TagsDto tags;

    public TweetDto() {
    }

    public TweetDto(String originalTweet, String tweetText, TagsDto tags) {
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

    public TagsDto getTags() {
        return tags;
    }

    public void setTags(TagsDto tags) {
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
