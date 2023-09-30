package ar.edu.itba.paw.models;

public class Contact {
    private final String contactName;
    private final String contactAddress;
    private final String contactPhone;
    private final long neighborhoodId;

    private Contact(Builder builder) {
        this.contactName = builder.contactName;
        this.contactAddress = builder.contactAddress;
        this.contactPhone = builder.contactPhone;
        this.neighborhoodId = builder.neighborhoodId;
    }

    public static class Builder {
        private String contactName;
        private String contactAddress;
        private String contactPhone;
        private long neighborhoodId;

        public Builder contactName(String contactName) {
            this.contactName = contactName;
            return this;
        }

        public Builder contactAddress(String contactAddress) {
            this.contactAddress = contactAddress;
            return this;
        }

        public Builder contactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
            return this;
        }

        public Builder neighborhoodId(long neighborhoodId) {
            this.neighborhoodId = neighborhoodId;
            return this;
        }

        public Contact build() {
            return new Contact(this);
        }
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public long getNeighborhoodId() {
        return neighborhoodId;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contactName='" + contactName + '\'' +
                ", contactAddress='" + contactAddress + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                '}';
    }
}
