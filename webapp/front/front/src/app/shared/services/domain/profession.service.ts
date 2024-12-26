import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ProfessionDto, Profession, formatName } from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Injectable({ providedIn: 'root' })
export class ProfessionService {
    constructor(
        private http: HttpClient,
        private linkService: HateoasLinksService
    ) { }

    public getProfession(url: string): Observable<Profession> {
        return this.http.get<ProfessionDto>(url).pipe(
            map(mapProfession)
        );
    }

    public getProfessions(
        queryParams: {
            forWorker?: string;
        } = {}
    ): Observable<Profession[]> {
        let params = new HttpParams();

        if (queryParams.forWorker !== undefined) params = params.set('forWorker', queryParams.forWorker.toString());

        const url = this.linkService.getLink('neighborhood:professions')

        return this.http.get<ProfessionDto[]>(url, { params }).pipe(
            map((professionDtos) => professionDtos.map(mapProfession))
        );
    }
}

export function mapProfession(professionDto: ProfessionDto): Profession {
    return {
        name: professionDto.name,
        displayName: formatName(professionDto.name),
        self: professionDto._links.self
    };
}
