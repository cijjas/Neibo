import { UserDto } from "./user"
import { ContactDto } from "./contact"
import { ResourceDto } from "./resource"
import { WorkerDto } from "./worker"
import { EventDto } from "./event"
import { Links } from "./links"
import { ChannelDto } from "./channel"

export interface Neighborhood {
    name: string
    users: UserDto[]
    contacts: ContactDto[]
    events: EventDto[]
    resources: ResourceDto[]
    channels: ChannelDto[]
    workers: WorkerDto[]
    self: string
}

export interface NeighborhoodDto {
    name: string
    _links: Links
}

export interface NeighborhoodForm {
    neighborhoodId: number
    name: string
    self: string
}
