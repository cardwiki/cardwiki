export class LearnAttempt {
  constructor(
    public cardId: number,
    public status: AttemptStatus
  ) { }
}

export enum AttemptStatus {
  AGAIN = 'AGAIN',
  GOOD = 'GOOD',
  EASY = 'EASY',
}
