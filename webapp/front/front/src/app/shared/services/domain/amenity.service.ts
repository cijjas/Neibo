import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin, of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { LinkKey } from '@shared/index';
// todoaca
import {
  Amenity,
  Shift,
  AmenityDto,
  ShiftDto,
  mapShift,
  parseLinkHeader,
} from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Injectable({ providedIn: 'root' })
export class AmenityService {
  constructor(
    private http: HttpClient,
    private linkService: HateoasLinksService
  ) { }

  public getAmenity(amenityUrl: string): Observable<Amenity> {
    return this.http
      .get<AmenityDto>(amenityUrl)
      .pipe(
        mergeMap((amenityDto: AmenityDto) => mapAmenity(this.http, amenityDto))
      );
  }

  public getAmenities(
    queryParams: {
      page?: number;
      size?: number;
    } = {}
  ): Observable<{
    amenities: Amenity[];
    totalPages: number;
    currentPage: number;
  }> {
    let amenitiesUrl: string = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_AMENITIES
    );

    let params = new HttpParams();

    if (queryParams.page !== undefined)
      params = params.set('page', queryParams.page.toString());
    if (queryParams.size !== undefined)
      params = params.set('size', queryParams.size.toString());

    return this.http
      .get<AmenityDto[]>(amenitiesUrl, { params, observe: 'response' })
      .pipe(
        mergeMap((response) => {
          const amenitiesDto: AmenityDto[] = response.body || [];
          const pagination = parseLinkHeader(response.headers.get('Link'));

          const amenityObservables = amenitiesDto.map((amenityDto) =>
            mapAmenity(this.http, amenityDto)
          );
          return forkJoin(amenityObservables).pipe(
            map((amenities) => ({
              amenities,
              totalPages: pagination.totalPages,
              currentPage: pagination.currentPage,
            }))
          );
        })
      );
  }

  public createAmenity(
    name: string,
    description: string,
    selectedShifts: string[]
  ): Observable<string | null> {
    let amenitiesUrl: string = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_AMENITIES
    );

    const body: AmenityDto = {
      name: name,
      description: description,
      selectedShifts: selectedShifts,
    };

    return this.http.post(amenitiesUrl, body, { observe: 'response' }).pipe(
      map((response) => {
        const locationHeader = response.headers.get('Location');
        if (locationHeader) {
          return locationHeader;
        } else {
          console.error('Location header not found:');
          return null;
        }
      }),
      catchError((error) => {
        console.error('Error creating amenity', error);
        return of(null);
      })
    );
  }

  public updateAmenity(
    amenityUrl: string,
    name: string,
    description: string,
    selectedShifts: string[]
  ): Observable<Amenity> {
    const body: AmenityDto = {
      name: name,
      description: description,
      selectedShifts: selectedShifts,
    };

    return this.http
      .patch<AmenityDto>(amenityUrl, body)
      .pipe(mergeMap((newAmenity) => mapAmenity(this.http, newAmenity)));
  }

  public deleteAmenity(amenityUrl: string): Observable<void> {
    return this.http.delete<void>(amenityUrl);
  }
}

export function mapAmenity(
  http: HttpClient,
  amenityDto: AmenityDto
): Observable<Amenity> {
  return forkJoin([http.get<ShiftDto[]>(amenityDto._links.shifts)]).pipe(
    map(([shiftsDto]) => {
      return {
        name: amenityDto.name,
        description: amenityDto.description,
        availableShifts: shiftsDto ? shiftsDto.map(mapShift) : null,
        self: amenityDto._links.self,
      } as Amenity;
    })
  );
}
