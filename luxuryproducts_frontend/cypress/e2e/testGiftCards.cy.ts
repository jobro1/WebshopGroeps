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

  describe('Gift Card Purchase Flow', () => {
    it('shows the gift card recipient section when a gift card is in the cart', () => {
      // 1. Go to gift card category and add a gift card to cart
      cy.visit('/category/10');
      cy.get('.product-info').should('exist').first().click();

      cy.get('button').contains(/Add to cart|In winkelwagen/).should('be.visible').click();

      // 2. Go to cart and verify the gift card is present
      cy.visit('/cart');
      cy.get('.item-card').should('exist');
      cy.get('.item-info-text h3').should('contain', 'Cadeaubon');

      // 3. Proceed to checkout
      cy.get('button').contains(/Checkout|Afrekenen/).should('be.visible').click();

      // 4. Wait for the checkout form to load (wait for a form field you know will be there)
      cy.get('form').should('exist');
      cy.get('input[formcontrolname="firstName"]').should('be.visible');

      // 5. Now check for the gift card recipient section
      cy.get('body').then($body => {
        if ($body.find('.gift-card-recipients').length) {
          cy.get('.gift-card-recipients').should('be.visible');
          cy.get('.gift-card-recipient input[type="email"]').should('be.visible').type('recipient@example.com', { force: true });
        } else {
          // Fail with a clear message
          throw new Error('Gift card recipient section did not appear. Ensure a gift card product is in the cart and the UI logic is correct.');
        }
      });

      // 6. Submit the form
      cy.get('button[type="submit"]').should('be.visible').click();
    });

    it('should be able to pay with gift cards', () => {
      // First purchase a gift card
      cy.visit('/category/10');
      cy.get('.product-info').first().click();
      cy.get('button').contains(/Add to cart|In winkelwagen/).click();
      cy.visit('/cart');
      cy.get('button').contains(/Checkout|Afrekenen/).click();

      // Wait for checkout form and fill recipient email
      cy.get('.gift-card-recipient input[type="email"]')
        .first()
        .should('be.visible')
        .type('recipient@example.com', { force: true });

      cy.get('button[type="submit"]')
        .should('be.visible')
        .click();

      // Then try to purchase a regular product using the gift card
      cy.visit('/products');
      cy.get('.product-info').first().click();
      cy.get('button').contains(/Add to cart|In winkelwagen/).click();
      cy.visit('/cart');
      cy.get('button').contains(/Checkout|Afrekenen/).click();

      // Verify gift card payment section exists
      cy.get('.gift-card-section').should('be.visible');
      cy.get('.gift-cards-list').should('be.visible');
      cy.get('.price-breakdown').should('be.visible');
    });
  });

  // describe('Checkout with Gift Card Purchase', () => {
  //   beforeEach(() => {
  //     // Add a gift card to cart
  //     cy.visit('/category/10');
  //     cy.get('.product-info').first().click();
  //     cy.get('button').contains(/Add to cart|In winkelwagen/).click();
  //     cy.visit('/cart');
  //     cy.get('button').contains('Checkout').click();
  //   });
  //
  //   it('should show gift card recipient fields in checkout', () => {
  //     cy.get('.gift-card-recipients').should('exist');
  //     cy.get('.recipient-input').should('exist');
  //     cy.get('input[type="email"]').should('exist');
  //   });
  //
  //   it('should require recipient email for gift cards', () => {
  //     // Fill in required checkout fields
  //     cy.get('#firstName').type('Test');
  //     cy.get('#lastName').type('User');
  //     cy.get('#address').type('Test Street 123');
  //     cy.get('#houseNumber').type('1');
  //     cy.get('#postcode').type('1234AB');
  //     cy.get('#phoneNumber').type('123456789');
  //     cy.get('#email').type('test@example.com');
  //
  //     // Try to submit without recipient email
  //     cy.get('button[type="submit"]').should('be.disabled');
  //
  //     // Add recipient email
  //     cy.get('.recipient-input input[type="email"]').type('recipient@example.com');
  //     cy.get('button[type="submit"]').should('not.be.disabled');
  //   });
  //
  //   it('should complete gift card purchase', () => {
  //     // Intercept the gift card creation request
  //     cy.intercept('POST', '**/api/giftcards', {
  //       statusCode: 200,
  //       body: {
  //         id: 1,
  //         code: 'LPHNEW123',
  //         initialValue: 50.00,
  //         currentBalance: 50.00,
  //         status: 'ACTIVE'
  //       }
  //     }).as('createGiftCard');
  //
  //     // Fill in all required fields
  //     cy.get('#firstName').type('Test');
  //     cy.get('#lastName').type('User');
  //     cy.get('#address').type('Test Street 123');
  //     cy.get('#houseNumber').type('1');
  //     cy.get('#postcode').type('1234AB');
  //     cy.get('#phoneNumber').type('123456789');
  //     cy.get('#email').type('test@example.com');
  //     cy.get('.recipient-input input[type="email"]').type('recipient@example.com');
  //
  //     // Submit the form
  //     cy.get('button[type="submit"]').click();
  //
  //     // Wait for gift card creation
  //     cy.wait('@createGiftCard');
  //
  //     // Should be redirected to profile page
  //     cy.url().should('include', '/userProfile');
  //   });
  // });
  //
  // describe('Pay with Gift Cards', () => {
  //   beforeEach(() => {
  //     // Intercept gift cards request when loading checkout
  //     cy.intercept('GET', '**/api/giftcards/my-cards', {
  //       fixture: 'giftcards.json'
  //     }).as('getGiftCards');
  //
  //     // Add a regular product to cart
  //     cy.visit('/products');
  //     cy.get('.product-info').first().click();
  //     cy.get('button').contains(/Add to cart|In winkelwagen/).click();
  //     cy.visit('/cart');
  //     cy.get('button').contains('Checkout').click();
  //   });
  //
  //   it('should display available gift cards for payment', () => {
  //     cy.wait('@getGiftCards');
  //     cy.get('.gift-card-section').should('exist');
  //     cy.get('.gift-cards-list').should('exist');
  //     cy.get('.gift-card-item').should('have.length', 2);
  //   });
  //
  //   it('should show correct price breakdown with gift cards', () => {
  //     cy.wait('@getGiftCards');
  //     cy.get('.price-breakdown').should('exist');
  //     cy.get('.price-breakdown').should('contain', 'Original Total');
  //     cy.get('.price-breakdown').should('contain', 'Gift Card Discount');
  //     cy.get('.price-breakdown').should('contain', 'Final Total');
  //   });
  //
  //   it('should complete checkout using gift cards', () => {
  //     // Intercept the balance update request
  //     cy.intercept('PATCH', '**/api/giftcards/*/balance', (req) => {
  //       req.reply({
  //         statusCode: 200,
  //         body: {
  //           ...req.body,
  //           status: req.body.balance === 0 ? 'USED' : 'ACTIVE'
  //         }
  //       });
  //     }).as('updateGiftCardBalance');
  //
  //     cy.wait('@getGiftCards');
  //
  //     // Fill in required checkout fields
  //     cy.get('#firstName').type('Test');
  //     cy.get('#lastName').type('User');
  //     cy.get('#address').type('Test Street 123');
  //     cy.get('#houseNumber').type('1');
  //     cy.get('#postcode').type('1234AB');
  //     cy.get('#phoneNumber').type('123456789');
  //     cy.get('#email').type('test@example.com');
  //
  //     // Submit the form
  //     cy.get('button[type="submit"]').click();
  //
  //     // Verify gift card balance update was called
  //     cy.wait('@updateGiftCardBalance');
  //
  //     // Should be redirected to profile page
  //     cy.url().should('include', '/userProfile');
  //   });
  // });
}); 