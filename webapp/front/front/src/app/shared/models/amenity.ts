import { Shift } from "./shift";

export interface Amenity {
  name: string;
  description: string;
  availableShifts: Shift[];
  self: string;
}
