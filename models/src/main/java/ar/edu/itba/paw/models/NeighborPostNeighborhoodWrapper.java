package ar.edu.itba.paw.models;

public class NeighborPostNeighborhoodWrapper {
    private User user;
    private Neighborhood neighborhood;
    private Post post;

    public NeighborPostNeighborhoodWrapper() {
        this.user = new User.Builder().build();
        this.post = new Post.Builder().build();
        this.neighborhood = new Neighborhood.Builder().build();
    }

    public User getNeighbor() {
        return user;
    }

    public void setNeighbor(User user) {
        this.user = user;
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
                "neighbor=" + user +
                ", neighborhood=" + neighborhood +
                ", post=" + post +
                '}';
    }
}
