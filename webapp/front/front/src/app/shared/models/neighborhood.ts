import { UserDto } from "./user"
import { ContactDto } from "./contact"
import { ResourceDto } from "./resource"
import { Channel } from "./channel"
import { WorkerDto } from "./worker"
import { EventDto } from "./event"

export interface Neighborhood {
    neighborhoodId: number
    name: string
    users: UserDto[]
    contacts: ContactDto[]
    events: EventDto[]
    resources: ResourceDto[]
    channels: Channel[]
    workers: WorkerDto[]
    self: string
}

export interface NeighborhoodDto {
    neighborhoodId: number
    name: string
    users: string
    contacts: string
    events: string
    resources: string
    channels: string
    workers: string
    self: string
}

export interface NeighborhoodForm {
    neighborhoodId: number
    name: string
    self: string
}
