import { User } from "./index";

export interface Comment {
  message: string;
  createdAt: Date;
  user: User;
  humanReadableDate: string;
  self: string;
}
