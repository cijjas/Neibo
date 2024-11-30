import { Worker } from "./worker";
import { Neighborhood } from "./neighborhood";

export interface Affiliation {
  worker: Worker;
  role: 'VERIFIED' | 'UNVERIFIED' | 'BANNED';
  neighborhoodName: string;
  self: string;
}
