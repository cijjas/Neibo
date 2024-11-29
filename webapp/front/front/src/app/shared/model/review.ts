import { User } from "./user";

export interface Review {
  rating: number;
  review: string;
  date: Date;
  user: User;
  self: string;
}
