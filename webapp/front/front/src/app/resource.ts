import { NeighborhoodDto } from "./neighborhood"
import { ImageDto } from "./image"

export interface Resource {
    resourceId: number
    title: string
    description: string
    image: ImageDto
    neighborhood: NeighborhoodDto
}

export interface ResourceDto {
    resourceId: number
    title: string
    description: string
    image: string
    neighborhood: string
}

export interface ResourceForm {
    resourceId: number
    description: string
    imageFile: string
    title: string
    self: string
  }
  
