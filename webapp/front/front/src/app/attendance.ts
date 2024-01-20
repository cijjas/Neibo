import {User} from "./user";
import {Event} from "./event";

export interface Attendance {
    attendanceId: number;
    user: User;
    event: Event;
    self: string;
}
