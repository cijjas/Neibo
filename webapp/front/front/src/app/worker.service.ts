import { HttpClient, HttpParams } from '@angular/common/http';
import {Worker} from './worker'
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({providedIn: 'root'})
export class WorkerService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) { }

    public getWorkers(neighborhoodId: number, professions: string[], workerRole: string, workerStatus: string, page: number, size: number): Observable<Worker[]> {
        const params = new HttpParams().set('neighborhoodId', neighborhoodId).set('professions', professions.toString()).set('workerRole', workerRole).set('workerStatus', workerStatus).set('page', page.toString()).set('size', size.toString());
    
        return this.http.get<Worker[]>(`${this.apiServerUrl}/workers`, { params });
    }

    public getWorker(workerId: number): Observable<Worker> {    
        return this.http.get<Worker>(`${this.apiServerUrl}/workers/${workerId}`);
    }

    public addWorker(worker: Worker): Observable<Worker> {
        return this.http.post<Worker>(`${this.apiServerUrl}/workers`, worker);
    }

    public updateWorker(worker: Worker): Observable<Worker> {
        return this.http.patch<Worker>(`${this.apiServerUrl}/workers/${worker.workerId}`, worker);
    }

}