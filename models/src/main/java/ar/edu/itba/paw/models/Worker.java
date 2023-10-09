package ar.edu.itba.paw.models;

public class Worker {

    private final User user;
    private final String phoneNumber;
    private final String businessName;
    private final String address;
    private final long backgroundPictureId;


    private Worker(Builder builder) {
        this.user = builder.user;
        this.phoneNumber = builder.phoneNumber;
        this.businessName = builder.businessName;
        this.address = builder.address;
        this.backgroundPictureId = builder.backgroundPictureId;
    }

    public static class Builder {
        private User user;
        private String phoneNumber;
        private String businessName;
        private String address;
        private long backgroundPictureId;

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

        public Builder backgroundPictureId(long backgroundPictureId) {
            this.backgroundPictureId = backgroundPictureId;
            return this;
        }

        public Worker build() {
            return new Worker(this);
        }
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

    @Override
    public String toString() {
        return "Worker{" +
                "user=" + user +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", businessName='" + businessName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
