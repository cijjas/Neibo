package ar.edu.itba.paw.models;

public class Channel {
    private final long channelId;
    private final String channel;

    private Channel(Builder builder) {
        this.channelId = builder.channelId;
        this.channel = builder.channel;
    }

    public long getChannelId() {
        return channelId;
    }

    public String getChannel() {
        return channel;
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

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelId=" + channelId +
                ", channel='" + channel + '\'' +
                '}';
    }
}
