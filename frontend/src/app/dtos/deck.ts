import {CardContent} from './cardContent';
import {Category} from './category';

export class Deck {
  constructor(
    public id: number,
    public name: string,
    public createdBy: number,
    public createdAt: string,
    public updatedAt: string,
    public cards: CardContent[],
    public categories: Category[]
  ) {
  }
}
