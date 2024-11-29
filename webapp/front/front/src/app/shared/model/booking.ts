import { Amenity } from "./amenity";
import { Shift } from "./shift";

export interface Booking {
  shift: Shift;
  amenity: Amenity;
  date: Date;
  self: string;
}
