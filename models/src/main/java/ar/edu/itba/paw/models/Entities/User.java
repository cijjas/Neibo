package ar.edu.itba.paw.models.Entities;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;

import javax.persistence.*;
import java.util.Date;
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
    @JoinColumn(name = "neighborhoodId", referencedColumnName = "neighborhoodId")
    private Neighborhood neighborhood;

    @Column(name = "darkmode")
    private Boolean darkMode;

    @Column(name = "phonenumber", length = 255)
    private String phoneNumber;

    @Column(name = "language", length = 32)
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "role", length = 64)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "creationdate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @ManyToOne
    @JoinColumn(name = "profilepictureid", referencedColumnName = "imageId")
    private Image profilePicture;

    @Column(name = "identification")
    private Integer identification;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @OneToMany(mappedBy = "user")
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;

    @ManyToMany
    @JoinTable(name = "users_availability",
            joinColumns = @JoinColumn(name = "userid"),
            inverseJoinColumns = @JoinColumn(name = "amenityavailabilityid"))
    private Set<Availability> availabilitiesTaken;

    @ManyToMany
    @JoinTable(name = "posts_users_likes", joinColumns = @JoinColumn(name = "userid"), inverseJoinColumns = @JoinColumn(name = "postid"))
    private Set<Post> likedPosts;

    @ManyToMany
    @JoinTable(name = "reviews", joinColumns = @JoinColumn(name = "userid"), inverseJoinColumns = @JoinColumn(name = "workerid"))
    private Set<Worker> reviewsGiven;

    @OneToMany(mappedBy = "seller")
    private List<Product> productsSelling;

    @ManyToMany
    @JoinTable(name = "products_users_requests", joinColumns = @JoinColumn(name = "userid"), inverseJoinColumns = @JoinColumn(name = "productid"))
    private Set<Product> requestedProducts;

    @ManyToMany
    @JoinTable(name = "products_users_inquiries", joinColumns = @JoinColumn(name = "userid"), inverseJoinColumns = @JoinColumn(name = "productid"))
    private Set<Product> inquiredProducts;

    @ManyToMany
    @JoinTable(name = "events_users", joinColumns = @JoinColumn(name = "userid"), inverseJoinColumns = @JoinColumn(name = "eventid"))
    private Set<Event> eventsSubscribed;

    User() {
    }

    private User(Builder builder) {
        this.userId = builder.userId;
        this.mail = builder.mail;
        this.name = builder.name;
        this.surname = builder.surname;
        this.password = builder.password;
        this.neighborhood = builder.neighborhood;
        this.darkMode = builder.darkMode;
        this.language = builder.language;
        this.role = builder.role;
        this.creationDate = builder.creationDate;
        this.profilePicture = builder.profilePicture;
        this.identification = builder.identification;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }

    public Boolean isDarkMode() {
        return darkMode;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Image getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Image profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Integer getIdentification() {
        return identification;
    }

    public void setIdentification(Integer identification) {
        this.identification = identification;
    }

    public Boolean getDarkMode() {
        return darkMode;
    }

    public void setDarkMode(Boolean darkMode) {
        this.darkMode = darkMode;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public Set<Availability> getAvailabilitiesTaken() {
        return availabilitiesTaken;
    }

    public void setAvailabilitiesTaken(Set<Availability> availabilitiesTaken) {
        this.availabilitiesTaken = availabilitiesTaken;
    }

    public Set<Post> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(Set<Post> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public Set<Worker> getReviewsGiven() {
        return reviewsGiven;
    }

    public void setReviewsGiven(Set<Worker> reviewsGiven) {
        this.reviewsGiven = reviewsGiven;
    }

    public List<Product> getProductsSelling() {
        return productsSelling;
    }

    public void setProductsSelling(List<Product> productsSelling) {
        this.productsSelling = productsSelling;
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

    public void setEventsSubscribed(Set<Event> eventsSubscribed) {
        this.eventsSubscribed = eventsSubscribed;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(mail, user.mail) && Objects.equals(name, user.name) && Objects.equals(surname, user.surname) && Objects.equals(password, user.password) && Objects.equals(neighborhood, user.neighborhood) && Objects.equals(darkMode, user.darkMode) && Objects.equals(phoneNumber, user.phoneNumber) && language == user.language && role == user.role && Objects.equals(creationDate, user.creationDate) && Objects.equals(profilePicture, user.profilePicture) && Objects.equals(identification, user.identification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, mail, name, surname, password, neighborhood, darkMode, phoneNumber, language, role, creationDate, profilePicture, identification);
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
            return new User(this);
        }
    }
}
