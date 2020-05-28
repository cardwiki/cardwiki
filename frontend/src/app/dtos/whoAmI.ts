export class WhoAmI {
    constructor(
        public authId: string,
        public userId: number,
        public admin: boolean,
        public hasAccount: boolean) {
    }
}
