import {Neighborhood} from "./neighborhood";
import {Product} from "./product";
import {Post} from "./post";
import {Image} from "./image";
import {Booking} from "./booking";

export interface User {
    userId: number;
    mail: string;
    name: string;
    surname: string;
    password: string;
    neighborhood: Neighborhood;
    darkMode: boolean;
    phoneNumber: string;
    profilePicture: Image;
    identification: number;
    comments: Comment[];
    posts: Post[];
    bookings: Booking[];
    subscribedPosts: Post[];
    likedPosts: Post[];
    purchases: Product[];
    sales: Product[];
    eventsSubscribed: Event[];
    self: string;
}
