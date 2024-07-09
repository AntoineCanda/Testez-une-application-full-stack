describe('Guard and router test', () => {
  beforeEach(() => {
    cy.visit('/sessions');
  });

  afterEach(() => {});

  it('should redirect on login page when trying to go on session page and not being logged in', () => {
    cy.url().should('eq', 'http://localhost:4200/login');
  });

  it('should display the Login form', () => {
    cy.visit('login');
    cy.get('.login').should('exist');
    cy.get('mat-card-title').should('contain', 'Login');
    cy.get('.login-form').should('exist');
  });
});
