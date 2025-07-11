package ar.edu.itba.paw.models.compositeKeys;

import java.io.Serializable;
import java.util.Objects;

public class ChannelMappingKey implements Serializable {
    private Long neighborhoodId;
    private Long channelId;

    public ChannelMappingKey() {
    }

    public ChannelMappingKey(Long neighborhoodId, Long channelId) {
        this.neighborhoodId = neighborhoodId;
        this.channelId = channelId;
    }

    public Long getNeighborhoodId() {
        return neighborhoodId;
    }

    public void setNeighborhoodId(Long neighborhoodId) {
        this.neighborhoodId = neighborhoodId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChannelMappingKey)) return false;
        ChannelMappingKey that = (ChannelMappingKey) o;
        return Objects.equals(neighborhoodId, that.neighborhoodId) && Objects.equals(channelId, that.channelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(neighborhoodId, channelId);
    }
}

