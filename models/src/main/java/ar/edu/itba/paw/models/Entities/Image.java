package ar.edu.itba.paw.models.Entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "images")
public class Image {
    @OneToMany(mappedBy = "image")
    private final Set<Resource> resources = new HashSet<>();

    @OneToMany(mappedBy = "profilePicture")
    private final Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "postPicture")
    private final Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "primaryPicture")
    private final Set<Product> productsWithThisImageAsPrimary = new HashSet<>();

    @OneToMany(mappedBy = "secondaryPicture")
    private final Set<Product> productsWithThisImageAsSecondary = new HashSet<>();

    @OneToMany(mappedBy = "tertiaryPicture")
    private final Set<Product> productsWithThisImageAsTertiary = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "images_imageid_seq")
    @SequenceGenerator(name = "images_imageid_seq", sequenceName = "images_imageid_seq", allocationSize = 1)
    @Column(name = "imageid")
    private Long imageId;

    @Column(name = "image", columnDefinition = "bytea", nullable = false)
    private byte[] image;

    Image() {
    }

    public Image(Long imageId, byte[] image) {
        this.imageId = imageId;
        this.image = image;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Image)) return false;
        Image image = (Image) o;
        return Objects.equals(imageId, image.imageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageId);
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
            return new Image(this.imageId, this.image);
        }
    }
}
