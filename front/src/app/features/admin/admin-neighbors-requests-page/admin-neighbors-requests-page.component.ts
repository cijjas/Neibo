import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ConfirmationService,
  HateoasLinksService,
  ToastService,
} from '@core/index';
import { User, UserService, LinkKey } from '@shared/index';

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
    private router: Router
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
          console.error('Error loading users:', err);
          this.users = [];
        },
      });
  }

  rejectUser(user: User): void {
    const actionDetails = this.neighbors
      ? {
          title: 'Remove Neighbor',
          message: `Are you sure you want to remove "${user.name}" as a neighbor? This action cannot be undone, and the user will lose access to neighborhood features.`,
          confirmText: 'Yes, Remove',
          successMessage: `"${user.name}" has been successfully removed as a neighbor. They no longer have access to neighborhood features.`,
          errorMessage: `We encountered an issue while trying to remove "${user.name}" as a neighbor. Please check your connection or try again later.`,
        }
      : {
          title: 'Reject User Request',
          message: `Are you sure you want to decline the request from "${user.name}"? This action cannot be undone.`,
          confirmText: 'Yes, Reject',
          successMessage: `The request from "${user.name}" has been successfully declined. The user will be notified accordingly.`,
          errorMessage: `We encountered an issue while trying to decline the request from "${user.name}". Please check your connection or try again later.`,
        };

    this.confirmationService
      .askForConfirmation({
        title: actionDetails.title,
        message: actionDetails.message,
        confirmText: actionDetails.confirmText,
        cancelText: 'Cancel',
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
          `User "${user.name}" has been successfully verified as a neighbor.`,
          'success'
        );
        this.loadUsers(
          this.route.snapshot.url.map((segment) => segment.path).join('/')
        );
      },
      error: () => {
        this.toastService.showToast(
          `Verification for user "${user.name}" failed. Please try again later.`,
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
