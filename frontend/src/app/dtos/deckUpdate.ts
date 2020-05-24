import {DeckSimple} from './deckSimple';
import {Category} from './category';

export class DeckUpdate extends DeckSimple {
  constructor(
    public id: number,
    public name: string,
    public categories: {id: number, name: string}[]) {
    super(id, name);
  }
}
