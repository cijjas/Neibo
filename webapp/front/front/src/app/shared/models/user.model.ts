import { Roles } from './enums/roles.enum';

export interface User {
  email: string;
  name: string;
  surname: string;
  darkMode: boolean;
  phoneNumber: string;
  identification: number;
  creationDate: Date;
  language: string;
  userRole: string;
  userRoleEnum: Roles;
  userRoleDisplay: string;
  image: string;
  self: string;
}
