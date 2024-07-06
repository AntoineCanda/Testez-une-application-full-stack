import * as cypress from 'cypress';
describe('Register Component', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  afterEach(() => {});

  it('should display the Register form', () => {
    cy.get('.register').should('exist');
    cy.get('mat-card-title').should('contain', 'Register');
    cy.get('.register-form').should('exist');
  });

  it('should display all input fields', () => {
    cy.get('input[formControlName="firstName"]').should('exist');
    cy.get('input[formControlName="lastName"]').should('exist');
    cy.get('input[formControlName="email"]').should('exist');
    cy.get('input[formControlName="password"]').should('exist');
  });

  it('should register successfully and redirect to the login page', () => {
    cy.visit('/register');

    cy.intercept('POST', '/api/auth/register', { statusCode: 200 });

    cy.get('input[formControlName=firstName]').type('firstName');
    cy.get('input[formControlName=lastName]').type('lastName');
    cy.get('input[formControlName=email]').type('email@test.fr');
    cy.get('input[formControlName=password]').type(
      `${'password'}{enter}{enter}`
    );
    cy.url().should('include', '/login');
  });

  it('should disabled button when field is empty', () => {
    cy.get('input[formControlName=firstName]').type('firstName');
    cy.get('input[formControlName=lastName]').type(`{enter}{enter}`);
    cy.get('input[formControlName=lastName]').should(
      'have.class',
      'ng-invalid'
    );

    cy.get('button[type=submit]').should('be.disabled');
  });

  it('should display an error message if the registration fails', () => {
    cy.visit('/register');

    cy.intercept('POST', '/api/auth/register', { statusCode: 500 });

    cy.get('input[formControlName=firstName]').type('firstName');
    cy.get('input[formControlName=lastName]').type('lastName');
    cy.get('input[formControlName=email]').type('email@test.fr');
    cy.get('input[formControlName=password]').type(
      `${'password'}{enter}{enter}`
    );
    cy.get('.error').should('be.visible');
  });

  it('should display an error message for invalid input', () => {
    cy.get('[formControlName="firstName"]').type('T');
    cy.get('[formControlName="lastName"]').type('T');
    cy.get('[formControlName="email"]').type('T');
    cy.get('[formControlName="password"]').type('T');

    cy.contains('Submit').should('be.disabled');
  });

  it('should display an error if email already exists', () => {
    cy.intercept('POST', '/api/auth/register', { statusCode: 400 });

    cy.get('input[formControlName=firstName]').type('firstName');
    cy.get('input[formControlName=lastName]').type('lastName');
    cy.get('[formControlName="email"]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(
      `${'password'}{enter}{enter}`
    );

    cy.get('.error').should('be.visible');
  });
});
