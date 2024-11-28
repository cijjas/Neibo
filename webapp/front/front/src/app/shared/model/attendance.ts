import { User } from "./user";
import { Event } from "./event";
import { Links } from "./links";

export interface AttendanceDto {
  _links: Links;
}

export interface Attendance {
  user: User;
  event: Event;
}
