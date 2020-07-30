import { CategorySimple } from './categorySimple';

export class CategoryUpdate {
  constructor(public name: string, public parent: CategorySimple) {}
}
