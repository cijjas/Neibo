export interface Event {
  name: string;
  description: string;
  eventDate: Date;
  startTime: string;
  endTime: string;
  duration: number;
  attendeesCount: number;
  self: string;
}
