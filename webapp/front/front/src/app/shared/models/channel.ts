export enum Channel {
  ANNOUNCEMENTS = 'Announcments',
  COMPLAINTS = 'Complaints',
  FEED = 'Feed',
}

export interface ChannelDto {
  channelId: number,
  channel: Channel,
  self: string
}
