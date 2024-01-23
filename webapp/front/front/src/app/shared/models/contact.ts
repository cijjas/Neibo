import { NeighborhoodDto } from "./neighborhood"

export interface Contact {
    contactId: number
    contactName: string
    contactAddress: string
    contactPhone: string
    neighborhood: NeighborhoodDto
    self: string
}

export interface ContactDto {
    contactId: number
    contactName: string
    contactAddress: string
    contactPhone: string
    neighborhood: string
    self: string
}

export interface ContactForm {
    contactId: number
    contactName: string
    contactAddress: string
    contactPhone: string
    self: string
  }
  
