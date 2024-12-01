import { User } from "./index";

export interface Review {
  rating: number;
  message: string;
  createdAt: Date;
  user: User;
  self: string;
}
