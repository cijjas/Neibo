import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ConfirmationService,
  HateoasLinksService,
  ToastService,
} from '@core/index';
import { User, UserService, LinkKey } from '@shared/index';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-admin-neighbors-requests-page',
  templateUrl: './admin-neighbors-requests-page.component.html',
})
export class AdminNeighborsRequestsPageComponent implements OnInit {
  neighbors: boolean = false; // Determines if we are dealing with neighbors or another role
  users: User[] = [];
  totalPages: number = 1;
  currentPage: number = 1;
  pageSize: number = 10;

  constructor(
    private userService: UserService,
    private linkService: HateoasLinksService,
    private toastService: ToastService,
    private route: ActivatedRoute,
    private confirmationService: ConfirmationService,
    private router: Router,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    this.route.url.subscribe((urlSegments) => {
      const currentRoute = urlSegments.map((segment) => segment.path).join('/');
      this.route.queryParams.subscribe((params) => {
        this.currentPage = +params['page'] || 1; // Default to page 1
        this.pageSize = +params['size'] || 10; // Default to size 10
        this.loadUsers(currentRoute);
      });
    });
  }

  loadUsers(currentRoute: string): void {
    let userRoleUrl: string;
    if (currentRoute === 'neighbors/requests') {
      userRoleUrl = this.linkService.getLink(
        LinkKey.UNVERIFIED_NEIGHBOR_USER_ROLE
      );
    } else if (currentRoute === 'neighbors') {
      this.neighbors = true;
      userRoleUrl = this.linkService.getLink(LinkKey.NEIGHBOR_USER_ROLE);
    } else {
      return;
    }

    this.userService
      .getUsers({
        userRole: userRoleUrl,
        page: this.currentPage,
        size: this.pageSize,
      })
      .subscribe({
        next: (res) => {
          this.users = res.users;
          this.totalPages = res.totalPages || 1;
        },
        error: (err) => {
          console.error(this.translate.instant(
            'ADMIN-NEIGHBORS-REQUESTS-PAGE.ERROR_LOADING_USERS',
          ), err);
          this.users = [];
        },
      });
  }

  rejectUser(user: User): void {
    const actionDetails = this.neighbors
      ? {
          title: this.translate.instant('ADMIN-NEIGHBORS-REQUESTS-PAGE.REMOVE_NEIGHBOR'),
          message: this.translate.instant('ADMIN-NEIGHBORS-REQUESTS-PAGE.ARE_YOU_SURE_YOU_WANT_TO_REMOVE_USERNAME_AS_A_NEIG', {userName: user.name}),
          confirmText: this.translate.instant('ADMIN-NEIGHBORS-REQUESTS-PAGE.YES_REMOVE'),
          successMessage: this.translate.instant('ADMIN-NEIGHBORS-REQUESTS-PAGE.USERNAME_HAS_BEEN_SUCCESSFULLY_REMOVED_AS_A_NEIGHB', {userName: user.name}),
          errorMessage: this.translate.instant('ADMIN-NEIGHBORS-REQUESTS-PAGE.WE_ENCOUNTERED_AN_ISSUE_WHILE_TRYING_TO_REMOVE_USE', {userName: user.name}),
        }
      : {
          title: this.translate.instant('ADMIN-NEIGHBORS-REQUESTS-PAGE.REJECT_USER_REQUEST'),
          message: this.translate.instant('ADMIN-NEIGHBORS-REQUESTS-PAGE.ARE_YOU_SURE_YOU_WANT_TO_DECLINE_THE_REQUEST_FROM_', {userName: user.name}),
          confirmText: this.translate.instant('ADMIN-NEIGHBORS-REQUESTS-PAGE.YES_REJECT'),
          successMessage: this.translate.instant('ADMIN-NEIGHBORS-REQUESTS-PAGE.THE_REQUEST_FROM_USERNAME_HAS_BEEN_SUCCESSFULLY_DE', {userName: user.name}),
          errorMessage: this.translate.instant('ADMIN-NEIGHBORS-REQUESTS-PAGE.WE_ENCOUNTERED_AN_ISSUE_WHILE_TRYING_TO_DECLINE_TH', {userName: user.name}),
        };

    this.confirmationService
      .askForConfirmation({
        title: actionDetails.title,
        message: actionDetails.message,
        confirmText: actionDetails.confirmText,
        cancelText: this.translate.instant('ADMIN-NEIGHBORS-REQUESTS-PAGE.CANCEL'),
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          this.userService.rejectUser(user).subscribe({
            next: () => {
              this.toastService.showToast(
                actionDetails.successMessage,
                'success'
              );
              this.loadUsers(
                this.route.snapshot.url.map((segment) => segment.path).join('/')
              );
            },
            error: () => {
              this.toastService.showToast(actionDetails.errorMessage, 'error');
            },
          });
        }
      });
  }

  verifyUser(user: User) {
    this.userService.verifyUser(user).subscribe({
      next: () => {
        this.toastService.showToast(
          this.translate.instant('ADMIN-NEIGHBORS-REQUESTS-PAGE.USER_USERNAME_HAS_BEEN_SUCCESSFULLY_VERIFIED_AS_A_', {userName: user.name}),
          'success'
        );
        this.loadUsers(
          this.route.snapshot.url.map((segment) => segment.path).join('/')
        );
      },
      error: () => {
        this.toastService.showToast(
          this.translate.instant('ADMIN-NEIGHBORS-REQUESTS-PAGE.VERIFICATION_FOR_USER_USERNAME_FAILED_PLEASE_TRY_A', {userName: user.name}),
          'error'
        );
      },
    });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.updateQueryParams();
    this.loadUsers(
      this.route.snapshot.url.map((segment) => segment.path).join('/')
    );
  }

  private updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: this.currentPage, size: this.pageSize },
      queryParamsHandling: 'merge', // Merge with other existing query params
    });
  }
}
