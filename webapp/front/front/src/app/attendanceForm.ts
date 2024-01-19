import {UserForm} from "./userForm"

export interface AttendanceForm {
    attendanceId: number
    user: UserForm
    event: Event
    self: string
}
