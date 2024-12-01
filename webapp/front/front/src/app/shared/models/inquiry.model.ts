import { User } from "./index";

export interface Inquiry {
  inquiryMessage: string;
  responseMessage: string;
  inquiryDate: Date;
  inquiryUser: User;
  responseUser: User;
  self: string;
}
