import { User } from "./user";

export interface Worker {
  phoneNumber: string;
  businessName: string;
  address: string;
  bio: string;
  averageRating: number;
  user: User;
  backgroundImage: Uint8Array;
  neighborhoodAffiliated: string[];
  professions: string[];
  self: string;
}
