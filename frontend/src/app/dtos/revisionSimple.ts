export class RevisionSimple {
    constructor(
      public id: number,
      public message: string,
      public createdBy: number,
      public createdAt: Date) {
    }
  }
