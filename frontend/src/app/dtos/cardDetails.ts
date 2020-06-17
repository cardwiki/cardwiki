import { RevisionSimple } from './revisionSimple';
import { DeckSimple } from './deckSimple';
import { CardSimple } from './cardSimple';
import {Image} from './image';

export class CardDetails extends CardSimple {
  constructor(
    public id: number,
    public deck: DeckSimple,
    public textFront: string,
    public textBack: string,
    public imageFront: Image,
    public imageBack: Image,
    public revisions: RevisionSimple[]) {
      super(id, deck, textFront, textBack, imageFront, imageBack);
  }
}
