import {CardContent} from './cardContent';

export class CardSimple extends CardContent {
  constructor(
    public id: number,
    public textFront: string,
    public textBack: string) {
    super(textFront, textBack);
  }
}
