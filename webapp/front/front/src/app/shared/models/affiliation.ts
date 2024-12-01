import { Worker } from "./worker";
import { Neighborhood } from "./neighborhood";

export interface Affiliation {
  worker: Worker;
  role: string;
  neighborhoodName: string;
  self: string;
}
