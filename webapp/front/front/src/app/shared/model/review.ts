import { User } from "./user";
import { Links } from "./links";

export interface ReviewDto {
  rating: number;
  review: string;
  date: Date;
  _links: Links;
}

export interface Review {
  rating: number;
  review: string;
  date: Date;
  user: User;
}
