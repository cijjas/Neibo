import { UserDto } from "./user"
import { EventDto } from "./event"

export interface Attendance {
    attendanceId: number
    user: UserDto
    event: EventDto
    self: string
}

export interface AttendanceDto {
    attendanceId: number
    user: string
    event: string
    self: string
}

export interface AttendanceForm {
    attendanceId: number
    user: string
    event: string
    self: string
}
