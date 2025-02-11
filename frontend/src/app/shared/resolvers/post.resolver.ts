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

  // The route parameter now holds the base64 encoded URL.
  const encodedUrl = route.paramMap.get('id');
  if (!encodedUrl) {
    router.navigate(['/not-found']);
    return EMPTY;
  }

  // Decode it
  let postUrl: string;
  try {
    postUrl = decodeUrlSafeBase64(encodedUrl);
  } catch (error) {
    console.error('Error decoding URL:', error);
    router.navigate(['/not-found']);
    return EMPTY;
  }

  // Use postUrl to get the post from your API.
  return postService.getPost(postUrl).pipe(
    catchError(error => {
      console.error('Error loading post:', error);
      router.navigate(['/not-found']);
      return EMPTY;
    }),
  );
};
