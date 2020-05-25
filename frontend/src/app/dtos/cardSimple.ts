import {CardContent} from './cardContent';
import {DeckSimple} from "./deckSimple";

export class CardSimple extends CardContent {
  constructor(
    public id: number,
    public textFront: string,
    public textBack: string,
    public deck: DeckSimple) {
      super(textFront, textBack);
  }
}
