import { User } from "./user";
import { Event } from "./event";

export interface Attendance {
  user: User;
  event: Event;
  self: string;
}
