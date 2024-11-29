import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Channel } from '../model/channel';
import { ChannelDto } from '../dtos/app-dtos';

@Injectable({ providedIn: 'root' })
export class ChannelService {
    constructor(private http: HttpClient) {}

    public getChannel(url: string): Observable<Channel> {
        return this.http.get<ChannelDto>(url).pipe(
            map((channelDto: ChannelDto) => mapChannel(channelDto))
        );
    }

    public listChannels(url: string, page: number, size: number): Observable<Channel[]> {
        let params = new HttpParams();
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

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
