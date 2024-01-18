import {Post} from "./post"

export interface Channel {
  channelId: number
  channel: string
  posts: Post[]
  self: string
}
