describe('Checkout flow', () => {
    beforeEach(() => {

        cy.intercept('GET', '/api/users/JohnJ@lph.nl', { fixture: 'user-john.json' }).as('getUser');
        cy.intercept('GET', '/api/orders?userId=1', { fixture: 'orders-user-1.json' }).as('getOrdersByUser');
        cy.intercept('GET', '/api/giftcards/my-cards', { fixture: 'giftcards-my-cards.json' }).as('getGiftCards');


        cy.login('JohnJ@lph.nl', 'whc8fKxHzGVPTMh');

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
        cy.get('input[formcontrolname=firstName]').type('John');
        cy.get('input[formcontrolname=infix]').type('van');
        cy.get('input[formcontrolname=lastName]').type('Doe');
        cy.get('input[formcontrolname=address]').type('Main Street');
        cy.get('input[formcontrolname=houseNumber]').type('123');
        cy.get('input[formcontrolname=postcode]').type('1234AB');
        cy.get('input[formcontrolname=phoneNumber]').type('0612345678');
        cy.get('input[formcontrolname=email]').clear().type('JohnJ@lph.nl');

        cy.intercept('POST', '/api/orders', { fixture: 'order-success.json' }).as('createOrder');

        cy.get('button.submit-button').click();

        cy.wait('@createOrder').its('request.body').should((body) => {
            expect(body.userId).to.exist;
            expect(body.orderItems).to.have.length(1);
            expect(body.orderItems[0].sku).to.equal('TESTSKU');
            expect(body.totalPrice).to.equal(20);
        });

        cy.url().should('include', '/userProfile/JohnJ@lph.nl');

        cy.window().then(win => {
            const cart = JSON.parse(<string>win.localStorage.getItem('cart'));
            expect(cart).to.be.empty;
        });
    });
});
