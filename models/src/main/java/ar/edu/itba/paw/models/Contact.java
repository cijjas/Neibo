package ar.edu.itba.paw.models;

public class Contact {
    private final long contactId;
    private final String contactName;
    private final String contactAddress;
    private final String contactPhone;
    private final long neighborhoodId;

    private Contact(Builder builder) {
        this.contactId = builder.contactId;
        this.contactName = builder.contactName;
        this.contactAddress = builder.contactAddress;
        this.contactPhone = builder.contactPhone;
        this.neighborhoodId = builder.neighborhoodId;
    }

    public long getContactId() {
        return contactId;
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
                "contactId=" + contactId +
                "contactName='" + contactName + '\'' +
                ", contactAddress='" + contactAddress + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                '}';
    }

    public static class Builder {
        private long contactId;
        private String contactName;
        private String contactAddress;
        private String contactPhone;
        private long neighborhoodId;

        public Builder contactId(long contactId) {
            this.contactId = contactId;
            return this;
        }

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
}
