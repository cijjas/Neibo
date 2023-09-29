package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Image;

import java.util.Optional;

public interface ImageService {
    Image storeImage(byte[] image);

    Optional<Image> getImage(long imageId);
}
