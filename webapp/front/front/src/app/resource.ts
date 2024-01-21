import { ImageDto } from "./image"
import { NeighborhoodDto } from "./neighborhood"

export interface Resource {
    resourceId: number
    title: string
    description: string
    image: ImageDto
    neighborhood: NeighborhoodDto
    self: string
}

export interface ResourceDto {
    resourceId: number
    title: string
    description: string
    image: string
    neighborhood: string
    self: string
}

export interface ResourceForm {
    resourceId: number
    description: string
    imageFile: string
    title: string
    self: string
  }
  
