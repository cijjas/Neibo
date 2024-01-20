import { UserDto } from "./user"
import { NeighborhoodDto } from "./neighborhood"

export interface Event {
    eventId: number
    name: string
    description: string
    date: Date
    neighborhood: NeighborhoodDto
    startTime: string
    endTime: string
    attendees: UserDto[]
    self: string
}

export interface EventDto {
    eventId: number
    name: string
    description: string
    date: Date
    neighborhood: string
    startTime: string
    endTime: string
    attendees: string
    self: string
}

export interface EventForm {
    eventId: number
    name: string
    description: string
    date: Date
    self: string
}
