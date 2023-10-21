package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "channels")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "channels_channelid_seq")
    @SequenceGenerator(sequenceName = "channels_channelid_seq", name = "channels_channelid_seq", allocationSize = 1)
    private Long channelId;

    @Column(name = "channel", length = 64, unique = true, nullable = false)
    private String channel;
/*

    @ManyToMany
    @JoinTable(name = "neighborhoods_channels",
            joinColumns = @JoinColumn(name = "channelid"),
            inverseJoinColumns = @JoinColumn(name = "neighborhoodid"))
    private Set<Neighborhood> neighborhoods;
*/


    public Channel() {
        // Default no-argument constructor is required for Hibernate
    }

    private Channel(Builder builder) {
        this.channelId = builder.channelId;
        this.channel = builder.channel;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getChannelId() {
        return channelId;
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelId=" + channelId +
                ", channel='" + channel + '\'' +
                '}';
    }

    public static class Builder {
        private Long channelId;
        private String channel;

        public Builder channelId(Long channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public Channel build() {
            return new Channel(this);
        }
    }
}
