import {DeckSimple} from './deckSimple';
import { CategorySimple } from './categorySimple';

export class CategoryDetails extends CategorySimple {
  constructor(
    public id: number = null,
    public name: string = null,
    public createdBy: number = null,
    public parent: CategorySimple = null,
    public children: CategorySimple[] = [],
    public createdAt: string = null,
    public updatedAt: string = null,
    public decks: DeckSimple[] = []) {
      super(id, name);
  }
}
