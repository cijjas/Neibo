import { Links } from "./links"
import { NeighborhoodDto } from "./neighborhood"

export interface Contact {
    contactName: string
    contactAddress: string
    contactPhone: string
    neighborhood: NeighborhoodDto
    self: string
}

export interface ContactDto {
    contactName: string
    contactAddress: string
    contactPhone: string
    _links: Links
}

export interface ContactForm {
    contactId: number
    contactName: string
    contactAddress: string
    contactPhone: string
    self: string
  }

