export class Deck {
  constructor(
    public id: number,
    public name: string,
    public createdBy: string,
    public createdAt: string,
    public updatedAt: string,
    public categories: {id: number, name: string}[]
  ) {
  }
}
