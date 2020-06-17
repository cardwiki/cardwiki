import {CardContent} from './cardContent';
import { DeckSimple } from './deckSimple';

export class CardSimple extends CardContent {
  constructor(
    public id: number,
    public deck: DeckSimple,
    public textFront: string,
    public textBack: string,
  ) {
    super(textFront, textBack);
  }
}
