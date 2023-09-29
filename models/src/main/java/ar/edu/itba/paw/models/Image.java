package ar.edu.itba.paw.models;


public class Image {
    private final long imageId;
    private final byte[] image;

    private Image(Builder builder) {
        this.imageId = builder.imageId;
        this.image = builder.image;
    }

    public static class Builder {
        private long imageId;
        private byte[] image;

        public Builder imageId(long imageId) {
            this.imageId = imageId;
            return this;
        }

        public Builder image(byte[] image) {
            this.image = image;
            return this;
        }

        public Image build() {
            return new Image(this);
        }
    }

    public long getImageId() {
        return imageId;
    }

    public byte[] getImage() {
        return image;
    }
}
