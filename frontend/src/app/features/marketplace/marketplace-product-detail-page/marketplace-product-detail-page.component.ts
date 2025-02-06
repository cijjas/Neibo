import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';

import {
  InquiryService,
  LinkKey,
  RequestService,
  UserService,
} from '@shared/index';
import { Department, Inquiry, Product, User } from '@shared/index';
import {
  ToastService,
  HateoasLinksService,
  UserSessionService,
} from '@core/index';

@Component({
  selector: 'app-marketplace-product-detail-page',
  templateUrl: './marketplace-product-detail-page.component.html',
})
export class MarketplaceProductDetailPageComponent implements OnInit {
  departmentList: Department[] = [];
  departmentName: string = '';
  department: string;
  productImages: string[] = [];
  currentBigImage: string = '';
  selectedImageIndex: number = 0; 

  channel: string = 'Marketplace';

  productSelf: string = '';
  product: Product;
  loggedUser: User;
  questions: Inquiry[] = [];
  totalPages: number = 1;
  page: number = 1;
  size: number = 10; 
  requestError: boolean = false;

  phoneNumber: string = '';
  requestMessage: string = '';
  phoneRequestMessage: string = '';

  requestDialogVisible: boolean = false;
  replyDialogVisible: boolean = false;
  questionForReply: any;

  replyMessage: string = '';
  questionMessage: string = '';

  toastVisible: boolean = false;
  toastMessage: string = '';
  toastType: 'success' | 'error' = 'success';

  constructor(
    private requestService: RequestService,
    private inquiryService: InquiryService,
    private userService: UserService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private linkService: HateoasLinksService,
    private toastService: ToastService,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    this.route.data.subscribe(({ product }) => {
      if (!product) {
        console.error('Product not found or failed to resolve');
        return;
      }
      this.product = product;
      this.currentBigImage = product.firstImage;
      this.loadProductImages();
      this.loadInquiries();
    });

    this.userService
      .getUser(this.linkService.getLink(LinkKey.USER_SELF))
      .subscribe(user => {
        this.loggedUser = user;
      });

    this.route.queryParams.subscribe(params => {
      this.page = params['page'] ? +params['page'] : 1;
      this.size = params['size'] ? +params['size'] : 10;
      this.loadInquiries();
    });
  }

  private loadInquiries(): void {
    this.inquiryService
      .getInquiries(this.product.inquiries, {
        page: this.page,
        size: this.size,
      })
      .subscribe({
        next: result => {
          this.questions = result.inquiries;
          this.totalPages = result.totalPages;
        },
        error: err => {
          console.error('Error fetching inquiries:', err);
        },
      });
  }

  private loadProductImages(): void {
    this.productImages = [
      this.product.firstImage,
      this.product.secondImage,
      this.product.thirdImage,
    ].filter(image => !!image);
  }

  openRequestDialog() {
    this.requestDialogVisible = true;
    this.cdr.detectChanges();
  }

  closeRequestDialog() {
    this.requestDialogVisible = false;
  }

  showReplyDialog(question: Inquiry): void {
    this.questionForReply = question;
    this.replyDialogVisible = true;
  }

  closeReplyDialog(): void {
    this.replyDialogVisible = false;
    this.questionForReply = null;
    this.replyMessage = '';
  }

  submitQuestionForm() {
    if (!this.loggedUser || !this.product) {
      console.error('User or product not loaded');
      return;
    }

    if (!this.questionMessage.trim()) {
      return; 
    }

    this.inquiryService
      .createInquiry(
        this.product.inquiries,
        this.questionMessage,
        this.loggedUser.self,
      )
      .subscribe({
        next: inquiryUrl => {
          if (inquiryUrl) {
            this.inquiryService.getInquiry(inquiryUrl).subscribe({
              next: createdInquiry => {
                this.questions.unshift(createdInquiry);
                this.questionMessage = '';
              },
              error: err => {
                console.error('Error retrieving inquiry:', err);
              },
            });
          } else {
            console.error('Inquiry creation did not return a valid URL');
          }
        },
        error: err => {
          console.error('Error submitting inquiry:', err);
        },
      });
  }

  submitRequestForm(requestForm: NgForm) {
    if (requestForm.invalid) return;

    const { message, amount } = requestForm.value;

    this.requestService
      .createRequest(
        message,
        amount,
        this.product.self,
        this.linkService.getLink(LinkKey.USER_SELF),
      )
      .subscribe({
        next: () => {
          this.toastService.showToast(
            this.translate.instant(
              'MARKETPLACE-PRODUCT-DETAIL-PAGE.REQUEST_SENT_SUCCESSFULLY',
            ),
            'success',
          );
          this.requestDialogVisible = false;
          requestForm.resetForm();
        },
        error: err => {
          this.toastService.showToast(
            this.translate.instant('MARKETPLACE-PRODUCT-DETAIL-PAGE.OOPS'),
            'error',
          );
          this.requestError = true;
        },
      });
    this.cdr.detectChanges();
  }

  submitPhoneRequestForm(phoneRequestForm: NgForm) {
    if (phoneRequestForm.invalid) return;

    const { phoneNumber, message, amount } = phoneRequestForm.value;

    this.userService
      .updatePhoneNumber(this.loggedUser.self, phoneNumber)
      .subscribe({
        next: () => {
          this.requestService
            .createRequest(
              message,
              amount,
              this.product.self,
              this.loggedUser.self,
            )
            .subscribe({
              next: () => {
                this.toastService.showToast(
                  this.translate.instant(
                    'MARKETPLACE-PRODUCT-DETAIL-PAGE.REQUEST_SENT_SUCCESSFULLY',
                  ),
                  'success',
                );
                this.requestDialogVisible = false;
                phoneRequestForm.resetForm();
              },
              error: err => {
                console.error('Error sending request:', err);
                this.toastService.showToast(
                  this.translate.instant(
                    'MARKETPLACE-PRODUCT-DETAIL-PAGE.OOPS',
                  ),
                  'error',
                );
                this.requestError = true;
              },
            });
        },
        error: err => {
          console.error('Error sending request:', err);
          this.toastService.showToast(
            this.translate.instant('MARKETPLACE-PRODUCT-DETAIL-PAGE.OOPS'),
            'error',
          );
          this.requestError = true;
        },
      });
    this.cdr.detectChanges();
  }

  getAmountOptions(): number[] {
    const stock = this.product?.stock || 1; 
    return Array.from({ length: stock }, (_, i) => i + 1);
  }

  submitReplyForm(): void {
    if (!this.questionForReply || !this.replyMessage.trim()) {
      return;
    }

    this.inquiryService
      .updateInquiry(this.questionForReply.self, { reply: this.replyMessage })
      .subscribe({
        next: response => {
          this.questionForReply.responseMessage = this.replyMessage;
          this.closeReplyDialog();
          this.toastService.showToast(
            this.translate.instant(
              'MARKETPLACE-PRODUCT-DETAIL-PAGE.REPLY_SENT_SUCCESSFULLY',
            ),
            'success',
          );
        },
        error: err => {
          console.error('Error replying to inquiry:', err);
          this.toastService.showToast(
            this.translate.instant(
              'MARKETPLACE-PRODUCT-DETAIL-PAGE.FAILED_TO_SEND_REPLY',
            ),
            'error',
          );
        },
      });
  }

  goToDepartment(department: Department): void {
    this.router.navigate(['/marketplace'], {
      queryParams: { inDepartment: department.self },
    });
  }

  onSelectSmallImage(image: string, index: number): void {
    this.currentBigImage = image;
    this.selectedImageIndex = index;
  }

  onHoverSmallImage(image: string, index: number): void {
    this.currentBigImage = image;
    this.selectedImageIndex = index;
  }

  onPageChange(page: number): void {
    this.page = page;
    this.updateQueryParams();
    this.loadInquiries();
  }

  private updateQueryParams(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: this.page, size: this.size },
      queryParamsHandling: 'merge', 
    });
  }
}
