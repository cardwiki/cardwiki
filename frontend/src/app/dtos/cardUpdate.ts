import { CardContent } from './cardContent';
import {Image} from './image';

export class CardUpdate extends CardContent {
  constructor(
    public textFront: string,
    public textBack: string,
    public imageFront: Image,
    public imageBack: Image,
    public message: string = null,
  ) {
      super(textFront, textBack, imageFront, imageBack)
  }
}
