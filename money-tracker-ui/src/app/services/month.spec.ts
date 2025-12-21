import { TestBed } from '@angular/core/testing';

import { Month } from './month';

describe('Month', () => {
  let service: Month;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Month);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
