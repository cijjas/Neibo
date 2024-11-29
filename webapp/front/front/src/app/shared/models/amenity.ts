import { Shift } from "./shift";

export interface Amenity {
  name: string;
  description: string;
  shiftsAvailable: Shift[];
  self: string;
}
