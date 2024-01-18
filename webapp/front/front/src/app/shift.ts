import { Amenity } from "./amenity"
import { Day } from "./day"

export interface Shift {
    shiftId: number
    amenities: Amenity[]
    day: Day
    startTime: string
    endTime: string
    taken: boolean
    self: string
}
