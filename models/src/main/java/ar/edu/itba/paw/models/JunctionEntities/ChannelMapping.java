package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.models.MainEntities.Channel;
import ar.edu.itba.paw.models.MainEntities.Neighborhood;
import ar.edu.itba.paw.models.compositeKeys.ChannelMappingKey;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "neighborhoods_channels")
public class ChannelMapping implements Serializable {
    @EmbeddedId
    private ChannelMappingKey id;

    @ManyToOne
    @MapsId("neighborhoodId")
    @JoinColumn(name = "neighborhoodid")
    private Neighborhood neighborhood;

    @ManyToOne
    @MapsId("channelId")
    @JoinColumn(name = "channelid")
    private Channel channel;

    public ChannelMapping() {
        this.id = new ChannelMappingKey();
    }

    public ChannelMapping(Neighborhood neighborhood, Channel channel) {
        this.id = new ChannelMappingKey(neighborhood.getNeighborhoodId(), channel.getChannelId());
        this.neighborhood = neighborhood;
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "ChannelMapping{" +
                "id=" + id +
                ", neighborhood=" + neighborhood +
                ", channel=" + channel +
                '}';
    }

    public ChannelMappingKey getId() {
        return id;
    }

    public void setId(ChannelMappingKey id) {
        this.id = id;
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChannelMapping)) return false;
        ChannelMapping that = (ChannelMapping) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
