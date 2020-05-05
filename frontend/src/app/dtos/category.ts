export class Category {
  constructor(
    public id: number,
    public name: string,
    public parentId: number,
    public createdBy: number,
    public createdAt: string,
    public updatedAt: string) {
  }
}
