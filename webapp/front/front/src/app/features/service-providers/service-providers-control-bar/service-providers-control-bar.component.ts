import { Component, OnInit } from '@angular/core';
import { ProfessionService, Profession } from '@shared/index';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-upper-service-buttons',
  templateUrl: './service-providers-control-bar.component.html',
})
export class ServiceProvidersControlBarComponent implements OnInit {
  professionList: Profession[] = []; // Fetch from a service
  currentProfession: Profession;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private professionService: ProfessionService
  ) { }

  ngOnInit(): void {
    // Fetch the profession list
    this.professionService.getProfessions().subscribe(
      (professions) => {
        this.professionList = professions.map((prof) => prof); // If using display names
      },
      (error) => {
        console.error('Error fetching professions:', error);
      }
    );

    // Get the current profession from query params
    this.route.queryParams.subscribe((params) => {
      this.currentProfession = params['professions'] || '';
    });
  }


  setProfession(prof: Profession | null): void {
    if (prof === null) {
      this.currentProfession = null;

      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: { professions: null },
        queryParamsHandling: 'merge',
      });
    } else {
      this.currentProfession = prof;

      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: { professions: prof.self },
        queryParamsHandling: 'merge',
      });
    }
  }

}
