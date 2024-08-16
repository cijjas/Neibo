import { UserDto } from "./user"
import { NeighborhoodDto } from "./neighborhood"
import { Links } from "./links"

export interface Event {
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
    name: string
    description: string
    date: Date
    startTime: string
    endTime: string
    _links: Links
}

export interface EventForm {
    eventId: number
    name: string
    description: string
    date: Date
    self: string
}
