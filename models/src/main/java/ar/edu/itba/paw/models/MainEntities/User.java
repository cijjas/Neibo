package ar.edu.itba.paw.models.MainEntities;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.models.JunctionEntities.Booking;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_userid_seq")
    @SequenceGenerator(sequenceName = "users_userid_seq", name = "users_userid_seq", allocationSize = 1)
    private Long userId;

    @Column(name = "mail", length = 128, unique = true, nullable = false)
    private String mail;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "surname", length = 64, nullable = false)
    private String surname;

    @Column(name = "password", length = 128)
    private String password;

    @ManyToOne
    @JoinColumn(name = "neighborhoodid", referencedColumnName = "neighborhoodId")
    private Neighborhood neighborhood;

    @Column(name = "darkmode")
    private Boolean darkMode;

    @Column(name = "language", length = 32)
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "role", length = 64)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "creationdate", nullable = false)
    private Date creationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profilepictureid", referencedColumnName = "imageId")
    private Image profilePicture;

    @Column(name = "identification")
    private Integer identification;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "users_availability",
            joinColumns = @JoinColumn(name = "userid"),
            inverseJoinColumns = @JoinColumn(name = "amenityavailabilityid"))
    private Set<Booking> bookings;

    @ManyToMany
    @JoinTable(name = "posts_users_subscriptions", joinColumns = @JoinColumn(name = "userid"), inverseJoinColumns = @JoinColumn(name = "postid"))
    private Set<Post> subscribedPosts;

    @ManyToMany
    @JoinTable(name = "posts_users_likes", joinColumns = @JoinColumn(name = "userid"), inverseJoinColumns = @JoinColumn(name = "postid"))
    private Set<Post> likedPosts;

    @ManyToMany
    @JoinTable(name = "reviews", joinColumns = @JoinColumn(name = "userid"), inverseJoinColumns = @JoinColumn(name = "workerid"))
    private Set<Worker> reviewsGiven;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL) //cambiar a seller y tmb en Product??
    private List<Product> productsSelling;

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Product> productsBought;

    @ManyToMany
    @JoinTable(name = "products_users_requests", joinColumns = @JoinColumn(name = "userid"), inverseJoinColumns = @JoinColumn(name = "productid"))
    private Set<Product> requestedProducts;

    @ManyToMany
    @JoinTable(name = "products_users_inquiries", joinColumns = @JoinColumn(name = "userid"), inverseJoinColumns = @JoinColumn(name = "productid"))
    private Set<Product> inquiredProducts;

    @ManyToMany
    @JoinTable(name = "events_users", joinColumns = @JoinColumn(name = "userid"), inverseJoinColumns = @JoinColumn(name = "eventid"))
    private Set<Event> eventsSubscribed;

//    @OneToOne
//    @JoinColumn(name = "workerid", referencedColumnName = "workerid")
//    private Worker worker;

    public User() {}

    private User(Long userId, String mail, String name, String surname, String password, Neighborhood neighborhood, Boolean darkMode, Language language, UserRole role, Date creationDate, Image profilePicture, Integer identification) {
        this.userId = userId;
        this.mail = mail;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.neighborhood = neighborhood;
        this.darkMode = darkMode;
        this.language = language;
        this.role = role;
        this.creationDate = new Date(System.currentTimeMillis());
        this.profilePicture = profilePicture;
        this.identification = identification;
    }

    public Long getUserId() {
        return userId;
    }

    public String getMail() {
        return mail;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPassword() {
        return password;
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public Boolean isDarkMode() {
        return darkMode;
    }

    public Language getLanguage() {
        return language;
    }

    public UserRole getRole() {
        return role;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Image getProfilePicture() {
        return profilePicture;
    }

    public Integer getIdentification() {
        return identification;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", mail='" + mail + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", password='" + password + '\'' +
                ", neighborhood=" + neighborhood +
                ", darkMode=" + darkMode +
                ", language=" + language +
                ", role=" + role +
                ", creationDate=" + creationDate +
                ", identification=" + identification +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User otherUser = (User) o;
        return userId.equals(otherUser.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    public static class Builder {
        private Long userId;
        private String mail;
        private String name;
        private String surname;
        private String password;
        private Neighborhood neighborhood;
        private Boolean darkMode;
        private Language language;
        private UserRole role;
        private Date creationDate;
        private Image profilePicture;
        private Integer identification;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder mail(String mail) {
            this.mail = mail;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder neighborhood(Neighborhood neighborhood) {
            this.neighborhood = neighborhood;
            return this;
        }

        public Builder darkMode(Boolean darkMode) {
            this.darkMode = darkMode;
            return this;
        }

        public Builder language(Language language) {
            this.language = language;
            return this;
        }

        public Builder role(UserRole role) {
            this.role = role;
            return this;
        }

        public Builder creationDate(Date creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder profilePicture(Image profilePicture) {
            this.profilePicture = profilePicture;
            return this;
        }

        public Builder identification(Integer identification) {
            this.identification = identification;
            return this;
        }

        public User build() {
            return new User(this.userId, this.mail, this.name, this.surname, this.password, this.neighborhood, this.darkMode, this.language, this.role, this.creationDate, this.profilePicture, this.identification);
        }
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNeighborhood(Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }

    public void setDarkMode(Boolean darkMode) {
        this.darkMode = darkMode;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setProfilePicture(Image profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setIdentification(Integer identification) {
        this.identification = identification;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

    public void setSubscribedPosts(Set<Post> subscribedPosts) {
        this.subscribedPosts = subscribedPosts;
    }

    public void setLikedPosts(Set<Post> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public Boolean getDarkMode() {
        return darkMode;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public Set<Post> getSubscribedPosts() {
        return subscribedPosts;
    }

    public Set<Post> getLikedPosts() {
        return likedPosts;
    }

    public Set<Worker> getReviews() {
        return reviewsGiven;
    }

    public void setReviews(Set<Worker> reviewsGiven) {
        this.reviewsGiven = reviewsGiven;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public void setReviewsGiven(Set<Worker> reviewsGiven) {
        this.reviewsGiven = reviewsGiven;
    }

    public Set<Worker> getReviewsGiven() {
        return reviewsGiven;
    }

    public List<Product> getProductsSelling() {
        return productsSelling;
    }

    public void setProductsSelling(List<Product> productsSelling) {
        this.productsSelling = productsSelling;
    }

    public List<Product> getProductsBought() {
        return productsBought;
    }

    public void setProductsBought(List<Product> productsBought) {
        this.productsBought = productsBought;
    }

    public Set<Product> getRequestedProducts() {
        return requestedProducts;
    }

    public void setRequestedProducts(Set<Product> requestedProducts) {
        this.requestedProducts = requestedProducts;
    }

    public Set<Product> getInquiredProducts() {
        return inquiredProducts;
    }

    public void setInquiredProducts(Set<Product> inquiredProducts) {
        this.inquiredProducts = inquiredProducts;
    }

    public Set<Event> getEventsSubscribed() {
        return eventsSubscribed;
    }

    public void setEventsSubscribed(Set<Event> events) {
        this.eventsSubscribed = events;
    }

    //    public Worker getWorker() {
//        return worker;
//    }
//
//    public void setWorker(Worker worker) {
//        this.worker = worker;
//    }
}
