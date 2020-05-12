export class RevisionSimple {
    constructor(
      public id: number,
      public message: string,
      public createdBy: string,
      public createdAt: Date) {
    }
  }