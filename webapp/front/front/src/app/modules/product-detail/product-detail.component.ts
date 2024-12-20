import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DepartmentService, HateoasLinksService, InquiryService, ProductService, RequestService, UserService, UserSessionService } from '../../shared/services/index.service'; // Example
import { Department, Inquiry, Product, User } from '../../shared/models';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
})
export class ProductDetailComponent implements OnInit {
  darkMode: boolean = false; // Dynamically set based on user (loggedUser.darkMode)
  departmentList: Department[] = [];
  departmentName: string = '';
  department: string;
  productImages: string[] = [];
  currentBigImage: string = '';
  selectedImageIndex: number = 0; // Default to the first image

  channel: string = 'Marketplace';

  productSelf: string = '';
  product: Product;
  loggedUser: User;
  questions: Inquiry[] = [];
  totalPages: number = 1;
  page: number = 1;
  size: number = 10; // Default size for inquiries
  requestError: boolean = false;

  phoneNumber: string = '';
  requestMessage: string = '';
  phoneRequestMessage: string = '';

  // Controls for dialogs
  requestDialogVisible: boolean = false;
  replyDialogVisible: boolean = false;
  questionForReply: any;

  replayMessage: string = '';
  questionMessage: string = '';


  // toast
  toastVisible: boolean = false;
  toastMessage: string = '';
  toastType: 'success' | 'error' = 'success';

  constructor(
    private productService: ProductService,
    private userSession: UserSessionService,
    private departmentService: DepartmentService,
    private requestService: RequestService,
    private inquiryService: InquiryService,
    private userService: UserService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private linkService: HateoasLinksService,
  ) { }

  ngOnInit(): void {
    this.productSelf = this.route.snapshot.paramMap.get('id')!;

    // Fetch product details
    this.productService.getProduct(this.productSelf).subscribe((product) => {
      this.product = product;
      this.currentBigImage = product.firstImage;
      this.loadProductImages();

      this.route.queryParams.subscribe((params) => {
        this.page = params['page'] ? +params['page'] : 1;
        this.size = params['size'] ? +params['size'] : 10;
        this.loadInquiries();
      });
    });

    // Fetch department list
    this.departmentService.getDepartments().subscribe({
      next: (departments) => {
        this.departmentList = departments;
        console.log(this.departmentList);
      },
      error: (err) => console.error(err),
    });

    // Get current user
    this.userService.getUser(this.linkService.getLink('user:self')).subscribe((user) => {
      this.loggedUser = user;
    });
  }

  private loadInquiries(): void {
    this.inquiryService
      .getInquiries(this.product.inquiries, { page: this.page, size: this.size })
      .subscribe({
        next: (result) => {
          this.questions = result.inquiries;
          this.totalPages = result.totalPages;
        },
        error: (err) => {
          console.error('Error fetching inquiries:', err);
        },
      });
  }


  private loadProductImages(): void {
    this.productImages = [
      this.product.firstImage,
      this.product.secondImage,
      this.product.thirdImage,
    ].filter((image) => !!image);
  }

  // Methods to show/hide dialogs
  openRequestDialog() {
    this.requestDialogVisible = true;
    this.cdr.detectChanges();
  }

  closeRequestDialog() {
    this.requestDialogVisible = false;
  }

  showReplyDialog(question: any) {
    this.questionForReply = question;
    this.replyDialogVisible = true;
  }
  closeReplyDialog() {
    this.replyDialogVisible = false;
    this.questionForReply = null;
  }

  onHoverSmallImage(image: string): void {
    this.currentBigImage = image;
  }

  // Create inquiry
  submitQuestionForm() {
    if (!this.loggedUser || !this.product) {
      console.error('User or product not loaded');
      return;
    }

    if (!this.questionMessage.trim()) {
      return; // Prevent submitting empty questions
    }

    this.inquiryService.createInquiry(this.product.inquiries, this.questionMessage, this.loggedUser.self).subscribe({
      next: (inquiryUrl) => {
        if (inquiryUrl) {
          this.inquiryService.getInquiry(inquiryUrl).subscribe({
            next: (createdInquiry) => {
              this.questions.unshift(createdInquiry);
              this.questionMessage = '';
            },
            error: (err) => {
              console.error('Error retrieving inquiry:', err);
            },
          });
        } else {
          console.error('Inquiry creation did not return a valid URL');
        }
      },
      error: (err) => {
        console.error('Error submitting inquiry:', err);
      },
    });
  }



  submitRequestForm(requestForm: NgForm) {
    if (requestForm.invalid) return;

    const { message, amount } = requestForm.value;

    this.requestService.createRequest(message, amount, this.product.self, this.linkService.getLink('user:self')).subscribe({
      next: () => {
        this.showToast('Request sent successfully', 'success');
        this.requestDialogVisible = false;
        requestForm.resetForm();
      },
      error: (err) => {
        console.error('Error sending request:', err);
        this.showToast('Error sending request', 'error');
        this.requestError = true;
      },
    });
    this.cdr.detectChanges();
  }

  submitPhoneRequestForm(phoneRequestForm: NgForm) {
    if (phoneRequestForm.invalid) return;

    const { phoneNumber, message, amount } = phoneRequestForm.value;

    this.userService.updatePhoneNumber(this.loggedUser.self, phoneNumber).subscribe({
      next: () => {
        this.requestService.createRequest(message, amount, this.product.self, this.loggedUser.self).subscribe({
          next: () => {
            this.showToast('Request sent successfully', 'success');
            this.requestDialogVisible = false;
            phoneRequestForm.resetForm();
          },
          error: (err) => {
            console.error('Error sending request:', err);
            this.showToast('Error sending request', 'error');
            this.requestError = true;
          },
        });
      },
      error: (err) => {
        console.error('Error sending request:', err);
        this.showToast('Error sending request', 'error');
        this.requestError = true;
      },
    });
    this.cdr.detectChanges();

  }

  getAmountOptions(): number[] {
    const stock = this.product?.stock || 1; // Default to 1 if product.stock is undefined
    return Array.from({ length: stock }, (_, i) => i + 1);
  }


  submitReplyForm() {

    // Handle reply form submit
  }



  goToDepartment() {
    // Handle navigation to department
  }

  onSelectSmallImage(image: string, index: number): void {
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
      queryParamsHandling: 'merge', // Merge with other existing query params
    });
  }


  showToast(message: string, type: 'success' | 'error'): void {
    this.toastMessage = message;
    this.toastType = type;
    this.toastVisible = true;
    setTimeout(() => {
      this.toastVisible = false;
    }, 3000); // Hide the toast after 3 seconds
  }
}