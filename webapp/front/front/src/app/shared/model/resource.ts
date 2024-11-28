import { Links } from "./links";

export interface ResourceDto {
  title: string;
  description: string;
  _links: Links;
}

export interface Resource {
  title: string;
  description: string;
  image: Uint8Array;
}
