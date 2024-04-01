import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {Neighborhood, NeighborhoodDto} from "../../../shared/models/neighborhood";
import {NeighborhoodService} from "../../../shared/services/neighborhood.service";
import { environment } from '../../../../environments/environment'
import { HttpClient, HttpParams } from '@angular/common/http'

import {HttpErrorResponse} from "@angular/common/http";
import {tap} from "rxjs";

@Component({
  selector: 'signup-dialog',
  templateUrl: './signup-dialog.component.html'
})
export class SignupDialogComponent implements OnInit {
  @Input() showSignupDialog: boolean = false;
  @Output() showSignupDialogChange = new EventEmitter<boolean>();
  private apiServerUrl = environment.apiBaseUrl

  selectedOption: 'neighbor' | 'service' = 'neighbor';

  signupForm: FormGroup;
  neighborhoodsList: NeighborhoodDto[] = [];
  constructor(
    private fb: FormBuilder,
    private neighborhoodService: NeighborhoodService,
    private http: HttpClient
  ) {}

  getNeighborhoods(): void {
    const params = new HttpParams()
          .set('page', '1')
          .set('size', '10');

    this.http.get<NeighborhoodDto[]>(`${this.apiServerUrl}/neighborhoods`, { params })
      .subscribe(
        (neighborhoods: NeighborhoodDto[]) => {
          this.neighborhoodsList = neighborhoods;
        }
      )
  }

  ngOnInit() {
    this.getNeighborhoods()
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
