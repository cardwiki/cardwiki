import {CardContent} from './cardContent';
import { DeckSimple } from './deckSimple';
import {Image} from './image';

export class CardSimple extends CardContent {
  constructor(
    public id: number,
    public deck: DeckSimple,
    public textFront: string,
    public textBack: string,
    public imageFront: Image,
    public imageBack: Image) {
    super(id, textFront, textBack, imageFront, imageBack);
  }
}
