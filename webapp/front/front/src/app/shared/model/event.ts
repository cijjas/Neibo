import { Links } from "./links";

export interface EventDto {
  name: string;
  description: string;
  date: Date;
  startTime: string;
  endTime: string;
  _links: Links;
}

export interface Event {
  name: string;
  description: string;
  date: Date;
  startTime: string;
  endTime: string;
  attendeesCount: number;
}
