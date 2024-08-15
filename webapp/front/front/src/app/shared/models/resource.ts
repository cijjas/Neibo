import { ImageDto } from "./image"
import { Links } from "./links"
import { NeighborhoodDto } from "./neighborhood"

export interface Resource {
    title: string
    description: string
    image: ImageDto
    neighborhood: NeighborhoodDto
    self: string
}

export interface ResourceDto {
    title: string
    description: string
    _links: Links
}

export interface ResourceForm {
    resourceId: number
    description: string
    imageFile: string
    title: string
    self: string
  }

