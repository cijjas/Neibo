import { User, Event } from "./index";

export interface Attendance {
  user: User;
  event: Event;
  self: string;
}
