describe('Gift Card Tests', () => {
  beforeEach(() => {
    // Login before each test
    cy.visit('/login');
    cy.get('#email').type('user@lux.com');
    cy.get('#password').type('User123!');
    cy.get('button').contains(/Login|Inloggen/).click();
    // Wait for login to complete by checking for a known post-login element
    cy.url().should('not.include', '/login');
  });

  describe('Gift Card Category Tests', () => {
    beforeEach(() => {
      // Login before each test
      cy.visit('/login');
      cy.get('#email').type('user@lux.com');
      cy.get('#password').type('User123!');
      cy.get('button').contains(/Login|Inloggen/).click();
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
}); 