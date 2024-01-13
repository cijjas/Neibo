export interface GroupedBooking {
    groupedBookingId: number;
    bookingIds: number[];
    amenityName: string;
    date: Date;
    day: string;
    startTime: string;
    endTime: string;
    self: string;
}
