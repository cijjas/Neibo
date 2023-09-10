package ar.edu.itba.paw.models;

public class NeighborPostNeighborhoodWrapper {
    private Neighbor neighbor;
    private Neighborhood neighborhood;
    private Post post;

    public NeighborPostNeighborhoodWrapper() {
        this.neighbor = new Neighbor.Builder().build(); // Initialize Neighbor object
        this.post = new Post.Builder().build(); // Initialize Post object
        this.neighborhood = new Neighborhood.Builder().build(); // Initialize Neighborhood object
    }

    public Neighbor getNeighbor() {
        return neighbor;
    }

    public void setNeighbor(Neighbor neighbor) {
        this.neighbor = neighbor;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }

    @Override
    public String toString() {
        return "NeighborPostNeighborhoodWrapper{" +
                "neighbor=" + neighbor +
                ", neighborhood=" + neighborhood +
                ", post=" + post +
                '}';
    }
}
