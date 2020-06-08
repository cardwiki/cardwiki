import { CategorySimple } from './categorySimple';

export class DeckUpdate {
  constructor(
    public name: string,
    public categories: CategorySimple[]) {
  }
}
