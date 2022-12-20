import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthorbooksComponent } from './authorbooks.component';

describe('AuthorbooksComponent', () => {
  let component: AuthorbooksComponent;
  let fixture: ComponentFixture<AuthorbooksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AuthorbooksComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AuthorbooksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
