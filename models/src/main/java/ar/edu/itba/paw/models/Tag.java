package ar.edu.itba.paw.models;

public class Tag {
    private final int tagId;
    private final String tag;

    private Tag(Builder builder) {
        this.tagId = builder.tagId;
        this.tag = builder.tag;
    }

    public static class Builder {
        private int tagId;
        private String tag;

        public Builder tagId(int tagId) {
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

    public int getTagId() {
        return tagId;
    }

    public String getTag() {
        return tag;
    }
}
