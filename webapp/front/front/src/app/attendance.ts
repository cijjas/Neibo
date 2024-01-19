import {User} from "./user";

export interface Attendance {
    attendanceId: number;
    user: User;
    event: Event;
    self: string;
}
