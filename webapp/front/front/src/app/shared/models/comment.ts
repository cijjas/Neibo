import { User } from "./user";

export interface Comment {
  message: string;
  createdAt: Date;
  user: User;
  humanReadableDate: string;
  self: string;
}
