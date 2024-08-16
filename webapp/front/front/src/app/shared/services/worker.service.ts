import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { Worker, WorkerDto, WorkerForm } from '../models/worker'
import { UserDto } from "../models/user"
import { ReviewDto } from "../models/review"
import { ProfessionDto } from "../models/profession"
import { NeighborhoodDto } from "../models/neighborhood"
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'
import { ImageDto } from '../models/image'

@Injectable({providedIn: 'root'})
export class WorkerService {
    private apiServerUrl = environment.apiBaseUrl
    private headers: HttpHeaders

    constructor(
        private http: HttpClient,
        private loggedInService: LoggedInService,
    ) { 
        this.headers = new HttpHeaders({
            'Authorization': this.loggedInService.getAuthToken()
        })
    }

    public getWorker(worker: string): Observable<Worker> {
        const workerDto$ = this.http.get<WorkerDto>(worker, { headers: this.headers });

        return workerDto$.pipe(
            mergeMap((workerDto: WorkerDto) => {
                return forkJoin([
                    this.http.get<UserDto>(workerDto._links.user),
                    this.http.get<ReviewDto[]>(workerDto._links.reviews),
                    this.http.get<ProfessionDto[]>(workerDto._links.professions),
                    this.http.get<NeighborhoodDto[]>(workerDto._links.workerNeighborhoods),
                    this.http.get<ImageDto>(workerDto._links.backgroundPicture)
                ]).pipe(
                    map(([user, reviews, professions, workerNeighborhoods, backgroundPicture]) => {
                        return {
                            phoneNumber: workerDto.phoneNumber,
                            businessName: workerDto.businessName,
                            address: workerDto.address,
                            bio: workerDto.bio,
                            user: user,
                            backgroundPicture: backgroundPicture,
                            reviews: reviews,
                            professions: professions,
                            workerNeighborhoods: workerNeighborhoods,
                            self: workerDto._links.self
                        } as Worker;
                    })
                );
            })
        );
    }

    public getWorkers(neighborhoods: string[], professions: string[], workerRole: string, workerStatus: string, page: number, size: number): Observable<Worker[]> {
        let params = new HttpParams()
        if(professions) params = params.set('withProfessions', professions.toString())
        if(neighborhoods) params = params.set('inNeighborhoods', neighborhoods.toString())
        if(workerRole) params = params.set('withRole', workerRole)
        if(workerStatus) params = params.set('wihtStatus', workerStatus)
        if(page) params = params.set('page', page.toString())
        if(size) params = params.set('size', size.toString())

        return this.http.get<WorkerDto[]>(`${this.apiServerUrl}/workers`, { params, headers: this.headers }).pipe(
            mergeMap((workersDto: WorkerDto[]) => {
                const workerObservables = workersDto.map(workerDto =>
                    forkJoin([
                        this.http.get<UserDto>(workerDto._links.user),
                        this.http.get<ReviewDto[]>(workerDto._links.reviews),
                        this.http.get<ProfessionDto[]>(workerDto._links.professions),
                        this.http.get<NeighborhoodDto[]>(workerDto._links.workerNeighborhoods),
                        this.http.get<ImageDto>(workerDto._links.backgroundPicture)
                    ]).pipe(
                        map(([user, reviews, professions, workerNeighborhoods, backgroundPicture]) => {
                            return {
                                phoneNumber: workerDto.phoneNumber,
                                businessName: workerDto.businessName,
                                address: workerDto.address,
                                bio: workerDto.bio,
                                user: user,
                                backgroundPicture: backgroundPicture,
                                reviews: reviews,
                                professions: professions,
                                workerNeighborhoods: workerNeighborhoods,
                                self: workerDto._links.self
                            } as Worker;
                        })
                    )
                );

                 return forkJoin(workerObservables);
            })
        );
    }

    public addWorker(worker: WorkerForm): Observable<WorkerForm> {
        return this.http.post<WorkerForm>(`${this.apiServerUrl}/workers`, worker, { headers: this.headers })
    }

    public updateWorker(workerForm: WorkerForm, worker: string): Observable<WorkerForm> {
        return this.http.patch<WorkerForm>(worker, workerForm, { headers: this.headers })
    }

}
