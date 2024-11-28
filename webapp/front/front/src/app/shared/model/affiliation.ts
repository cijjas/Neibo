import { Worker } from "./worker";
import { Neighborhood } from "./neighborhood";
import { Links } from "./links";

export interface AffiliationDto {
  workerRole: String;
  _links: Links;
}

export interface Affiliation {
  worker: Worker;
  workerRole: 'VERIFIED' | 'UNVERIFIED' | 'BANNED';
  neighborhoodName: string;
}
