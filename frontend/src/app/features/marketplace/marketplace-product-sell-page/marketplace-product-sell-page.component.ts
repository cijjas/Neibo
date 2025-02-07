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
import { Title } from '@angular/platform-browser';
import { AppTitleKeys } from '@shared/constants/app-titles';

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
    private imageService: ImageService,
    private linkService: HateoasLinksService,
    private departmentService: DepartmentService,
    private toastService: ToastService,
    private translate: TranslateService,
    private titleService: Title,
  ) {}

  ngOnInit(): void {
    const title = this.translate.instant(
      AppTitleKeys.MARKETPLACE_PRODUCT_SELL_PAGE,
    );
    this.titleService.setTitle(title);

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

    const remainingSlots = 3 - this.images.length;

    const filesToAdd = Array.from(input.files).slice(0, remainingSlots);

    for (let i = 0; i < filesToAdd.length; i++) {
      const file = filesToAdd[i];
      if (!file.type.startsWith('image/')) continue;

      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.images.push({ file, preview: e.target.result });
        this.clearImageError();
      };
      reader.readAsDataURL(file);
    }

    input.value = '';
  }

  triggerImageInput(el: HTMLInputElement): void {
    el.click();
  }

  removeImage(index: number): void {
    this.images.splice(index, 1);
    this.clearImageError();
  }

  clearImageError(): void {
    if (
      this.images.length > 0 &&
      this.formErrors ===
        this.translate.instant(
          'MARKETPLACE-PRODUCT-SELL-PAGE.PLEASE_UPLOAD_AT_LEAST_ONE_IMAGE_FOR_THE_PRODUCT',
        )
    ) {
      this.formErrors = null;
    }
  }
  formatCurrency(): void {
    let val = this.listingForm.get('price')?.value || '';
    if (!val) return;

    val = val.replace(/[^\d.]/g, '');

    let floatVal = parseFloat(val);
    if (isNaN(floatVal)) {
      floatVal = 0.0;
    }

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
      this.formErrors = this.translate.instant(
        'MARKETPLACE-PRODUCT-SELL-PAGE.PLEASE_FILL_IN_ALL_REQUIRED_FIELDS_CORRECTLY',
      );
      return;
    }

    if (this.images.length === 0) {
      this.formErrors = this.translate.instant(
        'MARKETPLACE-PRODUCT-SELL-PAGE.PLEASE_UPLOAD_AT_LEAST_ONE_IMAGE_FOR_THE_PRODUCT',
      );
      return;
    }

    const rawValue = this.listingForm.value;
    const userSelf = this.linkService.getLink(LinkKey.USER_SELF);

    const priceString = rawValue.price.replace(/[^0-9.]/g, '');
    const price = parseFloat(priceString);

    if (isNaN(price)) {
      this.formErrors = this.translate.instant(
        'MARKETPLACE-PRODUCT-SELL-PAGE.INVALID_PRICE_FORMAT',
      );
      return;
    }

    const productData: any = {
      name: rawValue.title,
      description: rawValue.description,
      price: price,
      used: rawValue.used,
      department: rawValue.departmentId,
      remainingUnits: rawValue.quantity,
      user: userSelf,
      images: [],
    };

    const imageUploadObservables = this.images.map(img =>
      this.imageService.createImage(img.file),
    );

    combineLatest(imageUploadObservables)
      .pipe(
        map(imageUrls => {
          productData.images = imageUrls.filter(url => url !== null);
          return productData;
        }),
        switchMap(data => this.productService.createProduct(data)),
      )
      .subscribe({
        next: productUrl => {
          this.router.navigate(['/marketplace/products', productUrl]);
          this.toastService.showToast(
            this.translate.instant(
              'MARKETPLACE-PRODUCT-SELL-PAGE.LISTING_CREATED_SUCCESSFULLY',
            ),
            'success',
          );
        },
        error: err => {
          this.formErrors = this.translate.instant(
            'MARKETPLACE-PRODUCT-SELL-PAGE.THERE_WAS_A_PROBLEM_CREATING_THE_LISTING',
          );
          console.error(err);
        },
      });
  }
}
