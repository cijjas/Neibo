import { User } from "./user";

export interface Comment {
  comment: string;
  date: Date;
  author: User;
  self: string;
}
