import { Links } from "./links";

export interface ChannelDto {
  name: string;
  _links: Links;
}

export interface Channel {
  name: string;
}
