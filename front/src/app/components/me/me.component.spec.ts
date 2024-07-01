import { MeComponent } from './me.component';
import { Observable, of } from 'rxjs';
import { SessionService } from '../../services/session.service';
import { UserService } from '../../services/user.service';
import { User } from '../../interfaces/user.interface';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

import { expect } from '@jest/globals';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { By } from '@angular/platform-browser';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  let httpTestingController : HttpTestingController;
  let sessionService : SessionService;
  let matSnackBar : MatSnackBar;
  let router : Router;

  const user: User = {
    id: 1,
    email: 'john.doe@example.com',
    password: 'test',
    firstName: 'John',
    lastName: 'Doe',
    admin: false,
    createdAt: new Date(),
    updatedAt: new Date(),
  };


  const sessionInformation : SessionInformation = {  
    token: 'token',
    type: 'type',
    id: 1,
    username: 'session@example.com',
    firstName: 'John',
    lastName: 'Doe',
    admin: false,
  }
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        HttpClientTestingModule,
        RouterTestingModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        SessionService
      ],
    }).compileComponents();

    httpTestingController = TestBed.inject(HttpTestingController);
    sessionService = TestBed.inject(SessionService);
    matSnackBar = TestBed.inject(MatSnackBar);
    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;

    sessionService.sessionInformation = sessionInformation;
    fixture.detectChanges();

    httpTestingController.expectOne('api/user/1').flush(user);
    
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should go back', () => {
    const back = jest.spyOn(window.history, 'back');
    component.back();
    expect(back).toHaveBeenCalled();
  });
  
  it('should display user session information', () => {
    const innerHtmlComponent = fixture.debugElement.nativeElement.innerHTML;
    expect(innerHtmlComponent).toContain('Name: John DOE');
    expect(innerHtmlComponent).toContain('Email: john.doe@example.com');
    expect(innerHtmlComponent).toContain('Delete my account:');
  });

  it('should successfully delete user account', async() => {
    jest.spyOn(matSnackBar, 'open').mockImplementation();
    jest.spyOn(sessionService, 'logOut').mockImplementation();
    jest.spyOn(router, 'navigate').mockImplementation();

    let deleteBtn = fixture.debugElement.query(By.css('div[class="my2"] button'));
    expect(deleteBtn).toBeTruthy();
    deleteBtn.triggerEventHandler('click', null);
    await fixture.whenStable();
    fixture.detectChanges();

    const deleteReq = httpTestingController.expectOne('api/user/1');
    deleteReq.flush(null);

    expect(deleteReq.request.method).toBe('DELETE');

    expect(matSnackBar.open).toHaveBeenCalledWith("Your account has been deleted !", 'Close', { duration: 3000 });
    expect(sessionService.logOut).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should display admin user session information', () => {
    expect(component.user).toBeTruthy();
    component.user!.admin = true;
    fixture.detectChanges();

    const innerHtmlComponent = fixture.debugElement.nativeElement.innerHTML;
    expect(innerHtmlComponent).not.toContain('Delete my account:');
    expect(innerHtmlComponent).toContain('You are admin');
  });

  
});