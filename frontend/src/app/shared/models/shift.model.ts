export interface Shift {
  startTime: string;
  endTime: string;
  startTimeNoSec?: string;
  endTimeNoSec?: string;
  startTimeDisplay?: string;
  endTimeDisplay?: string;
  day: string;
  taken: boolean;
  self: string;
}
