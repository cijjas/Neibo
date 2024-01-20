import {User} from "./user";
import {Contact} from "./contact";
import {Resource} from "./resource";
import {Channel} from "./channel";
import {Worker} from "./worker";
import {Event} from "./event";

export interface Neighborhood {
    neighborhoodId: number;
    name: string;
    users: User[];
    contacts: Contact[];
    events: Event[];
    resources: Resource[];
    channels: Channel[];
    workers: Worker[];
    self: string;
}
