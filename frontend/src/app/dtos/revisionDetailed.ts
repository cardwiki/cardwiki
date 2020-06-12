import {CardSimple} from "./cardSimple";
import { RevisionSimple, RevisionType } from './revisionSimple';

export class RevisionDetailed {
  constructor(
    public id: number,
    public type: RevisionType,
    public message: string,
    public createdAt: Date,
    public card: CardSimple,
  ) {}
}
