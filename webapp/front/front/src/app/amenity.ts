import { Neighborhood } from './neighborhood';
import { AvailabilityForm } from './availabilityForm';

export interface Amenity {
    amenityId: number;
    name: string;
    description: string;
    neighborhood: Neighborhood;
    availability: AvailabilityForm;
    self: string;
}
