export type Role = 'KLANT' | 'MEDEWERKER' | 'CHAUFFEUR';

export interface Recipe {
  id: string;
  name: string;
  description: string;
  image: string;
  category: string;
  prepTimeMinutes: number;
  servings: number;
  allergens: string[];
  tags: string[];
}
