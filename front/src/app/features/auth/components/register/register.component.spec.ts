import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';
import { jest } from '@jest/globals';

import { RegisterComponent } from './register.component';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { routes } from '../../auth-routing.module';

describe('RegisterComponent', () => {
  let registerComponent: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let router: Router;
  let authService: AuthService;
  let httpTestingController: HttpTestingController;

  let email : HTMLInputElement;
  let password : HTMLInputElement;
  let firstName : HTMLInputElement;
  let lastName : HTMLInputElement;
  let register : DebugElement;
  
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        HttpClientTestingModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        RouterTestingModule.withRoutes(routes)
      ],
      providers: [
        { provide: Router, useValue: { navigate: jest.fn() } }, // Mock del Router
        AuthService
      ]
    })
      .compileComponents();

    httpTestingController = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(RegisterComponent);
    registerComponent = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
    registerComponent = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    fixture.detectChanges();

    email = fixture.debugElement.query(By.css('input[formControlName="email"]')).nativeElement;
    password = fixture.debugElement.query(By.css('input[formControlName="password"]')).nativeElement;
    firstName = fixture.debugElement.query(By.css('input[formControlName="firstName"]')).nativeElement;
    lastName = fixture.debugElement.query(By.css('input[formControlName="lastName"]')).nativeElement;
    register = fixture.debugElement.query(By.css('.register-form'));
  });

  it('should create RegisterComponent', () => {
    expect(registerComponent).toBeTruthy();
  });

  it('should navigate to the login page', async () => {
    const navigateSpy = jest
      .spyOn(router, 'navigate')
      .mockImplementation(async () => true);

    const authSpy = jest
      .spyOn(authService, 'register')
      .mockImplementation(() => of(undefined));

    registerComponent.submit();
    expect(authSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });

  it('should show an error when the form is empty', () => {
    const formElement: HTMLElement = fixture.nativeElement;
    const submitButton = formElement.querySelector<HTMLButtonElement>(
      'button[type="submit"]'
    );
    if (!submitButton) {
      throw new Error('Submit button not found');
    }

    submitButton.click();
    fixture.detectChanges();

    expect(formElement.querySelectorAll('input.ng-invalid')).toHaveLength(4);
  });

  it('should indicate error', () => {
    const errorMessage = 'An error occurred';
    jest
      .spyOn(authService, 'register')
      .mockImplementation(() => throwError(errorMessage));

    registerComponent.submit();

    expect(registerComponent.onError).toBe(true);
  });

  it('should show an error when there is an error', () => {
    registerComponent.onError = true;
    fixture.detectChanges();
    const formElement: HTMLElement = fixture.nativeElement;
    const errorMessage = formElement.querySelector('span.error');
    expect(errorMessage).toBeTruthy();
    expect(errorMessage!.textContent).toContain('An error occurred');
  });

  it('should call form methods and redirect to login page', async () => {
    jest.spyOn(router, 'navigate').mockImplementation(async () => true);

    email.value = 'john.doe@example.com';
    email.dispatchEvent(new Event('input'));
    password.value = 'password';
    password.dispatchEvent(new Event('input'));
    firstName.value = 'John';
    firstName.dispatchEvent(new Event('input'));
    lastName.value = 'Doe';
    lastName.dispatchEvent(new Event('input'));

    register.triggerEventHandler('ngSubmit', null);
    await fixture.whenStable();
    fixture.detectChanges();

    const req = httpTestingController.expectOne('api/auth/register');

    req.flush(null);

    expect(registerComponent.form.valid).toBeTruthy();
    expect(registerComponent.onError).toBeFalsy();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);

    // After every test, assert that there are no more pending requests.
    httpTestingController.verify();
  });

  

  it('should unsuccessfull register due to invalid inputs', async () => {
    email.value = 'invalidEmail';
    email.dispatchEvent(new Event('input'));
    password.value = '';
    password.dispatchEvent(new Event('input'));
    firstName.value = '';
    firstName.dispatchEvent(new Event('input'));
    lastName.value = '';
    lastName.dispatchEvent(new Event('input'));

    register.triggerEventHandler('ngSubmit', null);
    await fixture.whenStable();
    fixture.detectChanges();

    expect(registerComponent.form.controls['email'].valid).toBeFalsy();
    expect(registerComponent.form.controls['password'].valid).toBeFalsy();
    expect(registerComponent.form.controls['firstName'].valid).toBeFalsy();
    expect(registerComponent.form.controls['lastName'].valid).toBeFalsy();
  });
});