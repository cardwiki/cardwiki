export class CardContent implements CardTextContent {
  constructor(
    public textFront: string,
    public textBack: string,
    public imageFrontUrl: string,
    public imageBackUrl: string
  ) {}
}
