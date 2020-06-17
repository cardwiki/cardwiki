import { CardContent } from './cardContent';

export class CardUpdate extends CardContent {
  constructor(
    public textFront: string,
    public textBack: string,
    public message: string = null,
  ) {
      super(textFront, textBack)
  }
}
