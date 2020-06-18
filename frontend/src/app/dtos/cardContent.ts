import {Image} from './image';

export class CardContent {
  constructor(
    public textFront: string,
    public textBack: string,
    public imageFront: Image,
    public imageBack: Image) {}
}
