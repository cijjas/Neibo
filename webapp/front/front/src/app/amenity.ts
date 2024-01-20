import { NeighborhoodDto } from './neighborhood'
import { AvailabilityDto } from './availability'

export interface Amenity {
    amenityId: number
    name: string
    description: string
    neighborhood: NeighborhoodDto;
    availability: AvailabilityDto[]
    self: string
}

export interface AmenityDto {
    amenityId: number
    name: string
    description: string
    neighborhood: string
    availability: string
    self: string
}

export interface AmenityForm {
    amenityId: number
    name: string
    description: string
    selectedShifts: string[]
    self: string
}

