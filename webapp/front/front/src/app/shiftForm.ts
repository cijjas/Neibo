import { AmenityForm } from "./amenityForm"
import { Day } from "./day"

export interface ShiftForm {
    shiftId: number
    amenities: AmenityForm[]
    day: Day
    startTime: string
    endTime: string
    taken: boolean
    self: string
}
