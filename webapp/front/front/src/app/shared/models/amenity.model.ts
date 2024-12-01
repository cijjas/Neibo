import { Shift } from "./index";

export interface Amenity {
  name: string;
  description: string;
  availableShifts: Shift[];
  self: string;
}
