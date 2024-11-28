import { Links } from "./links";

export interface BookingDto {
  date: Date;
  _links: Links;
}

export interface Booking {
  date: Date;
}
