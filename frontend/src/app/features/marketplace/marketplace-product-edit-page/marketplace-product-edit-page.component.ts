import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import {
  DepartmentService,
  ProductService,
  Department,
  Product,
  LinkKey,
} from '@shared/index';
import { ImageService, ToastService, HateoasLinksService } from '@core/index';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, forkJoin, map, of, switchMap } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { VALIDATION_CONFIG } from '@shared/constants/validation-config';
import { AppTitleKeys } from '@shared/constants/app-titles';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-marketplace-product-edit-page',
  templateUrl: './marketplace-product-edit-page.component.html',
})
export class MarketplaceProductEditPageComponent implements OnInit {
  channel: string = 'Edit';
  departmentList: Department[] = [];
  product: Product | null = null;

  selectedDepartmentSelf: string | null = null;
  selectedQuantity: number = 1;

  quantities: number[] = Array.from({ length: 100 }, (_, i) => i + 1);

  listingForm = this.fb.group({
    title: [
      '',
      [
        Validators.required,
        Validators.maxLength(VALIDATION_CONFIG.name.maxLength),
      ],
    ],
    price: [
      '',
      [Validators.required, Validators.pattern(/^\$?\d+(,\d{3})*(\.\d{2})?$/)],
    ],
    description: [
      '',
      [
        Validators.required,
        Validators.maxLength(VALIDATION_CONFIG.description.maxLength),
      ],
    ],
    departmentId: ['', Validators.required],
    used: [false, Validators.required],
    quantity: [1, Validators.required],
  });

  formErrors: string | null = null;
  images: { file: File; preview: string }[] = [];

  showImageUpload: boolean = true;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private productService: ProductService,
    private departmentService: DepartmentService,
    private imageService: ImageService,
    private toastService: ToastService,
    private linkService: HateoasLinksService,
    private translate: TranslateService,
    private titleService: Title,
  ) {}

  ngOnInit(): void {
    this.departmentService.getDepartments().subscribe({
      next: (departments: Department[]) => {
        this.departmentList = departments;
      },
      error: (err: any) => console.error(err),
    });

    this.route.data.subscribe(({ product }) => {
      if (!product) {
        console.error('Product not found or failed to resolve');
        return;
      }
      this.product = product;

      const title = this.translate.instant(
        AppTitleKeys.MARKETPLACE_PRODUCT_EDIT_PAGE,
        {
          productName: this.product.name,
        },
      );
      this.titleService.setTitle(title);

      this.populateForm(product);
    });
  }

  populateForm(prod: Product): void {
    this.listingForm.patchValue({
      title: prod.name,
      price: '$' + Number(prod.price).toFixed(2),
      description: prod.description,
      departmentId: prod.department.self,
      used: prod.used,
      quantity: prod.stock,
    });

    this.selectedDepartmentSelf = prod.department.self;

    this.selectedQuantity = prod.stock;

    this.images = [];

    const imageUrls = [
      prod.firstImage,
      prod.secondImage,
      prod.thirdImage,
    ].filter(url => url);

    imageUrls.forEach(url => {
      this.images.push({ file: null as unknown as File, preview: url });
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
          'MARKETPLACE-PRODUCT-EDIT-PAGE.PLEASE_UPLOAD_AT_LEAST_ONE_IMAGE_FOR_THE_PRODUCT',
        )
    ) {
      this.formErrors = null;
    }
  }

  formatCurrency(blur?: boolean): void {
    let val = this.listingForm.get('price')?.value || '';
    if (!val) return;

    val = val.replace(/[^0-9.]/g, '');

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
        'MARKETPLACE-PRODUCT-EDIT-PAGE.PLEASE_FILL_IN_ALL_REQUIRED_FIELDS_CORRECTLY',
      );
      return;
    }

    if (!this.product) {
      this.formErrors = this.translate.instant(
        'MARKETPLACE-PRODUCT-EDIT-PAGE.NO_PRODUCT_LOADED',
      );
      return;
    }

    if (this.images.length === 0) {
      this.formErrors = this.translate.instant(
        'MARKETPLACE-PRODUCT-EDIT-PAGE.PLEASE_UPLOAD_AT_LEAST_ONE_IMAGE_FOR_THE_PRODUCT',
      );
      return;
    }

    const rawValue = this.listingForm.value;
    const userSelf = this.linkService.getLink(LinkKey.USER_SELF);

    const productData: any = {
      name: rawValue.title,
      description: rawValue.description,
      price: rawValue.price.replace('$', ''),
      used: rawValue.used,
      department: rawValue.departmentId,
      remainingUnits: rawValue.quantity,
      user: userSelf,
      images: [],
    };

    const newImageFiles = this.images.filter(img => !!img.file);
    const existingImageUrls = this.images
      .filter(img => !img.file)
      .map(img => img.preview);

    const imageUploadObservables = newImageFiles.map(img =>
      this.imageService.createImage(img.file),
    );

    const upload$ = imageUploadObservables.length
      ? forkJoin(imageUploadObservables)
      : of<string[]>([]);

    upload$
      .pipe(
        map(newImageUrls => {
          productData.images = [...existingImageUrls, ...newImageUrls];
          return productData;
        }),
        switchMap(finalData =>
          this.productService.updateProduct(this.product!.self, finalData),
        ),
      )
      .subscribe({
        next: () => {
          this.toastService.showToast(
            this.translate.instant(
              'MARKETPLACE-PRODUCT-EDIT-PAGE.LISTING_UPDATED_SUCCESSFULLY',
            ),
            'success',
          );
          this.router.navigate(['/marketplace/products', this.product!.self]);
        },
        error: err => {
          this.formErrors = this.translate.instant(
            'MARKETPLACE-PRODUCT-EDIT-PAGE.THERE_WAS_A_PROBLEM_UPDATING_THE_LISTING',
          );
          console.error(err);
        },
      });
  }

  goBack(): void {
    this.router.navigate(['/marketplace/products', this.product?.self]);
  }
}
