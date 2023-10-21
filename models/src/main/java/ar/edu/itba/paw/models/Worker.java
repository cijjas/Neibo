package ar.edu.itba.paw.models;

public class Worker {

    private final User user;
    private final String phoneNumber;
    private final String businessName;
    private final String address;
    private final String bio;
    private final Long backgroundPictureId;


    private Worker(Builder builder) {
        this.user = builder.user;
        this.phoneNumber = builder.phoneNumber;
        this.businessName = builder.businessName;
        this.address = builder.address;
        this.bio = builder.bio;
        this.backgroundPictureId = builder.backgroundPictureId;
    }

    public User getUser() {
        return user;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getBio() {
        return bio;
    }

    public Long getBackgroundPictureId() {
        return backgroundPictureId;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "user=" + user +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", businessName='" + businessName + '\'' +
                ", address='" + address + '\'' +
                ", bio='" + bio + '\'' +
                '}';
    }

    public static class Builder {
        private User user;
        private String phoneNumber;
        private String businessName;
        private String address;
        private String bio;
        private Long backgroundPictureId;

        public Builder user(User user) {
            this.user = user;
            return this;
        }

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

        public Worker build() {
            return new Worker(this);
        }
    }
}
