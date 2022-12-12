import { TestBed } from '@angular/core/testing';

import { AuthorserviceService } from './authorservice.service';

describe('AuthorserviceService', () => {
  let service: AuthorserviceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthorserviceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
