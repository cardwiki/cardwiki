import { HttpParams } from '@angular/common/http';

export class Pageable {
    constructor(
        public pageNumber: number, // 0-indexed page number
        public pageSize: number,
    ) {}

    toHttpParams(): HttpParams {
        return new HttpParams({
            fromObject: this.toObject()
        })
    }

    toObject(): { [k: string]: string } {
        return {
            limit: String(this.pageSize),
            offset: String(this.pageNumber),
        }
    }
}