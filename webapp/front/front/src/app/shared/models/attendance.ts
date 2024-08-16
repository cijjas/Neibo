import { UserDto } from "./user"
import { EventDto } from "./event"
import { Links } from "./links"

export interface Attendance {
    user: UserDto
    event: EventDto
    self: string
}

export interface AttendanceDto {
    _links: Links
}

export interface AttendanceForm {
    attendanceId: number
    user: string
    event: string
    self: string
}
