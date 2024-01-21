import { HttpClient, HttpParams } from '@angular/common/http'
import { Shift, ShiftDto } from './shift'
import { AmenityDto } from "./amenity"
import { Day } from "./day"
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'
import { map, mergeMap } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class ShiftService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getShift(shiftId: number): Observable<Shift> {
        const shiftDto$ = this.http.get<ShiftDto>(`${this.apiServerUrl}/shift/${shiftId}`);
            
        return shiftDto$.pipe(
            mergeMap((shiftDto: ShiftDto) => {
                return forkJoin([
                    this.http.get<AmenityDto[]>(shiftDto.amenities),
                    this.http.get<Day>(shiftDto.day)
                ]).pipe(
                    map(([amenities, day]) => {
                        return {
                            shiftId: shiftDto.shiftId,
                            amenities: amenities,
                            day: day,
                            startTime: shiftDto.startTime,
                            endTime: shiftDto.endTime,
                            taken: shiftDto.taken,
                            self: shiftDto.self
                        } as Shift;
                    })
                );
            })
        );
    }

    public getShifts(): Observable<Shift[]> {
        return this.http.get<ShiftDto[]>(`${this.apiServerUrl}/shifts`).pipe(
            mergeMap((shiftsDto: ShiftDto[]) => {
                const shiftObservables = shiftsDto.map(shiftDto => 
                    forkJoin([
                        this.http.get<AmenityDto[]>(shiftDto.amenities),
                        this.http.get<Day>(shiftDto.day)
                    ]).pipe(
                        map(([amenities, day]) => {
                            return {
                                shiftId: shiftDto.shiftId,
                                amenities: amenities,
                                day: day,
                                startTime: shiftDto.startTime,
                                endTime: shiftDto.endTime,
                                taken: shiftDto.taken,
                                self: shiftDto.self
                            } as Shift;
                        })
                    )
                );
        
                 return forkJoin(shiftObservables);
            })
        );
    }
}
