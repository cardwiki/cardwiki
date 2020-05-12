export class UserRegistration {
    constructor(
        public oAuthId: string,
        public username: string,
        public description: string,
        public createdAt: Date,
        public updatedAt: Date,
        public isAdmin: boolean) {
    }
}