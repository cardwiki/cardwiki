import { RevisionSimple } from './revisionSimple';
import { DeckSimple } from './deckSimple';
import { CardSimple } from './cardSimple';

export class CardDetails extends CardSimple {
  constructor(
    public id: number,
    public deck: DeckSimple,
    public textFront: string,
    public textBack: string,
    public revisions: RevisionSimple[]) {
      super(id, deck, textFront, textBack);
  }
}
