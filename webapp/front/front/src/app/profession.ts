import { Worker } from './worker';

export interface Profession {
    professionId: number;
    name: string;
    workers: Worker[];
    self: string;
}
