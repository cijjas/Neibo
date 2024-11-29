export interface User {
  mail: string;
  name: string;
  surname: string;
  darkMode: boolean;
  phoneNumber: string;
  identification: number;
  creationDate: Date;
  language: string;
  userRole: string;
  image: Uint8Array;
  self: string;
}
