import {CardSimple} from "./cardSimple";
import { RevisionSimple, RevisionType } from './revisionSimple';
import {DeckSimple} from './deckSimple';

export class RevisionDetailed {
  constructor(
    public id: number,
    public type: RevisionType,
    public message: string,
    public textFront: string,
    public textBack: string,
    public createdAt: Date,
    public cardId: number,
    public deck: DeckSimple,
  ) {}
}
