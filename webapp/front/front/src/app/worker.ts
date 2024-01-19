import {Neighborhood} from "./neighborhood";
import {User} from "./user";
import {Review} from "./review";
import {Profession} from "./profession";

export interface Worker {
    workerId: number;
    phoneNumber: string;
    businessName: string;
    address: string;
    bio: string;
    user: User;
    backgroundPicture: string;
    reviews: Review[];
    professions: Profession[];
    workerNeighborhoods: Neighborhood[];
    self: string;
}
