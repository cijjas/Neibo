import { Links } from "./links"

export interface WorkerRole {
    workerRole: string
    self: string
}

export interface WorkerRoleDto {
    workerRole: string
    _links: Links
}
