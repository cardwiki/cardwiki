import { Pageable } from './pageable';

export class Page<T> {
    constructor(
        public content: T[],
        public numberOfElements: number,
        public totalElements: number,
        public totalPages: number,
        public first: boolean,
        public last: boolean,
        public pageable: Pageable,
    ) {}
}