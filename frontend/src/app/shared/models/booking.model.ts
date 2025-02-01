import { Amenity, Shift } from './index';

export interface Booking {
  shift: Shift;
  amenity: Amenity;
  bookingDate: string;
  self: string;
}
