export class WhoAmI {
  constructor(
    public id: number,
    public username: string,
    public authId: string,
    public admin: boolean,
    public hasAccount: boolean
  ) {}
}
