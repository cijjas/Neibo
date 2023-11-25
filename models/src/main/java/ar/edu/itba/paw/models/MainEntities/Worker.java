package ar.edu.itba.paw.models.MainEntities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "workers_info")
public class Worker {
    @Id
    @Column(name = "workerid")
    private Long workerId;

    @OneToOne
    @JoinColumn(name = "workerid", referencedColumnName = "workerid")
    private User user;

    @Column(name = "phonenumber", length = 64)
    private String phoneNumber;

    @Column(name = "businessname", length = 128)
    private String businessName;

    @Column(name = "address", length = 128)
    private String address;

    @Column(name = "bio", length = 255)
    private String bio;

    @Column(name = "backgroundpictureid")
    private Long backgroundPictureId;

    @ManyToMany
    @JoinTable(name = "reviews", joinColumns = @JoinColumn(name = "workerid"), inverseJoinColumns = @JoinColumn(name = "userid"))
    private Set<User> reviewedByUsers;

    @ManyToMany
    @JoinTable(name = "workers_professions", joinColumns = @JoinColumn(name = "workerid"), inverseJoinColumns = @JoinColumn(name = "professionid"))
    private Set<Profession> professions;

    @ManyToMany
    @JoinTable(name = "workers_neighborhoods", joinColumns = @JoinColumn(name = "workerid"), inverseJoinColumns = @JoinColumn(name = "neighborhoodid"))
    private Set<Neighborhood> workNeighborhoods;

    Worker() {
    }

    public Worker(Builder builder) {
        this.workerId = builder.workerId;
        this.user = builder.user;
        this.phoneNumber = builder.phoneNumber;
        this.businessName = builder.businessName;
        this.address = builder.address;
        this.bio = builder.bio;
        this.backgroundPictureId = builder.backgroundPictureId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Long getBackgroundPictureId() {
        return backgroundPictureId;
    }

    public void setBackgroundPictureId(Long backgroundPictureId) {
        this.backgroundPictureId = backgroundPictureId;
    }

    public Set<User> getReviewedByUsers() {
        return reviewedByUsers;
    }

    public void setReviewedByUsers(Set<User> reviewedByUsers) {
        this.reviewedByUsers = reviewedByUsers;
    }

    public Set<Profession> getProfessions() {
        return professions;
    }

    public void setProfessions(Set<Profession> professions) {
        this.professions = professions;
    }

    public List<String> getProfessionsAsStrings() {
        List<String> professionsList = new ArrayList<String>();
        for (Profession profession : professions) {
            professionsList.add(profession.getProfession().toString());
        }
        return professionsList;
    }

    public Set<Neighborhood> getWorkNeighborhoods() {
        return workNeighborhoods;
    }

    public void setWorkNeighborhoods(Set<Neighborhood> workNeighborhoods) {
        this.workNeighborhoods = workNeighborhoods;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Worker{" +
//                "user=" + user +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", businessName='" + businessName + '\'' +
                ", address='" + address + '\'' +
                ", bio='" + bio + '\'' +
                '}';
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public static class Builder {
        private Long workerId;
        private User user;
        private String phoneNumber;
        private String businessName;
        private String address;
        private String bio;
        private Long backgroundPictureId;

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder businessName(String businessName) {
            this.businessName = businessName;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder backgroundPictureId(Long backgroundPictureId) {
            this.backgroundPictureId = backgroundPictureId;
            return this;
        }

        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder workerId(Long workerId) {
            this.workerId = workerId;
            return this;
        }

        public Worker build() {
            return new Worker(this);
        }
    }
}
