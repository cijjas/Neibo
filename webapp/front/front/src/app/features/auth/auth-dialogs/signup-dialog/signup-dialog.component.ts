import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Neighborhood } from '@shared/index'; // Updated to use Neighborhood only
import { environment } from 'environments/environment';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { tap, map } from "rxjs/operators";

@Component({
  selector: 'signup-dialog',
  templateUrl: './signup-dialog.component.html'
})
export class SignupDialogComponent implements OnInit {
  @Input() showSignupDialog: boolean = false;
  @Output() showSignupDialogChange = new EventEmitter<boolean>();
  private apiServerUrl = environment.apiBaseUrl;

  selectedOption: 'neighbor' | 'service' = 'neighbor';

  signupForm: FormGroup;
  neighborhoodsList: Neighborhood[] = []; // Changed to Neighborhood
  constructor(
    private fb: FormBuilder,
    private http: HttpClient
  ) { }

  // Method to fetch neighborhoods
  getNeighborhoods(): void {
    const params = new HttpParams()
      .set('page', '1')
      .set('size', '10');

    this.http.get<any[]>(`${this.apiServerUrl}/neighborhoods`, { params }) // Adjusted type
      .pipe(
        map((neighborhoodDtos) =>
          neighborhoodDtos.map((dto) => ({
            name: dto.name,
            self: dto._links?.self?.href || '' // Map DTO properties to Neighborhood model
          }))
        )
      )
      .subscribe(
        (neighborhoods: Neighborhood[]) => {
          this.neighborhoodsList = neighborhoods;
        },
        (error: HttpErrorResponse) => {
          console.error('Error fetching neighborhoods:', error);
        }
      );
  }

  ngOnInit() {
    this.getNeighborhoods();
    this.signupForm = this.fb.group({
      neighborhoodId: [null, Validators.required],
      name: ['', Validators.required],
      surname: ['', Validators.required],
      mail: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      identification: [null, [Validators.required, Validators.pattern('[0-9]*'), Validators.min(1), Validators.max(99999999)]],
      language: ['', Validators.required],
    });
  }

  trySignup(): void {
    // Implement your signup logic here using this.signupForm.value
    // This will give you an object with all the form values
  }

  selectOption(option: 'neighbor' | 'service'): void {
    this.selectedOption = option;
  }

  closeSignupDialog(): void {
    this.showSignupDialog = false;
    this.showSignupDialogChange.emit(this.showSignupDialog);
  }
}