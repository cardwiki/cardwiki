export class WhoAmI {
    constructor(
        public id: number,
        public username: string,
        public authId: string,
        public userId: number,
        public isAdmin: boolean,
        public hasAccount: boolean) {
    }
}
