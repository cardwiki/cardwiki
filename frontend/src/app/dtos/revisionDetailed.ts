import {CardSimple} from "./cardSimple";

export class RevisionDetailed {
  constructor(
    public id: number,
    public message: string,
    public createdAt: Date,
    public card: CardSimple) {
  }
}
