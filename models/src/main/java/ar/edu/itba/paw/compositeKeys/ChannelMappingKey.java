package ar.edu.itba.paw.compositeKeys;

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

    @Override
    public String toString() {
        return "ChannelMappingKey{" +
                "neighborhoodId=" + neighborhoodId +
                ", channelId=" + channelId +
                '}';
    }

    public void setNeighborhoodId(Long neighborhoodId) {
        this.neighborhoodId = neighborhoodId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getNeighborhoodId() {
        return neighborhoodId;
    }

    public Long getChannelId() {
        return channelId;
    }
}

