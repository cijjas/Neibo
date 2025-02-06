import { Role } from '@shared/enums/roles.enum';

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
  userRoleEnum: Role;
  userRoleDisplay: string;
  image: string;
  self: string;
}
