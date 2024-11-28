import { AmenityDto } from "./amenity"
import { Day } from "./day"
import { Links } from "./links"

export interface Shift {
    //amenities: AmenityDto[]
    day: Day
    startTime: string
    self: string
}

export interface ShiftDto {
    day: string
    startTime: string
    isBooked: boolean
    _links: Links
}
