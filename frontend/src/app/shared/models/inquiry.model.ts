import { User } from "./index";

export interface Inquiry {
  inquiryMessage: string;
  responseMessage: string;
  inquiryDate: Date;
  self: string;
}
