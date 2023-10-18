package ar.edu.itba.paw.models;

public class Channel {
    private final long channelId;
    private final String channel;

    private Channel(Builder builder) {
        this.channelId = builder.channelId;
        this.channel = builder.channel;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public long getChannelId() {
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
        private long channelId;
        private String channel;

        public Builder channelId(long channelId) {
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
