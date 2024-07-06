import * as cypress from 'cypress';

describe('Login spec', () => {
  it('should display the Login form', () => {
    cy.visit('login');
    cy.get('.login').should('exist');
    cy.get('mat-card-title').should('contain', 'Login');
    cy.get('.login-form').should('exist');
  });

  it('should display all input fields', () => {
    cy.visit('login');
    cy.get('input[formControlName=email]').should('exist');
    cy.get('input[formControlName=password]').should('exist');
  });

  it('should disabled button when email is empty', () => {
    cy.visit('login');
    cy.get('input[formControlName=email]').type('{enter}');
    cy.get('button').should('be.disabled');
  });

  it('Login successfull', () => {
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true,
      },
    });

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []
    ).as('session');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(
      `${'test!1234'}{enter}{enter}`
    );

    cy.url().should('include', '/sessions');
  });

  it('Login failed because of wrong email and wrong password', () => {
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 400,
      body: {
        message: 'Unauthorized',
      },
    });

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []
    ).as('session');

    cy.get('input[formControlName=email]').type('yoga@example.com');
    cy.get('input[formControlName=password]').type(
      `${'password'}{enter}{enter}`
    );

    cy.url().should('include', '/login');
    cy.get('p[class="error ng-star-inserted"]').should(
      'contain',
      'An error occurred'
    );
  });

  it('Login failed because the password is less than 3 caracters', () => {
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 400,
      body: {
        message: 'Unauthorized',
      },
    });

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []
    ).as('session');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(`${'te'}{enter}{enter}`);

    cy.url().should('include', '/login');
    cy.get('p[class="error ng-star-inserted"]').should(
      'contain',
      'An error occurred'
    );
  });

  it('Login failed because of malformed email', () => {
    //Given
    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 400,
      body: {
        message: 'Unauthorized',
      },
    });

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []
    ).as('session');
    //When
    cy.get('input[formControlName=email]').type('yoga.studio.com');
    cy.get('input[formControlName=password]').type(
      `${'test!1234'}{enter}{enter}`
    );
    //Then
    cy.url().should('include', '/login');
    cy.get('p[class="error ng-star-inserted"]').should(
      'contain',
      'An error occurred'
    );
  });

  it('should log out user after log in', () => {
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true,
      },
    });

    cy.intercept('GET', '/api/session').as('Session');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(
      `${'test!1234'}{enter}{enter}`
    );

    cy.url().should('include', '/sessions');
    cy.contains('Logout').click();
    cy.url().should('include', '/');
    cy.contains('Login');
    cy.contains('Register');
    cy.contains('Logout').should('not.exist');
  });
});
