
describe('Edit Page Product Variation Management', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/users/JohnJ@lph.nl', { fixture: 'user-john.json' }).as('getUser');
    cy.intercept('GET', '/api/products/1', { fixture: 'product1.json' }).as('getProduct');

    cy.login('JohnJ@lph.nl', 'whc8fKxHzGVPTMh');

    cy.window().then(win => {
      win.localStorage.setItem('cart', JSON.stringify([
        {
          productVariation: { sku: 'TESTSKU', price: 10 },
          quantity: 2
        }
      ]));
    });

    cy.contains('a', 'Admin').click();

    cy.contains('a.btn', 'edit products').click();

    cy.visit('/edit/1');

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
    cy.contains('Create New Variant').click();
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
    cy.intercept('GET', '/api/products/1', { fixture: 'product1.json' }).as('getProduct');

    cy.intercept('DELETE', '/api/products/admin/variation/1', {
      statusCode: 200,
    }).as('deleteVariation');

    cy.fixture('product1.json').then((product) => {
      product.variations = product.variations.filter((v: { values: any[]; }) =>
        v.values?.every((vv: any) => vv.value !== 'S')
      );

      cy.intercept('GET', '/api/products/1', product).as('getProductAfterDelete');
    });

    cy.get('.variation-group').each(($group) => {
      cy.wrap($group).find('button').first().click();
    });

    cy.contains('button', 'Delete Variation').click();
    cy.on('window:confirm', () => true);

    cy.wait('@deleteVariation');

    cy.reload();
    cy.wait('@getProductAfterDelete');

    cy.get('.variation-buttons').should('not.contain', 'S');
  });
});
