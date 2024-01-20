import { AmenityDto } from "./amenity"
import { Day } from "./day"

export interface Shift {
    shiftId: number
    amenities: AmenityDto[]
    day: Day
    startTime: string
    endTime: string
    taken: boolean
    self: string
}

export interface ShiftDto {
    shiftId: number
    amenities: string
    day: string
    startTime: string
    endTime: string
    taken: boolean
    self: string
}

