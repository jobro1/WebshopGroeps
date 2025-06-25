describe('Gift Card Tests', () => {
  beforeEach(() => {
    // Login before each test
    cy.intercept('GET', '/api/users/jan@email.com', { fixture: 'user-jan.json' }).as('getUser');
    cy.intercept('GET', '/api/productCategories/10', { fixture: 'category-10.json' }).as('getUser');

    cy.login('jan@example.com', 'Password12!');
    // Wait for login to complete by checking for a known post-login element
    cy.url().should('not.include', '/login');
  });
  
  it('should display gift cards in category 10', () => {
    cy.visit('/category/10');
      
      // Wait for products to load
    cy.get('.product-info').should('exist');
      
      // Check if we have products displayed
    cy.get('.product-info').should('have.length.at.least', 1);
      
      // Verify the first product is visible and contains expected gift card text
    cy.get('.product-info').first()
        .should('be.visible')
        .should('contain.text', 'Cadeaubon');
  });
});
