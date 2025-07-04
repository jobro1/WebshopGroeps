describe('Gift Card Tests', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/users/JohnJ@lph.nl', { fixture: 'user-john.json' }).as('getUser');
    cy.intercept('GET', '/api/productCategories/10', { fixture: 'category-10.json' }).as('getUser');

    cy.login('JohnJ@lph.nl', 'whc8fKxHzGVPTMh');
    cy.url().should('not.include', '/login');
  });
  
  it('should display gift cards in category 10', () => {
    cy.visit('/category/10');
      
    cy.get('.product-info').should('exist');
      
    cy.get('.product-info').should('have.length.at.least', 1);
      
    cy.get('.product-info').first()
        .should('be.visible')
        .should('contain.text', 'Cadeaubon');
  });
});
