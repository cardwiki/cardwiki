export class LearnAttempt {
  constructor(
    public cardId: number,
    public status: AttemptStatus,
    public reverse: boolean,
  ) { }
}

export enum AttemptStatus {
  AGAIN = 'AGAIN',
  GOOD = 'GOOD',
  EASY = 'EASY',
}
