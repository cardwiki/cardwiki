export class RevisionSimple {
    constructor(
      public id: number,
      public type: RevisionType,
      public message: string,
      public createdBy: number,
      public createdAt: Date) {
    }
  }
  
export enum RevisionType {
  CREATE = 'CREATE',
  EDIT = 'EDIT',
  DELETE = 'DELETE',
}
