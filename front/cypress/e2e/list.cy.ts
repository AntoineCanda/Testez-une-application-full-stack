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
