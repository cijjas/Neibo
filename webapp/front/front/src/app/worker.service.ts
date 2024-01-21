import { HttpClient, HttpParams } from '@angular/common/http'
import { Worker, WorkerDto, WorkerForm } from './worker'
import { UserDto } from "./user"
import { ReviewDto } from "./review"
import { Profession } from "./profession"
import { NeighborhoodDto } from "./neighborhood"
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class WorkerService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getWorker(workerId: number): Observable<Worker> {
        const workerDto$ = this.http.get<WorkerDto>(`${this.apiServerUrl}/workers/${workerId}`);
            
        return workerDto$.pipe(
            mergeMap((workerDto: WorkerDto) => {
                return forkJoin([
                    this.http.get<UserDto>(workerDto.user),
                    this.http.get<ReviewDto[]>(workerDto.reviews),
                    this.http.get<Profession[]>(workerDto.professions),
                    this.http.get<NeighborhoodDto[]>(workerDto.workerNeighborhoods)
                ]).pipe(
                    map(([user, reviews, professions, workerNeighborhoods]) => {
                        return {
                            workerId: workerDto.workerId,
                            phoneNumber: workerDto.phoneNumber,
                            businessName: workerDto.businessName,
                            address: workerDto.address,
                            bio: workerDto.bio,
                            user: user,
                            backgroundPicture: workerDto.backgroundPicture,
                            reviews: reviews,
                            professions: professions,
                            workerNeighborhoods: workerNeighborhoods,
                            self: workerDto.self
                        } as Worker;
                    })
                );
            })
        );
    }

    public getWorkers(neighborhoodId: number, professions: string[], workerRole: string, workerStatus: string, page: number, size: number): Observable<Worker[]> {
        const params = new HttpParams().set('neighborhoodId', neighborhoodId).set('professions', professions.toString()).set('workerRole', workerRole).set('workerStatus', workerStatus).set('page', page.toString()).set('size', size.toString())

        return this.http.get<WorkerDto[]>(`${this.apiServerUrl}/workers`, { params }).pipe(
            mergeMap((workersDto: WorkerDto[]) => {
                const workerObservables = workersDto.map(workerDto => 
                    forkJoin([
                        this.http.get<UserDto>(workerDto.user),
                        this.http.get<ReviewDto[]>(workerDto.reviews),
                        this.http.get<Profession[]>(workerDto.professions),
                        this.http.get<NeighborhoodDto[]>(workerDto.workerNeighborhoods)
                    ]).pipe(
                        map(([user, reviews, professions, workerNeighborhoods]) => {
                            return {
                                workerId: workerDto.workerId,
                                phoneNumber: workerDto.phoneNumber,
                                businessName: workerDto.businessName,
                                address: workerDto.address,
                                bio: workerDto.bio,
                                user: user,
                                backgroundPicture: workerDto.backgroundPicture,
                                reviews: reviews,
                                professions: professions,
                                workerNeighborhoods: workerNeighborhoods,
                                self: workerDto.self
                            } as Worker;
                        })
                    )
                );
        
                 return forkJoin(workerObservables);
            })
        );
    }

    public addWorker(worker: WorkerForm): Observable<WorkerForm> {
        return this.http.post<WorkerForm>(`${this.apiServerUrl}/workers`, worker)
    }

    public updateWorker(worker: WorkerForm): Observable<WorkerForm> {
        return this.http.patch<WorkerForm>(`${this.apiServerUrl}/workers/${worker.workerId}`, worker)
    }

}
