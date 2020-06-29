export class UserProfile {
  constructor(
    public id: number,
    public username: string,
    public description: string,
    public createdAt: Date,
    public updatedAt: Date,
    public admin: boolean,
    public enabled: boolean,
    public deleted: boolean,
    public theme: string) {
  }
}
