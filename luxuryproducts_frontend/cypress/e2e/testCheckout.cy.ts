describe('Checkout flow', () => {
    beforeEach(() => {
        // Zorg dat je in een schone staat begint, bv lege cart en ingelogd bent

        cy.intercept('GET', '/api/users/jan@email.com', { fixture: 'user-jan.json' }).as('getUser');
        cy.intercept('GET', '/api/orders?userId=1', { fixture: 'orders-user-1.json' }).as('getOrdersByUser');
        cy.intercept('GET', '/api/giftcards/my-cards', { fixture: 'giftcards-my-cards.json' }).as('getGiftCards');


        // Hier mock je het ingelogde user
        cy.login('jan@example.com', 'Password12!'); // schrijf zelf de login command of mock

        // Mock cart vullen (kan ook via API of localStorage)
        cy.window().then(win => {
            win.localStorage.setItem('cart', JSON.stringify([
                {
                    productVariation: { sku: 'TESTSKU', price: 10 },
                    quantity: 2
                }
            ]));
        });

        cy.visit('/checkout');
    });

    it('should place order successfully', () => {
        // Vul het formulier in
        cy.get('input[formcontrolname=firstName]').type('John');
        cy.get('input[formcontrolname=infix]').type('van');
        cy.get('input[formcontrolname=lastName]').type('Doe');
        cy.get('input[formcontrolname=address]').type('Main Street');
        cy.get('input[formcontrolname=houseNumber]').type('123');
        cy.get('input[formcontrolname=postcode]').type('1234AB');
        cy.get('input[formcontrolname=phoneNumber]').type('0612345678');
        cy.get('input[formcontrolname=email]').clear().type('jan@email.com');

        // Intercept de POST call naar orders API en forceer een succesvolle response
        cy.intercept('POST', '/api/orders', { fixture: 'order-success.json' }).as('createOrder');

        // Klik op 'pay'
        cy.get('button.submit-button').click();

        // Wacht op de POST call en controleer payload
        cy.wait('@createOrder').its('request.body').should((body) => {
            expect(body.userId).to.exist;
            expect(body.orderItems).to.have.length(1);
            expect(body.orderItems[0].sku).to.equal('TESTSKU');
            expect(body.totalPrice).to.equal(20); // 10 * 2 quantity
        });

        // Controleer of je wordt doorgestuurd naar de profielpagina
        cy.url().should('include', '/userProfile/jan@email.com');

        // Optioneel: check dat de cart nu leeg is in localStorage
        cy.window().then(win => {
            const cart = JSON.parse(<string>win.localStorage.getItem('cart'));
            expect(cart).to.be.empty;
        });
    });
});
