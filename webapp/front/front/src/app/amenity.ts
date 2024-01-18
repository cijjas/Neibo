import { Neighborhood } from './neighborhood';
import { Availability } from './availability';

export interface Amenity {
    amenityId: number
    name: string
    description: string
    date: Date
    startTime: string
    endTime: string
    self: string
}
