describe('Product toevoegen aan winkelmand', () => {
    beforeEach(() => {
        // Stub de product API response
        cy.intercept('GET', '/api/products/1', { fixture: 'product1.json' }).as('getProduct');

        // Ga naar de productpagina
        cy.visit('/products/1');

        // Wacht tot de API call is afgerond
        cy.wait('@getProduct');

        // Eventuele extra render-tijd (optioneel)
        cy.wait(500); // small delay to ensure DOM updates after async logic
    });

    it('selecteert variaties, klikt op add to cart en controleert winkelmand', () => {
        // Zorg dat de productdata is gerenderd
        cy.get('.variation-buttons').should('exist');
        cy.get('.variation-buttons button').should('have.length.at.least', 1);

        cy.get('.variation-buttons button', { timeout: 10000 }).should('have.length.greaterThan', 0);

        // Klik de eerste optie van elke variatie groep aan
        cy.get('.variation-group').each(($group) => {
            cy.wrap($group).find('button').first().click();
        });

        // Klik op de "Add to cart" knop
        cy.contains('button', 'Add to cart').click();

        // Ga naar de winkelmand pagina
        cy.visit('/cart');

        // Controleer dat het product in de winkelmand staat
        cy.get('.items-container', { timeout: 10000 }).should('contain', 'Brown'); // Pas dit aan indien nodig

        // Controleer dat de total price niet 0 is
        cy.get('.cart-total strong').should(($el) => {
            const text = $el.text();
            expect(text).to.match(/€\d+/);
            expect(parseFloat(text.replace(/[^0-9.,]/g, '').replace(',', '.'))).to.be.greaterThan(0);
        });
    });
});
