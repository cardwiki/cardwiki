export class CardUpdateDto implements CardTextContent {
  constructor(
    public textFront: string,
    public textBack: string,
    public imageFrontFilename: string,
    public imageBackFilename: string,
    public message: string = null) {}
}
