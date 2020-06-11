export class UserProfile {
  constructor(
    public id: number,
    public username: string,
    public description: string,
    public createdAt: Date,
    public updatedAt: Date,
    public isAdmin: boolean) {
  }
}
