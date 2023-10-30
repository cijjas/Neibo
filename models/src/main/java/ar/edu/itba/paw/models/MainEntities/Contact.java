package ar.edu.itba.paw.models.MainEntities;

import javax.persistence.*;

@Entity
@Table(name = "contacts")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contacts_contactid_seq")
    @SequenceGenerator(sequenceName = "contacts_contactid_seq", name = "contacts_contactid_seq", allocationSize = 1)
    @Column(name = "contactid")
    private Long contactId;

    @Column(name = "contactname", length = 64, nullable = false)
    private String contactName;

    @Column(name = "contactaddress", length = 128)
    private String contactAddress;

    @Column(name = "contactphone", length = 32)
    private String contactPhone;

    @ManyToOne
    @JoinColumn(name = "neighborhoodid", referencedColumnName = "neighborhoodid")
    private Neighborhood neighborhood;

    public Contact() {
    }

    public Contact(Builder builder) {
        this.contactId = builder.contactId;
        this.contactName = builder.contactName;
        this.contactAddress = builder.contactAddress;
        this.contactPhone = builder.contactPhone;
        this.neighborhood = builder.neighborhood;
    }

    public Long getContactId() {
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

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contactId=" + contactId +
                ", contactName='" + contactName + '\'' +
                ", contactAddress='" + contactAddress + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", neighborhood=" + neighborhood +
                '}';
    }

    public static class Builder {
        private Long contactId;
        private String contactName;
        private String contactAddress;
        private String contactPhone;
        private Neighborhood neighborhood;

        public Builder contactId(Long contactId) {
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

        public Builder neighborhood(Neighborhood neighborhood) {
            this.neighborhood = neighborhood;
            return this;
        }

        public Contact build() {
            return new Contact(this);
        }
    }
}

