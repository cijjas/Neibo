import {AmenityForm} from "./amenityForm"
import {ShiftForm} from "./shiftForm"

export interface AvailabilityForm {
    availabilityId: number
    amenity: AmenityForm
    shift: ShiftForm
    self: string
}
