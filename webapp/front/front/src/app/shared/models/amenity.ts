import { NeighborhoodDto } from './neighborhood'
import { Links } from './links'
import { ShiftDto } from './shift'

export interface Amenity {
    name: string
    description: string
    neighborhood: NeighborhoodDto;
    shifts: ShiftDto[]
    self: string
}

export interface AmenityDto {
    name: string
    description: string
    _links: Links
}

export interface AmenityForm {
    amenityId: number
    name: string
    description: string
    selectedShifts: string[]
    self: string
}

