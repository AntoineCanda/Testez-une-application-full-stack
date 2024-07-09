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

describe('list', () => {
  it('List of sessions for admin', () => {
    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'yoga@studio.com',
        firstName: 'firstname',
        lastName: 'lastname',
        admin: true,
      },
    });

    cy.intercept('GET', '/api/session', {
      body: [
        {
          id: 1,
          name: 'test',
          date: '2024-07-07T19:00:00',
          teacher_id: 1,
          description: 'test description',
          users: [],
          createdAt: '2024-07-02T17:00:00',
          updatedAt: '2024-07-03T18:00:00',
        },
      ],
    });

    cy.get('input[formControlName="email"]').type('yoga@studio.com');
    cy.get('input[formControlName="password"]').type('test!1234');
    cy.get('button[type="submit"]').click();
    cy.url().should('include', 'sessions');

    //Sessions informations for admins
    cy.contains('Rentals available').should('be.visible');
    cy.contains('Create').should('be.visible');
    cy.contains('test').should('be.visible');
    cy.contains('Session on July 7, 2024').should('be.visible');

    //image
    const image = cy.get('img.picture');
    image.should('have.attr', 'src', 'assets/sessions.png');
    image.should('have.attr', 'alt', 'Yoga session');
    image.should('be.visible');

    cy.contains('test description').should('be.visible');
    cy.contains('Detail').should('be.visible');
    cy.contains('Edit').should('be.visible');
  });
});

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

describe('Not-found Component', () => {
  beforeEach(() => {
    cy.visit('/not-exist');
  });

  afterEach(() => {});

  it('should redirect to the 404 page when the page not exist', () => {
    cy.url().should('eq', 'http://localhost:4200/404');
  });

  it('should display not found message', () => {
    cy.get('h1').should('contain', 'Page not found !');
  });
});

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
