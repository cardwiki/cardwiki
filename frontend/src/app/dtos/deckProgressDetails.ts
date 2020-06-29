export class DeckProgressDetails {
  constructor(
    public deckId: number,
    public deckName: string,
    public newCount: number,
    public learningCount: number,
    public toReviewCount: number
  ) {
  }
}
