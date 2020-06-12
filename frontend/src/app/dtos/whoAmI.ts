export class WhoAmI {
    constructor(
        public id: number,
        public username: string,
        public authId: string,
        public userId: number,
        public admin: boolean,
        public hasAccount: boolean) {
    }
}
