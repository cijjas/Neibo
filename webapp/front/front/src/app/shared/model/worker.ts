import { User } from "./user";
import { Links } from "./links";

export interface WorkerDto {
  phoneNumber: string;
  businessName: string;
  address: string;
  bio: string;
  averageRating: number;
  _links: Links;
}

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
}
