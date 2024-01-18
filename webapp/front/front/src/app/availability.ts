import {Amenity} from "./amenity"
import {Shift} from "./shift"

export interface Availability {
    availabilityId: number
    amenity: Amenity
    shift: Shift
    self: string
}
