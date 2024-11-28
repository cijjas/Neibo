import { Amenity } from "./amenity";
import { Links } from "./links";
import { Shift } from "./shift";

export interface BookingDto {
  date: Date;
  _links: Links;
}

export interface Booking {
  shift: Shift;
  amenity: Amenity;
  date: Date;
}
