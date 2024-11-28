import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { Channel, ChannelDto } from '../models/channel'
import { Observable, mergeMap, forkJoin, map } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { LoggedInService } from './loggedIn.service'
import { PostDto } from '../models/post'

@Injectable({ providedIn: 'root' })
export class ChannelService {
    private apiServerUrl = environment.apiBaseUrl
    private headers: HttpHeaders

    constructor(
        private http: HttpClient,
        private loggedInService: LoggedInService
    ) {
        this.headers = new HttpHeaders({
            'Authorization': this.loggedInService.getAuthToken()
        })
    }

    public getChannel(channel: string): Observable<Channel> {
        const channelDto$ = this.http.get<ChannelDto>(channel, { headers: this.headers });

        return channelDto$.pipe(
            mergeMap((channelDto: ChannelDto) => {
                return forkJoin([
                    this.http.get<PostDto[]>(channelDto._links.posts),
                ]).pipe(
                    map(([posts]) => {
                        return {
                            channel: channelDto.name,
                            posts: posts,
                            self: channelDto._links.self
                        } as Channel;
                    })
                );
            })
        );
    }

    public getChannels(neighborhood: string, page: number, size: number): Observable<Channel[]> {
        let params = new HttpParams()
        if (page) params = params.set('page', page.toString())
        if (size) params = params.set('size', size.toString())

        return this.http.get<ChannelDto[]>(`${neighborhood}/channels`, { params, headers: this.headers }).pipe(
            mergeMap((channelsDto: ChannelDto[]) => {
                const channelObservables = channelsDto.map(channelDto =>
                    forkJoin([
                        this.http.get<PostDto[]>(channelDto._links.posts),
                    ]).pipe(
                        map(([posts]) => {
                            return {
                                channel: channelDto.name,
                                posts: posts,
                                self: channelDto._links.self
                            } as Channel;
                        })
                    )
                );

                return forkJoin(channelObservables);
            })
        );
    }

}
