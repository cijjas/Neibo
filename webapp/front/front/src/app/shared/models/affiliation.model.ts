import { Neighborhood, Worker } from "./index";

export interface Affiliation {
  worker: Worker;
  role: string;
  neighborhood: Neighborhood;
  self: string;
}
