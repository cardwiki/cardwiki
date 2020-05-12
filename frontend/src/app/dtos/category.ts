export class Category {
  constructor(
    public name: string,
    public parent: Category = null,
    public id: number = null,
    public createdBy: number = null,
    public createdAt: string = null,
    public updatedAt: string = null) {
  }
}