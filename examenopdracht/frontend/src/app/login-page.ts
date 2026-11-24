import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Role } from './models';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.html',
  styleUrl: './login-page.css',
})
export class LoginPage {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly roles: { id: Role; label: string; detail: string; icon: string }[] = [
    { id: 'KLANT', label: 'Klant', detail: 'Ontdek het weekmenu', icon: '♡' },
    { id: 'MEDEWERKER', label: 'Medewerker', detail: 'Voor het keukenteam', icon: '✳' },
    { id: 'CHAUFFEUR', label: 'Chauffeur', detail: 'Voor ons bezorgteam', icon: '↗' },
  ];
  enter(role: Role): void {
    this.auth.signIn(role);
    void this.router.navigate(['/menu']);
  }
}
