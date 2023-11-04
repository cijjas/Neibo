package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.models.compositeKeys.ChannelMappingKey;
import ar.edu.itba.paw.models.MainEntities.Channel;
import ar.edu.itba.paw.models.MainEntities.Neighborhood;

import javax.persistence.*;
import java.io.Serializable;

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

    public void setId(ChannelMappingKey id) {
        this.id = id;
    }

    public void setNeighborhood(Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ChannelMappingKey getId() {
        return id;
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelMapping that = (ChannelMapping) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
