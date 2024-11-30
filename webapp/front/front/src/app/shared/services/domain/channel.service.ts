import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Channel } from '../../models/index';
import { ChannelDto } from '../../dtos/app-dtos';

@Injectable({ providedIn: 'root' })
export class ChannelService {
    constructor(private http: HttpClient) { }

    public getChannel(url: string): Observable<Channel> {
        return this.http.get<ChannelDto>(url).pipe(
            map((channelDto: ChannelDto) => mapChannel(channelDto))
        );
    }

    public getChannels(
        url: string,
        queryParams: {
            page?: number;
            size?: number;
        } = {}
    ): Observable<Channel[]> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());

        return this.http.get<ChannelDto[]>(url, { params }).pipe(
            map((channelsDto: ChannelDto[]) => channelsDto.map(mapChannel))
        );
    }

}

export function mapChannel(channelDto: ChannelDto): Channel {
    return {
        name: channelDto.name,
        self: channelDto._links.self
    };
}
