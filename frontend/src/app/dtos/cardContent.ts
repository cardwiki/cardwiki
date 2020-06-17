import {Image} from './image';

export class CardContent {
  constructor(
    public id: number,
    public textFront: string,
    public textBack: string,
    public imageFront: Image,
    public imageBack: Image) {
  }
}
