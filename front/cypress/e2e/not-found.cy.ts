import * as cypress from 'cypress';

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
