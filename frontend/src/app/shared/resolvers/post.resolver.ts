import { inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  ResolveFn,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Post } from '@shared/models';
import { PostService } from '@shared/services/domain/post.service';
import { decodeUrlSafeBase64 } from '@shared/utils/url-safe-base64.util';
import { catchError, EMPTY } from 'rxjs';

export const postResolver: ResolveFn<Post> = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const router = inject(Router);
  const postService = inject(PostService);

  const encodedUrl: string | null = route.paramMap.get('id');
  if (!encodedUrl) {
    router.navigate(['/not-found']);
    return EMPTY;
  }

  let postUrl: string;
  try {
    postUrl = decodeUrlSafeBase64(encodedUrl);
  } catch (error) {
    console.error('Error decoding URL:', error);
    router.navigate(['/not-found']);
    return EMPTY;
  }

  return postService.getPost(postUrl).pipe(
    catchError(error => {
      console.error('Error loading post:', error);
      router.navigate(['/not-found']);
      return EMPTY;
    }),
  );
};
