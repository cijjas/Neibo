package ar.edu.itba.paw.models.MainEntities;

public class Tag {
    private final Long tagId;
    private final String tag;

    private Tag(Builder builder) {
        this.tagId = builder.tagId;
        this.tag = builder.tag;
    }

    public Long getTagId() {
        return tagId;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tagId=" + tagId +
                ", tag='" + tag + '\'' +
                '}';
    }

    public static class Builder {
        private Long tagId;
        private String tag;

        public Builder tagId(Long tagId) {
            this.tagId = tagId;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Tag build() {
            return new Tag(this);
        }
    }
}
