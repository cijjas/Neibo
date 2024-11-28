import { Links } from "./links";

export interface UserDto {
  mail: string;
  name: string;
  surname: string;
  darkMode: boolean;
  phoneNumber: string;
  identification: number;
  creationDate: Date;
  _links: Links;
}

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
}
