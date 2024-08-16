import { UserDto } from "./user"
import { Links } from "./links"
import { ShiftDto } from "./shift"

export interface Booking {
    bookingDate: Date
    user: UserDto
    shift: ShiftDto
    self: string
}

export interface BookingDto {
    bookingDate: Date
    _links: Links
}

export interface BookingForm {
    bookingId: number
    shiftIds: number[]
    reservationDate: Date
    self: string
}

