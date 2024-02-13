import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {Neighborhood} from "../../../shared/models/neighborhood";
import {NeighborhoodService} from "../../../shared/services/neighborhood.service";
import {HttpErrorResponse} from "@angular/common/http";
import {tap} from "rxjs";

@Component({
  selector: 'signup-dialog',
  templateUrl: './signup-dialog.component.html'
})
export class SignupDialogComponent implements OnInit {
  @Input() showSignupDialog: boolean = false;
  @Output() showSignupDialogChange = new EventEmitter<boolean>();

  selectedOption: 'neighbor' | 'service' = 'neighbor';

  signupForm: FormGroup;
  neighborhoodsList: Neighborhood[] = [];
  constructor(
    private fb: FormBuilder,
    private neighborhoodService: NeighborhoodService
  ) {}

  getNeighborhoods(): void {
    this.neighborhoodService.getNeighborhoods(1, 10)
      .subscribe(
        (neighborhoods: Neighborhood[]) => {
          this.neighborhoodsList = neighborhoods;
        }
      )
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
