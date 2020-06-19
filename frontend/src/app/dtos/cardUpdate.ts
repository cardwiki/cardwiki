import {Image} from './image';
import {CardUpdateContent} from './cardUpdateContent';

export class CardUpdate extends CardUpdateContent {
  constructor(
    public textFront: string,
    public textBack: string,
    public imageFront: Image,
    public imageBack: Image,
    public message: string = null) {
      super(textFront, textBack, imageFront, imageBack)
  }
}
