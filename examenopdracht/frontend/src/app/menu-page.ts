import { Component, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { AuthService } from './auth.service';
import { Recipe } from './models';
import { RecipeService } from './recipe.service';

@Component({
  selector: 'app-menu-page',
  imports: [RouterLink],
  templateUrl: './menu-page.html',
  styleUrl: './menu-page.css',
})
export class MenuPage {
  private readonly recipesApi = inject(RecipeService);
  private readonly router = inject(Router);
  readonly auth = inject(AuthService);
  readonly recipes = signal<Recipe[]>([]);
  readonly search = signal('');
  readonly category = signal('Alles');
  readonly selected = signal<string[]>([]);
  readonly categories = ['Alles', 'Vegetarisch', 'Vis', 'Vlees'];
  readonly visibleRecipes = computed(() =>
    this.recipes().filter((recipe) => {
      const categoryMatch = this.category() === 'Alles' || recipe.category === this.category();
      const searchMatch = `${recipe.name} ${recipe.description} ${recipe.allergens.join(' ')}`
        .toLowerCase()
        .includes(this.search().toLowerCase());
      return categoryMatch && searchMatch;
    }),
  );
  constructor() {
    this.recipesApi.getWeeklyMenu().subscribe((items) => this.recipes.set(items));
  }
  toggleRecipe(id: string): void {
    this.selected.update((current) =>
      current.includes(id) ? current.filter((item) => item !== id) : [...current, id],
    );
  }
  signOut(): void {
    this.auth.signOut();
    void this.router.navigate(['/login']);
  }
}
