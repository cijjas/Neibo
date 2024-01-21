package ar.edu.itba.paw.services;

public class PaginationUtils {
    public static int calculatePages(int totalItems, int size) {
        return (int) Math.ceil((double) totalItems / size);
    }
}
