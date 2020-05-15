import {DeckSimple} from './deckSimple';

export class Category {
  constructor(
    public name: string,
    public parent: Category = null,
    public id: number = null,
    public createdBy: string = null,
    public createdAt: string = null,
    public updatedAt: string = null,
    public children: Category[] = [],
    public decks: DeckSimple[] = []) {
  }
}
