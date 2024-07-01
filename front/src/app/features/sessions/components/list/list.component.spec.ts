import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { ListComponent } from './list.component';
import { SessionApiService } from '../../services/session-api.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true
    }
  }

  let sessionApiService : SessionApiService;
  let httpTestingController : HttpTestingController;
  
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [HttpClientModule, MatCardModule, MatIconModule, HttpClientTestingModule, RouterTestingModule],
      providers: [{ provide: SessionService, useValue: mockSessionService }, SessionApiService]
    })
      .compileComponents();

    sessionApiService = TestBed.inject(SessionApiService);
    httpTestingController = TestBed.inject(HttpTestingController);

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get sessions', () => {
    const req = httpTestingController.expectOne('api/session');
    req.flush([{
      id: 1,
      name: 'session test',
      description: 'description test',
      date: new Date(),
      teacher_id: 1,
      users: [1],
      createdAt: new Date(),
      updatedAt: new Date(),
    }]);

    expect(req.request.method).toBe('GET');
    fixture.detectChanges();

    const cardTitle : HTMLElement = fixture.debugElement.query(By.css('.item mat-card-header mat-card-title')).nativeElement;
    expect(cardTitle).toBeTruthy();
    expect(cardTitle.textContent).toBe('session test');

  });

  it('should see create and edit on session element when logged as admin user and see detail button', () => {
    const req = httpTestingController.expectOne('api/session');
    req.flush([{
      id: 1,
      name: 'session test',
      description: 'description test',
      date: new Date(),
      teacher_id: 1,
      users: [1],
      createdAt: new Date(),
      updatedAt: new Date(),
    }]);

    expect(req.request.method).toBe('GET');
    fixture.detectChanges();

    expect(component.user?.admin).toBeTruthy();

    const spanCreateButton = fixture.debugElement.query(By.css('.ml1')).nativeElement;
    expect(spanCreateButton).toBeTruthy();
    expect(spanCreateButton.textContent).toBe('Create');

    const cardButtons : DebugElement[] = fixture.debugElement.queryAll(By.css('mat-card-actions button'));
    expect(cardButtons.length).toBeGreaterThan(0);
    expect(cardButtons[0].nativeNode.innerHTML).toContain('Detail');
    expect(cardButtons[1].nativeNode.innerHTML).toContain('Edit');
  });
});