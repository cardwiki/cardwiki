export class WhoAmI {
    constructor(
        public id: number,
        public authId: string,
        public admin: boolean,
        public hasAccount: boolean) {
    }
}
