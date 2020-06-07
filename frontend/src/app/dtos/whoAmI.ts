export class WhoAmI {
    constructor(
        public authId: string,
        public userId: number,
        public isAdmin: boolean,
        public hasAccount: boolean) {
    }
}
