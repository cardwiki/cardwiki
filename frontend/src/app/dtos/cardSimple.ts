import {CardContent} from './cardContent';

export class CardSimple extends CardContent {
  constructor(
    public id: number,
    public textFront: string,
    public textBack: string,
    public imageFrontUrl: string,
    public imageBackUrl: string) {
    super(textFront, textBack, imageFrontUrl, imageBackUrl);
  }
}
