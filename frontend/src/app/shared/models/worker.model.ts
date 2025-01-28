import { Profession, User } from "./index";

export interface Worker {
  phoneNumber: string;
  businessName: string;
  address: string;
  bio: string;
  averageRating: number;
  user: User;
  reviews: string;
  totalReviews: number;
  totalPosts: number;
  posts: string;
  backgroundImage: string;
  neighborhoodAffiliated: string[];
  professions: Profession[];
  self: string;
}
