export enum Channel {
  ANNOUNCEMENTS = 'ANNOUNCEMENTS',
  COMPLAINTS = 'COMPLAINTS',
  FEED = 'FEED',
  WORKERS = 'WORKERS',
  RESERVATIONS = 'RESERVATIONS',
  INFORMATION = 'INFORMATION',
}

export namespace Channel {
  export function toString(channel: Channel): string {
    switch (channel) {
      case Channel.ANNOUNCEMENTS:
        return 'Announcements';
      case Channel.COMPLAINTS:
        return 'Complaints';
      case Channel.FEED:
        return 'Feed';
      case Channel.WORKERS:
        return 'Workers';
      case Channel.RESERVATIONS:
        return 'Reservations';
      case Channel.INFORMATION:
        return 'Information';
      default:
        return 'Unknown Channel';
    }
  }
}
