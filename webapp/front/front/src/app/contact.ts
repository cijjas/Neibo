import {Neighborhood} from "./neighborhood";

export interface Contact {
  contactId: number;
  contactName: string;
  contactAddress: string;
  contactPhone: string;
  neighborhood: Neighborhood;
  self: string;
}
