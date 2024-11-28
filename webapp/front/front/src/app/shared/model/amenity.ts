import { Shift } from "./shift";
import { Links } from "./links";

export interface AmenityDto {
  name: string;
  description: string;
  _links: Links;
}

export interface Amenity {
  name: string;
  description: string;
  shiftsAvailable: Shift[];
}
