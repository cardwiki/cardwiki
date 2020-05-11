import { RevisionSimple } from './revisionSimple';
import { DeckSimple } from './deckSimple';
import { CardContent } from './cardContent';

export class CardDetails extends CardContent {
  constructor(
    public id: number,
    public deck: DeckSimple,
    public textFront: string,
    public textBack: string,
    public revisions: RevisionSimple[]) {
      super(textFront, textBack)
  }
}
