package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Image;

import java.util.Optional;

public interface ImageDao {
    Image storeImage(byte[] image);

    Optional<Image> getImage(long imageId);
}
