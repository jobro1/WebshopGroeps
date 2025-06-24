// cypress/e2e/edit-page.cy.ts

describe('Edit Page Product Variation Management', () => {
  beforeEach(() => {
    // Intercept user and order API
    cy.intercept('GET', '/api/users/jan@email.com', { fixture: 'user-jan.json' }).as('getUser');
    cy.intercept('GET', '/api/products/1', { fixture: 'product1.json' }).as('getProduct');

    // Log in as admin (define cy.login custom command or intercept token flow)
    cy.login('jan@example.com', 'Password12!');

    // Pre-fill the cart (optional)
    cy.window().then(win => {
      win.localStorage.setItem('cart', JSON.stringify([
        {
          productVariation: { sku: 'TESTSKU', price: 10 },
          quantity: 2
        }
      ]));
    });

    // Click on admin link if user is admin
    cy.contains('a', 'Admin').click(); // Matches translated "admin" link

    // Click on "edit products" button in admin area
    cy.contains('a.btn', 'edit products').click(); // Class + text match

    // Finally navigate to edit/1
    cy.visit('/edit/1');

    // Ensure product data is loaded
    cy.wait('@getProduct');
  });


  it('displays existing variations and allows selecting a variation', () => {
    cy.get('.variation-group').should('exist');
    cy.get('.variation-group').each(($group) => {
      cy.wrap($group).find('button').first().click();
    });

    cy.get('.product-display').should('exist');
    cy.get('.product-image').should('be.visible');
  });

  it('updates selected variation and saves changes', () => {
    cy.get('.variation-group').each(($group) => {
      cy.wrap($group).find('button').first().click();
    });

    cy.get('input[type="number"]').first().clear().type('99.99');
    cy.get('input[type="number"]').eq(1).clear().type('42');

    cy.get('button').contains('Save').click();

    cy.on('window:alert', (str) => {
      expect(str).to.contain('Variation updated successfully');
    });
  });

  it('creates a new variation', () => {
    cy.contains('Create New Variation').click();
    cy.get('input[placeholder="SKU"], input[type="text"]').first().type('SKU123');
    cy.get('input[type="number"]').eq(0).type('49.99');
    cy.get('input[type="number"]').eq(1).type('10');
    cy.get('input[type="text"]').eq(1).type('http://example.com/image.jpg');

    cy.contains('+ Add Variation Field').click();
    cy.get('label').contains('Variation Name:').parent().find('input').first().type('Color');
    cy.get('label').contains('Value').parent().find('input').first().type('Red');

    cy.contains('Create Variation').click();

    cy.reload();
    cy.get('.variation-group').should('exist');
  });

  it('deletes a variation and ensures "S" is no longer available', () => {
    // Intercept the initial product load
    cy.intercept('GET', '/api/products/1', { fixture: 'product1.json' }).as('getProduct');

    // Intercept the DELETE call
    cy.intercept('DELETE', '/api/products/admin/variation/1', {
      statusCode: 200,
    }).as('deleteVariation');

    // Simulate deletion and replace the product fixture with a modified version
    cy.fixture('product1.json').then((product) => {
      // Remove the variation value with value === 'S'
      product.variations = product.variations.filter((v: { values: any[]; }) =>
        v.values?.every((vv: any) => vv.value !== 'S')
      );

      // After delete, respond with modified product data
      cy.intercept('GET', '/api/products/1', product).as('getProductAfterDelete');
    });

    // Select variations to enable the delete button
    cy.get('.variation-group').each(($group) => {
      cy.wrap($group).find('button').first().click();
    });

    cy.contains('button', 'Delete Variation').click();
    cy.on('window:confirm', () => true); // confirm popup

    cy.wait('@deleteVariation');

    // Reload to trigger GET /api/products/1 again
    cy.reload();
    cy.wait('@getProductAfterDelete');

    // ✅ Assert "S" is gone
    cy.get('.variation-buttons').should('not.contain', 'S');
  });
});
