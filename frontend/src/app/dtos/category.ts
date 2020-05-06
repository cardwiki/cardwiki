export class Category {
  constructor(
    public name: string,
    public parentId: number = null,
    public createdBy: number = 1,
    public id: number = null,
    public createdAt: string = null,
    public updatedAt: string = null) {
  }
}
