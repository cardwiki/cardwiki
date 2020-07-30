import { Image } from './image';

export class CardUpdate implements CardTextContent {
  constructor(
    public textFront: string,
    public textBack: string,
    public imageFront: Image,
    public imageBack: Image,
    public message: string = null
  ) {}
}
