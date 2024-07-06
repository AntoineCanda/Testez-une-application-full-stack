import * as cypress from 'cypress';

describe('Me Component', () => {
  describe('User admin', () => {
    beforeEach(() => {
      cy.visit('login');
      cy.intercept('POST', '/api/auth/login', {
        body: {
          token: 'jwt',
          type: 'Bearer',
          id: 1,
          username: 'userName',
          firstName: 'firstName',
          lastName: 'lastName',
          admin: true,
          createdAt: '2024-07-02T18:00:00',
          updatedAt: '2024-07-02T18:00:00',
        },
      });

      cy.intercept('GET', '/api/session').as('Session');

      cy.get('input[formControlName=email]').type('yoga@studio.com');
      cy.get('input[formControlName=password]').type(
        `${'test!1234'}{enter}{enter}`
      );

      cy.url().should('include', '/sessions');

      cy.intercept('GET', '/api/user/1', {
        body: {
          id: 1,
          username: 'userName',
          email: 'yoga@studio.com',
          firstName: 'firstName',
          lastName: 'lastName',
          admin: true,
          createdAt: '2024-07-02T18:00:00',
          updatedAt: '2024-07-02T18:00:00',
        },
      }).as('user');

      cy.get('span.link').contains('Account').click();
      cy.url().should('include', '/me');
    });

    afterEach(() => {});

    it('should access account page', () => {
      cy.get('.mat-card-title').should('contain', 'User information');
    });

    it('should display "You are admin"', () => {
      cy.get('.mat-card-content').should('contain', 'You are admin');
    });

    it('should display account information', () => {
      cy.get('p').eq(0).should('contain', 'Name: firstName LASTNAME');
      cy.get('p').eq(1).should('contain', 'Email: yoga@studio.com');
      cy.get('p').eq(2).should('contain', 'You are admin');
      cy.get('p').eq(3).should('contain.text', 'Create at:  July 2, 2024');
      cy.get('p').eq(4).should('contain.text', 'Last update:  July 2, 2024');
    });

    it('should not display delete button', () => {
      cy.get('button[mat-raised-button]').should('not.exist');
    });

    it('should navigate back when back button is clicked', () => {
      cy.get('button').contains('arrow_back').click();
      cy.url().should('not.include', '/me');
    });
  });

  describe('User not admin', () => {
    beforeEach(() => {
      cy.visit('login');
      cy.intercept('POST', '/api/auth/login', {
        body: {
          token: 'jwt',
          type: 'Bearer',
          id: 2,
          username: 'userName',
          firstName: 'firstName',
          lastName: 'lastName',
          admin: false,
          createdAt: '2024-05-02T18:35:00',
          updatedAt: '2024-05-03T18:35:00',
        },
      });

      cy.intercept('GET', '/api/session').as('session');

      cy.get('input[formControlName=email]').type('test@studio.com');
      cy.get('input[formControlName=password]').type(
        `${'password1234'}{enter}{enter}`
      );

      cy.url().should('include', '/sessions');

      cy.intercept('GET', '/api/user/2', {
        body: {
          id: 2,
          username: 'userName',
          email: 'test@studio.com',
          firstName: 'firstName',
          lastName: 'lastName',
          admin: false,
          createdAt: '2024-05-02T18:35:00',
          updatedAt: '2024-05-03T18:35:00',
        },
      }).as('user');

      cy.get('span.link').contains('Account').click();
      cy.url().should('include', '/me');
    });

    afterEach(() => {});

    it('should access account page', () => {
      cy.get('.mat-card-title').should('contain', 'User information');
    });

    it('should not display "You are admin"', () => {
      cy.get('p').eq(2).should('not.contain', 'You are admin');
    });

    it('should display account information', () => {
      cy.get('p').eq(0).should('contain', 'Name: firstName LASTNAME');
      cy.get('p').eq(1).should('contain', 'Email: test@studio.com');
      cy.get('p').eq(2).should('contain', `Delete my account:`);
      cy.get('p').eq(3).should('contain.text', 'Create at:  May 2, 2024');
      cy.get('p').eq(4).should('contain.text', 'Last update:  May 3, 2024');
    });

    it('should delete account', () => {
      cy.intercept('DELETE', '/api/user/2', { statusCode: 200 });

      cy.contains('button', 'delete').click();

      cy.get('.mat-snack-bar-container')
        .contains('Your account has been deleted !')
        .should('exist');
      cy.url().should('eq', 'http://localhost:4200/');
      cy.get('[routerLink="login"]').should('exist');
      cy.get('[routerLink="register"]').should('exist');
    });

    it('should display delete button', () => {
      cy.get('button[mat-raised-button]').should('exist');
    });

    it('should navigate back when back button is clicked', () => {
      cy.get('button').contains('arrow_back').click();
      cy.url().should('not.include', '/me');
    });
  });
});
