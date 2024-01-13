import {User} from "./user";
import {Worker} from "./worker";

export interface Review {
  reviewId: number;
  rating: number;
  review: string;
  date: Date;
  worker: Worker;
  user: User;
  self: string;
}
