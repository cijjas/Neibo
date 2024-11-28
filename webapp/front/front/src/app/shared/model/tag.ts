import { Links } from "./links";

export interface TagDto {
  name: string;
  _links: Links;
}

export interface Tag {
  name: string;
}
