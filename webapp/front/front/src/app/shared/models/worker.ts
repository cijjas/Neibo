import { User } from "./user";

export interface Worker {
  phoneNumber: string;
  businessName: string;
  address: string;
  bio: string;
  averageRating: number;
  user: User;
  backgroundImage: string;
  neighborhoodAffiliated: string[];
  professions: string[];
  self: string;
}
