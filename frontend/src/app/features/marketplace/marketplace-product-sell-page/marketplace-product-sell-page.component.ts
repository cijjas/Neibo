import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { combineLatest, map, switchMap } from 'rxjs';
import { Department, LinkKey } from '@shared/index';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import {
  HateoasLinksService,
  ImageService,
  ToastService,
  UserSessionService,
} from '@core/index';

import { DepartmentService, ProductService } from '@shared/index';

@Component({
  selector: 'app-marketplace-product-sell-page',
  templateUrl: './marketplace-product-sell-page.component.html',
})
export class MarketplaceProductSellPageComponent implements OnInit {
  channel: string = 'Sell';
  departmentList: Department[] = [];
  departmentName: string = 'NONE';

  quantities: number[] = Array.from({ length: 100 }, (_, i) => i + 1);

  listingForm = this.fb.group({
    title: ['', Validators.required],
    price: [
      '',
      [Validators.required, Validators.pattern(/^\$?\d+(,\d{3})*(\.\d{2})?$/)],
    ],
    description: ['', Validators.required],
    departmentId: ['', Validators.required],
    used: [false, Validators.required],
    quantity: [1, Validators.required],
  });

  formErrors: string | null = null;
  images: { file: File; preview: string }[] = [];

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private productService: ProductService,
    private userSessionService: UserSessionService,
    private imageService: ImageService,
    private linkService: HateoasLinksService,
    private departmentService: DepartmentService,
    private toastService: ToastService,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    this.departmentService.getDepartments().subscribe({
      next: (departments: Department[]) => {
        this.departmentList = departments;
      },
      error: (err: any) => console.error(err),
    });
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;

    // Calculate the number of remaining slots for images
    const remainingSlots = 3 - this.images.length;

    // Only allow uploading up to the remaining number of slots
    const filesToAdd = Array.from(input.files).slice(0, remainingSlots);

    for (let i = 0; i < filesToAdd.length; i++) {
      const file = filesToAdd[i];
      if (!file.type.startsWith('image/')) continue;

      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.images.push({ file, preview: e.target.result });
        this.clearImageError(); // Clear error if images are selected
      };
      reader.readAsDataURL(file);
    }

    // Clear the file input to allow selecting the same file again if needed
    input.value = '';
  }

  triggerImageInput(el: HTMLInputElement): void {
    el.click();
  }

  removeImage(index: number): void {
    this.images.splice(index, 1);
    this.clearImageError(); // Clear error if there are still images left
  }

  clearImageError(): void {
    if (
      this.images.length > 0 &&
      this.formErrors === this.translate.instant('MARKETPLACE-PRODUCT-SELL-PAGE.PLEASE_UPLOAD_AT_LEAST_ONE_IMAGE_FOR_THE_PRODUCT')
    ) {
      this.formErrors = null;
    }
  }
  formatCurrency(): void {
    let val = this.listingForm.get('price')?.value || '';
    if (!val) return;

    // Remove all non-digit and non-dot characters
    val = val.replace(/[^\d.]/g, '');

    // Parse value
    let floatVal = parseFloat(val);
    if (isNaN(floatVal)) {
      floatVal = 0.0;
    }

    // Format as currency
    let formatted =
      '$' +
      floatVal.toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      });
    this.listingForm.patchValue({ price: formatted }, { emitEvent: false });
  }

  onSubmit(): void {
    if (this.listingForm.invalid) {
      this.formErrors = this.translate.instant('MARKETPLACE-PRODUCT-SELL-PAGE.PLEASE_FILL_IN_ALL_REQUIRED_FIELDS_CORRECTLY');
      return;
    }

    // Ensure at least one image exists
    if (this.images.length === 0) {
      this.formErrors = this.translate.instant('MARKETPLACE-PRODUCT-SELL-PAGE.PLEASE_UPLOAD_AT_LEAST_ONE_IMAGE_FOR_THE_PRODUCT');
      return;
    }

    // Extract and clean form data
    const rawValue = this.listingForm.value;
    const userSelf = this.linkService.getLink(LinkKey.USER_SELF);

    // Clean the price value by removing currency formatting
    const priceString = rawValue.price.replace(/[^0-9.]/g, ''); // Remove all non-numeric characters except `.`.
    const price = parseFloat(priceString);

    if (isNaN(price)) {
      this.formErrors = this.translate.instant('MARKETPLACE-PRODUCT-SELL-PAGE.INVALID_PRICE_FORMAT');
      return;
    }

    // Prepare product data
    const productData: any = {
      name: rawValue.title,
      description: rawValue.description,
      price: price, // Send the price as a double
      used: rawValue.used,
      department: rawValue.departmentId,
      remainingUnits: rawValue.quantity,
      user: userSelf,
      images: [],
    };

    console.log(productData.price);
    // Upload images
    const imageUploadObservables = this.images.map((img) =>
      this.imageService.createImage(img.file)
    );

    combineLatest(imageUploadObservables)
      .pipe(
        map((imageUrls) => {
          productData.images = imageUrls.filter((url) => url !== null);
          return productData;
        }),
        switchMap((data) => this.productService.createProduct(data))
      )
      .subscribe({
        next: (productUrl) => {
          this.router.navigate(['/marketplace/products', productUrl]);
          this.toastService.showToast(
            this.translate.instant('MARKETPLACE-PRODUCT-SELL-PAGE.LISTING_CREATED_SUCCESSFULLY'),
            'success'
          );
        },
        error: (err) => {
          this.formErrors = this.translate.instant('MARKETPLACE-PRODUCT-SELL-PAGE.THERE_WAS_A_PROBLEM_CREATING_THE_LISTING');
          console.error(err);
        },
      });
  }
}
