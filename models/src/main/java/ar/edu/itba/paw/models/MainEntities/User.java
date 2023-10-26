package ar.edu.itba.paw.models.MainEntities;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.models.JunctionEntities.Booking;
import ar.edu.itba.paw.models.MainEntities.Image;
import ar.edu.itba.paw.models.MainEntities.Neighborhood;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "channels_channelid_seq")
    @SequenceGenerator(sequenceName = "channels_channelid_seq", name = "channels_channelid_seq", allocationSize = 1)
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
    private boolean darkMode;

    @Column(name = "language", length = 32)
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "role", length = 64)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "creationdate", nullable = false)
    private Date creationDate;

    @ManyToOne
    @JoinColumn(name = "profilepictureid", referencedColumnName = "imageId")
    private Image profilePicture;

    @Column(name = "identification")
    private int identification;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "users_availability",
            joinColumns = @JoinColumn(name = "userid"),
            inverseJoinColumns = @JoinColumn(name = "amenityavailabilityid"))
    private Set<Booking> bookings;

    public User() {}

    private User(Long userId, String mail, String name, String surname, String password, Neighborhood neighborhood, boolean darkMode, Language language, UserRole role, Date creationDate, Image profilePicture, int identification) {
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

    public boolean isDarkMode() {
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

    public int getIdentification() {
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
                ", profilePicture=" + profilePicture +
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
        private boolean darkMode;
        private Language language;
        private UserRole role;
        private Date creationDate;
        private Image profilePicture;
        private int identification;

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

        public Builder darkMode(boolean darkMode) {
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

        public Builder identification(int identification) {
            this.identification = identification;
            return this;
        }

        public User build() {
            return new User(this.userId, this.mail, this.name, this.surname, this.password, this.neighborhood, this.darkMode, this.language, this.role, this.creationDate, this.profilePicture, this.identification);
        }
    }
}
