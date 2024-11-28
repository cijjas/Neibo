import { Links } from "./links"

export interface WorkerStatus {
    workerStatus: string
    self: string
}

export interface WorkerStatusDto {
    workerStatus: string
    _links: Links
}
