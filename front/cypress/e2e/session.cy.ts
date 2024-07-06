import * as cypress from 'cypress';

describe('Session Components', () => {
  const session1 = {
    id: 1,
    name: 'Session 1',
    date: '2024-07-02T17:30:00',
    teacher_id: 1,
    description: 'Description 1',
    users: [2, 4],
    createdAt: '2024-06-30T12:00:00',
    updatedAt: '2024-07-01T18:30:00',
  };

  const session2 = {
    id: 2,
    name: 'Session 2',
    date: '2024-07-02T18:30:00',
    teacher_id: 1,
    description: 'Yoga session description 2',
    users: [2, 4],
    createdAt: '2024-06-03T20:30:00',
    updatedAt: '2024-06-04T12:30:00',
  };

  const session4 = {
    id: 4,
    name: 'Session 4',
    date: '2024-07-02T18:30:00',
    teacher_id: 1,
    description: 'Session 4 description',
    users: [2, 4],
    createdAt: '2024-06-10T18:00:00',
    updatedAt: '2024-06-15T12:30:00',
  };

  const sessionAdd = {
    id: 1,
    name: 'Session 1',
    date: '2024-06-02T18:35:00',
    teacher_id: 1,
    description: 'Yoga session description 1',
    users: [1, 2, 4],
    createdAt: '2024-05-02T18:35:00',
    updatedAt: '2024-05-03T18:35:00',
  };

  const teacher1 = {
    id: 1,
    lastName: 'Marley',
    firstName: 'Bob',
    createdAt: '2024-01-01T08:30:00',
    updatedAt: '2024-06-14T12:30:00',
  };

  const teacher2 = {
    id: 2,
    lastName: 'Grey',
    firstName: 'Alice',
    createdAt: '2024-01-01T08:30:00',
    updatedAt: '2024-06-14T12:30:00',
  };

  describe('User admin', () => {
    beforeEach(() => {
      cy.visit('login');

      cy.intercept('POST', '/api/auth/login', {
        body: {
          id: 1,
          username: 'StudioYoga',
          firstName: 'Studio',
          lastName: 'Yoga',
          admin: true,
        },
      }).as('Login');

      cy.intercept('GET', '/api/session', [session1, session2]).as('Sessions');

      cy.intercept('GET', '/api/teacher', [teacher1, teacher2]).as('Teachers');

      cy.get('input[formControlName=email]').type('yoga@studio.com');
      cy.get('input[formControlName=password]').type(
        `${'test!1234'}{enter}{enter}`
      );
    });

    it('should display the list of sessions', () => {
      cy.contains('Rentals available');
      cy.contains(session1.name);
      cy.contains(session1.description);
      cy.contains(session2.name);
      cy.contains(session1.description);
    });

    it('should display a button to create a session', () => {
      cy.get('[routerLink=create]').should('exist');
    });

    it('should create a session', () => {
      cy.intercept('POST', '/api/session', {
        body: {
          id: 3,
          name: 'Session 3',
          date: '2024-07-04',
          teacher_id: 1,
          description: 'Session description',
          users: [],
          createdAt: '2024-06-15T18:00:00',
          updatedAt: '2024-06-20T21:30:00',
        },
      }).as('Session create');

      cy.contains('Create').click();
      cy.url().should('eq', `${Cypress.config().baseUrl}sessions/create`);
      cy.get('input[formControlName=name]').type('Session 3');
      cy.get('input[formControlName=date]').type('2024-07-04');
      cy.get('mat-select[formControlName=teacher_id]').click();
      cy.get('mat-option')
        .contains(`${teacher1.firstName} ${teacher1.lastName}`)
        .click();
      cy.get('textarea[formControlName=description]').type(
        'Session description'
      );

      cy.get('button[type="submit"]').contains('Save').click();
      cy.get('snack-bar-container')
        .contains('Session created !')
        .should('exist');
      cy.get('.list mat-card').should('be.visible');
      cy.url().should('eq', `${Cypress.config().baseUrl}sessions`);
    });

    it('should disable save button if form fields are empty', () => {
      cy.contains('Create').click();
      cy.url().should('eq', `${Cypress.config().baseUrl}sessions/create`);

      cy.get('button[type="submit"]').should('be.disabled');
      cy.get('input[formControlName="name"]').type('Session 3');
      cy.get('button[type="submit"]').should('be.disabled');
    });

    it('should display a button to view details of a session', () => {
      cy.get('span[class="mat-button-wrapper"]').should('contain', 'Detail');
    });

    it('should display details of a session', () => {
      cy.intercept('GET', '/api/session/1', session1).as('Session');
      cy.intercept('GET', '/api/teacher/1', teacher1).as('Teacher');

      cy.get('button').contains('Detail').click();
      cy.url().should('include', 'detail/1');

      cy.get('.mat-card-title').contains('Session 1');
    });

    it('should display a button to edit details of a session', () => {
      cy.get('span[class="mat-button-wrapper"]').should('contain', 'Edit');
    });

    it('should edit a session', () => {
      cy.intercept('GET', '/api/session/1', session1).as('Session');

      cy.contains('Edit').first().click();

      cy.url().should('include', '/sessions/update/1');
      cy.contains('Update session');

      cy.get('input[formControlName=name]')
        .should('have.value', session1.name)
        .clear()
        .type(session4.name);

      cy.intercept('PUT', '/api/session/1', session4).as('Session 4');
      cy.intercept('GET', '/api/session', [session4, session2]).as('Session 4');

      cy.contains('Save').click();
      cy.contains('Session updated !');
      cy.contains('Close').click();
      cy.url().should('include', '/sessions');
    });

    it('should display a button to delete a session for admin', () => {
      cy.intercept('GET', '/api/session/1', session1).as('Session');
      cy.intercept('GET', '/api/teacher/1', teacher1).as('Teacher');

      cy.get('button').contains('Detail').click();
      cy.get('button').contains('Delete');
    });

    it('should delete a session', () => {
      cy.intercept('GET', '/api/session/1', session1).as('Session');
      cy.intercept('GET', '/api/teacher/1', teacher1).as('Teacher');
      cy.intercept('DELETE', '/api/session/1', session1).as('Session delete');

      cy.get('button').contains('Detail').click();
      cy.get('button').contains('Delete').click();

      cy.url().should('include', '/sessions');
      cy.contains('Session deleted !');
    });
  });

  describe('User not admin', () => {
    beforeEach(() => {
      cy.visit('login');

      cy.intercept('POST', '/api/auth/login', {
        body: {
          id: 1,
          username: 'userName',
          firstName: 'firstName',
          lastName: 'lastName',
          admin: false,
        },
      }).as('Login');

      cy.intercept('GET', '/api/session', [session1, session2]).as('Sessions');

      cy.intercept('GET', '/api/teacher', [teacher1, teacher2]).as('Teachers');

      cy.get('input[formControlName=email]').type('user@studio.com');
      cy.get('input[formControlName=password]').type(
        `${'test!1234'}{enter}{enter}`
      );
    });

    it('should display the list of sessions', () => {
      cy.contains('Rentals available');
      cy.contains(session1.name);
      cy.contains(session1.description);
      cy.contains(session2.name);
      cy.contains(session1.description);
    });

    it('should not display a button to create a session', () => {
      cy.get('[routerLink=create]').should('not.exist');
    });
    it('should not display a button to edit details of a session', () => {
      cy.get('span[class="mat-button-wrapper"]').should('not.contain', 'Edit');
    });

    it('should display a button to view details of a session', () => {
      cy.get('span[class="mat-button-wrapper"]').should('contain', 'Detail');
    });

    it('should display details of a session', () => {
      cy.intercept('GET', '/api/session/1', session1).as('Session');
      cy.intercept('GET', '/api/teacher/1', teacher1).as('Teacher');

      cy.get('button').contains('Detail').click();
      cy.url().should('include', 'detail/1');

      cy.get('.mat-card-title').contains('Session 1');
    });

    it('should display a button to participate at a session', () => {
      cy.intercept('GET', '/api/session/1', session1).as('Session');
      cy.intercept('GET', '/api/teacher/1', teacher1).as('Teacher');

      cy.get('button').contains('Detail').click();
      cy.url().should('include', 'detail/1');
      cy.get('span[class="mat-button-wrapper"]').should(
        'contain',
        'Participate'
      );
    });

    it('should participate to a session', () => {
      cy.intercept('GET', '/api/session/1', session1).as('Session');
      cy.intercept('GET', '/api/teacher/1', teacher1).as('Teacher');
      cy.intercept('POST', '/api/session/1/participate/1', {}).as(
        'Participate'
      );

      cy.contains('Detail').first().click();
      cy.url().should('include', '/sessions/detail/1');

      cy.intercept('GET', '/api/session/1', sessionAdd).as('Session');

      cy.contains('Participate').click();
      cy.contains('Do not participate');
    });

    it('should cancel participation to a session', () => {
      cy.intercept('GET', '/api/session/1', sessionAdd).as('Session');
      cy.intercept('GET', '/api/teacher/1', teacher1).as('Teacher');
      cy.intercept('DELETE', '/api/session/1/participate/1', {}).as(
        'Do not participate'
      );

      cy.contains('Detail').first().click();
      cy.url().should('include', '/sessions/detail/1');

      cy.intercept('GET', '/api/session/1', session1).as('Session');

      cy.contains('Do not participate').click();
      cy.contains('Participate');
    });
  });
});
