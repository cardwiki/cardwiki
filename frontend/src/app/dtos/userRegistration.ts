export class UserRegistration {
    constructor(
        public id: number,
        public authId: string,
        public username: string,
        public description: string,
        public createdAt: Date,
        public updatedAt: Date,
        public isAdmin: boolean) {
    }
}
