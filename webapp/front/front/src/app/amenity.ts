import { Neighborhood } from './neighborhood';
import { Availability } from './availability';

export interface Amenity {
    amenityId: number;
    name: string;
    description: string;
    neighborhood: Neighborhood;
    availability: Availability;
    self: string;
}
