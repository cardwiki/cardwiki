import { TestBed } from '@angular/core/testing';

import { LearnService } from './learn.service';

describe('LearnService', () => {
  let service: LearnService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LearnService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
