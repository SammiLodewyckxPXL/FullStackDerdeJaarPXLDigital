import { Injectable, signal } from '@angular/core';
import { Role } from './models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly storageKey = 'mise-role';
  readonly role = signal<Role | null>(this.readRole());

  signIn(role: Role): void {
    sessionStorage.setItem(this.storageKey, role);
    this.role.set(role);
  }

  signOut(): void {
    sessionStorage.removeItem(this.storageKey);
    this.role.set(null);
  }

  private readRole(): Role | null {
    const role = sessionStorage.getItem(this.storageKey);
    return role === 'KLANT' || role === 'MEDEWERKER' || role === 'CHAUFFEUR' ? role : null;
  }
}
