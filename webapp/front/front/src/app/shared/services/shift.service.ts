import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { Shift, ShiftDto } from '../models/shift'
import { AmenityDto } from "../models/amenity"
import { Day } from "../models/day"
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class ShiftService {
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

    public getShift(shift: string): Observable<Shift> {
        const shiftDto$ = this.http.get<ShiftDto>(shift, { headers: this.headers });

        return shiftDto$.pipe(
            mergeMap((shiftDto: ShiftDto) => {
                return forkJoin([
                    //this.http.get<AmenityDto[]>(shiftDto._links.amenities),
                    this.http.get<Day>(shiftDto.day)
                ]).pipe(
                    map(([day]) => {
                        return {
                            day: day,
                            startTime: shiftDto.startTime,
                            self: shiftDto._links.self
                        } as Shift;
                    })
                );
            })
        );
    }

    public getShifts(): Observable<Shift[]> {
        return this.http.get<ShiftDto[]>(`${this.apiServerUrl}/shifts`, { headers: this.headers }).pipe(
            mergeMap((shiftsDto: ShiftDto[]) => {
                const shiftObservables = shiftsDto.map(shiftDto =>
                    forkJoin([
                        //this.http.get<AmenityDto[]>(shiftDto.amenities),
                        this.http.get<Day>(shiftDto.day)
                    ]).pipe(
                        map(([day]) => {
                            return {
                                day: day,
                                startTime: shiftDto.startTime,
                                self: shiftDto._links.self
                            } as Shift;
                        })
                    )
                );

                 return forkJoin(shiftObservables);
            })
        );
    }
}
