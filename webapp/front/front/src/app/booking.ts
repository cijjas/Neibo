import {User} from "./user";
import {Availability} from "./availability";

export interface Booking {
    bookingId: number;
    bookingDate: Date;
    user: User;
    amenityAvailability: Availability;
    self: string;
}
