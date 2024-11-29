import argparse

# Template for the generated service
service_template = """import {{ HttpClient, HttpParams }} from '@angular/common/http';
import {{ Injectable }} from '@angular/core';
import {{ Observable, forkJoin }} from 'rxjs';
import {{ map, mergeMap }} from 'rxjs/operators';
import {{ {Singular} }} from '../model/{singular}';
import {{ ChannelDto, ImageDto, {Singular}Dto, UserDto }} from '../dtos/app-dtos';
import {{ LikeCountDto }} from '../models/likeCount';
import {{ mapUser }} from './user.service';

@Injectable({{ providedIn: 'root' }})
export class {Singular}Service {{

    constructor(
        private http: HttpClient
    ) {{ }}

    public get{Singular}(url: string): Observable<{Singular}> {{
        return this.http.get<{Singular}Dto>(url).pipe(
            mergeMap(({singular}Dto: {Singular}Dto) => map{Singular}(this.http, {singular}Dto))
        );
    }}

    public list{Plural}(url: string, page: number, size: number): Observable<{Singular}[]> {{
        let params = new HttpParams();
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

        return this.http.get<{Singular}Dto[]>(url, {{ params }}).pipe(
            mergeMap(({plural}Dto: {Singular}Dto[]) => {{
                const {singular}Observables = {plural}Dto.map({singular}Dto => map{Singular}(this.http, {singular}Dto));
                return forkJoin({singular}Observables);
            }})
        );
    }}
}}

export function map{Singular}(http: HttpClient, {singular}Dto: {Singular}Dto): Observable<{Singular}> {{
    return forkJoin([
        // Preemptive fetching
    ]).pipe(
        map(([]) => {{
            return {{
                // Attribute mapping
            }} as {Singular};
        }})
    );
}}
"""

# Main function to handle argument parsing and file creation
def main():
    parser = argparse.ArgumentParser(description="Generate an Angular service for a given entity.")
    parser.add_argument("-s", "--singular", required=True, help="Singular form of the entity name.")
    parser.add_argument("-p", "--plural", required=True, help="Plural form of the entity name.")
    args = parser.parse_args()

    singular = args.singular.lower()
    plural = args.plural.lower()
    Singular = singular.capitalize()
    Plural = plural.capitalize()

    # Format the template with the provided names
    service_code = service_template.format(
        singular=singular,
        plural=plural,
        Singular=Singular,
        Plural=Plural
    )

    # Save the file with the correct name
    filename = f"{singular}.service.ts"
    with open(filename, "w") as file:
        file.write(service_code)
    print(f"Service file '{filename}' generated successfully!")

if __name__ == "__main__":
    main()

# EXAMPLE USAGE COMMAND
# python3 generate.py -s post -p posts

# EXAMPLE OUTPOUT
# import { HttpClient, HttpParams } from '@angular/common/http';
# import { Injectable } from '@angular/core';
# import { Observable, forkJoin } from 'rxjs';
# import { map, mergeMap } from 'rxjs/operators';
# import { Post } from '../model/post';
# import { ChannelDto, ImageDto, PostDto, UserDto } from '../dtos/app-dtos';
# import { LikeCountDto } from '../models/likeCount';
# import { mapUser } from './user.service';

# @Injectable({ providedIn: 'root' })
# export class PostService {

#     constructor(
#         private http: HttpClient
#     ) { }

#     public getPost(url: string): Observable<Post> {
#         return this.http.get<PostDto>(url).pipe(
#             mergeMap((postDto: PostDto) => mapPost(this.http, postDto))
#         );
#     }

#     public listPosts(url: string, page: number, size: number): Observable<Post[]> {
#         let params = new HttpParams();
#         if (page) params = params.set('page', page.toString());
#         if (size) params = params.set('size', size.toString());

#         return this.http.get<PostDto[]>(url, { params }).pipe(
#             mergeMap((postsDto: PostDto[]) => {
#                 const postObservables = postsDto.map(postDto => mapPost(this.http, postDto));
#                 return forkJoin(postObservables);
#             })
#         );
#     }
# }

# export function mapPost(http: HttpClient, postDto: PostDto): Observable<Post> {
#     return forkJoin([
#         // Preemptive fetching
#     ]).pipe(
#         map(([]) => {
#             return {
#                 // Attribute mapping
#             } as Post;
#         })
#     );
# }
