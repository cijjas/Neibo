import {User} from "./user";
import {Neighborhood} from "./neighborhood";

export interface Event {
    eventId: number;
    name: string;
    description: string;
    date: Date;
    neighborhood: Neighborhood;
    startTime: string;
    endTime: string;
    attendees: User[];
    self: string;
}
