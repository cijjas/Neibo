package ar.edu.itba.paw.models.Entities;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Objects;
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

    @OneToMany(mappedBy = "channel")
    private Set<Post> posts;

    @ManyToMany
    @JoinTable(
            name = "neighborhoods_channels",
            joinColumns = @JoinColumn(name = "channelid"),
            inverseJoinColumns = @JoinColumn(name = "neighborhoodid")
    )
    private Set<Neighborhood> neighborhoods;

    Channel() {
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

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public Set<Neighborhood> getNeighborhoods() {
        return neighborhoods;
    }

    public void setNeighborhoods(Set<Neighborhood> neighborhoods) {
        this.neighborhoods = neighborhoods;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelId=" + channelId +
                ", channel='" + channel + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Channel)) return false;
        Channel channel = (Channel) o;
        return Objects.equals(channelId, channel.channelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId);
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
