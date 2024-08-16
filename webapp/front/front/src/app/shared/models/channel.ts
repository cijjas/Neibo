import { Links } from "./links"
import { PostDto } from "./post"

export interface Channel {
    channel: string
    posts: PostDto[]
    self: string
}

export interface ChannelDto {
    channel: string
    _links: Links
}
