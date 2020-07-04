import { CategorySimple } from './categorySimple';
import { DeckSimple } from './deckSimple';

export class DeckDetails extends DeckSimple {
  constructor(
    public id: number,
    public name: string,
    public createdBy: number,
    public createdAt: string,
    public updatedAt: string,
    public categories: CategorySimple[]
  ) {
    super(id, name);
  }
}
