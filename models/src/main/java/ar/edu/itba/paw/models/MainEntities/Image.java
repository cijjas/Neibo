package ar.edu.itba.paw.models.MainEntities;


public class Image {
    private final Long imageId;
    private final byte[] image;

    private Image(Builder builder) {
        this.imageId = builder.imageId;
        this.image = builder.image;
    }

    public Long getImageId() {
        return imageId;
    }

    public byte[] getImage() {
        return image;
    }

    public static class Builder {
        private Long imageId;
        private byte[] image;

        public Builder imageId(Long imageId) {
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
}
