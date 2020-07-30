import { RevisionType } from './revisionSimple';
import { DeckSimple } from './deckSimple';
import { Image } from './image';
import { UserSimple } from './userSimple';

export class RevisionDetailed {
  constructor(
    public id: number,
    public type: RevisionType,
    public message: string,
    public textFront: string,
    public textBack: string,
    public imageFront: Image,
    public imageBack: Image,
    public createdAt: Date,
    public cardId: number,
    public deck: DeckSimple,
    public createdBy: UserSimple
  ) {}
}
