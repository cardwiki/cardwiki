import {Image} from './image';

export class CardUpdateContent implements CardTextContent {
  constructor(
    public textFront: string,
    public textBack: string,
    public imageFront: Image,
    public imageBack: Image) {}
}
