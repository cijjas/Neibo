import {Neighborhood} from "./neighborhood";
import {Image} from "./image";

export interface Resource {
    resourceId: number;
    title: string;
    description: string;
    image: Image;
    neighborhood: Neighborhood;
}
