import { UserDto } from "./user"
import { AvailabilityDto } from "./availability"

export interface Booking {
    bookingId: number
    bookingDate: Date
    user: UserDto
    amenityAvailability: AvailabilityDto
    self: string
}

export interface BookingDto {
    bookingId: number
    bookingDate: Date
    user: string
    amenityAvailability: string
    self: string
}

export interface BookingForm {
    bookingId: number
    shiftIds: number[]
    reservationDate: Date
    self: string
  }
  
