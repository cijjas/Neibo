package ar.edu.itba.paw.models.MainEntities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "images_imageid_seq")
    @SequenceGenerator(name = "images_imageid_seq", sequenceName = "images_imageid_seq", allocationSize = 1)
    @Column(name = "imageid")
    private Long imageId;

    @Column(name = "image", columnDefinition = "bytea", nullable = false)
    private byte[] image;

    @OneToMany(mappedBy = "image")
    private Set<Resource> resources = new HashSet<>();

    @OneToMany(mappedBy = "profilePicture")
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "postPicture")
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "primaryPicture")
    private Set<Product> productsWithThisImageAsPrimary = new HashSet<>();

    @OneToMany(mappedBy = "secondaryPicture")
    private Set<Product> productsWithThisImageAsSecondary = new HashSet<>();

    @OneToMany(mappedBy = "tertiaryPicture")
    private Set<Product> productsWithThisImageAsTertiary = new HashSet<>();

    Image() {}

    public Image(Long imageId, byte[] image) {
        this.imageId = imageId;
        this.image = image;
    }

    public Long getImageId() {
        return imageId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Image{" +
                "imageId=" + imageId +
                ", image=[BLOB]" +  // Representing a byte array
                '}';
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
