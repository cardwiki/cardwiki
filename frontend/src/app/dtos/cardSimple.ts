import {CardContent} from './cardContent';
import { DeckSimple } from './deckSimple';

export class CardSimple extends CardContent {
  constructor(
    public id: number,
    public deck: DeckSimple,
    public textFront: string,
    public textBack: string,
    public imageFrontUrl: string,
    public imageBackUrl: string) {
    super(textFront, textBack, imageFrontUrl, imageBackUrl);
  }
}
