export interface Event {
  name: string;
  description: string;
  eventDate: Date;
  startTime: string;
  endTime: string;
  duration: number;
  attendees: string;
  attendeesCount: number;
  self: string;
}
