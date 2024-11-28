import { Links } from "./links";

export interface ContactDto {
  name: string;
  address: string;
  phone: string;
  _links: Links;
}

export interface Contact {
  name: string;
  address: string;
  phone: string;
}
