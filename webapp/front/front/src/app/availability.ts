import { AmenityDto } from "./amenity"
import { ShiftDto } from "./shift"

export interface Availability {
    availabilityId: number
    amenity: AmenityDto
    shift: ShiftDto
    self: string
}

export interface AvailabilityDto {
    availabilityId: number
    amenity: string
    shift: string
    self: string
}
