import {Amenity} from "./amenity";
import {User} from "./user";
import {Shift} from "./shift";

export interface Availability {
    availabilityId: number;
    amenity: Amenity;
    shift: Shift;
    self: string;
}
