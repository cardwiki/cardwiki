import { DeckProgress } from './deckProgress';

export class DeckProgressDetails {
  constructor(
    public deckId: number,
    public deckName: string,
    public normal: DeckProgress,
    public reverse: DeckProgress,
  ) {
  }
}
