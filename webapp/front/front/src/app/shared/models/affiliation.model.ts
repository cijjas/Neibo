import { Worker } from "./index";

export interface Affiliation {
  worker: Worker;
  role: string;
  neighborhoodName: string;
  self: string;
}
