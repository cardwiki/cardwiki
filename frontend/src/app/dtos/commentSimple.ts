import { UserSimple } from './userSimple'

export class CommentSimple {
  constructor(
    public id: number,
    public message: string,
    public createdAt: string,
    public updatedAt: string,
    public createdBy: UserSimple,
  ) {}
}
