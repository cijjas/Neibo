import { AmenityDto } from "./amenity"
import { Links } from "./links"

export interface Shift {
    //amenities: AmenityDto[]
    startTime: string
    self: string
}

export interface ShiftDto {
    day: string
    startTime: string
    isBooked: boolean
    _links: Links
}
