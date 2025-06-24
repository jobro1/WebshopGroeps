describe('Checkout flow', () => {
    beforeEach(() => {
        // Zorg dat je in een schone staat begint, bv lege cart en ingelogd bent

        cy.intercept('GET', '/api/users/jan@email.com', { fixture: 'user-jan.json' }).as('getUser');
        cy.intercept('GET', '/api/orders?userId=1', { fixture: 'orders-user-1.json' }).as('getOrdersByUser');
        cy.intercept('POST', 'http://localhost:8080/api/orders', { fixture: 'order-success.json' }).as('createOrder');

        // Hier mock je het ingelogde user
        cy.login('jan@example.com', 'Password12!'); // schrijf zelf de login command of mock

        // Mock cart vullen (kan ook via API of localStorage)
        const mockedCart = [
            {
                productVariation: {
                productVariationId: 1,
                sku: "GLV-BRO-S-1-BROWNS",
                price: 297.66,
                stock: 38,
                imageUrl: "/assets/images/products/luxury_driving_glove_2f5861c2-cd14-4713-a32e-26453347ee44.png",
                values: [
                    {
                    variationValueId: 1,
                    value: "Brown",
                    variation: {
                        variationId: 1,
                        variationName: "Color"
                    }
                    },
                    {
                    variationValueId: 2,
                    value: "S",
                    variation: {
                        variationId: 2,
                        variationName: "Size"
                    }
                    }
                ]
                },
                quantity: 1
            }
        ];

        cy.window().then((win) => {
            win.localStorage.setItem('cart', JSON.stringify(mockedCart));
        });
        cy.visit('/cart'); // Zorg dat je op de checkout pagina begint
    });

    it('should place order successfully', () => {
        cy.intercept('POST', '**/orders*', (req) => {
            console.log('Intercepted POST request to:', req.url);
        });

        cy.get('button.checkout-button').click();
        cy.url().should('include', '/checkout');

        // Vul het formulier in
        cy.get('input[formcontrolname=firstName]').type('John');
        cy.get('input[formcontrolname=infix]').type('van');
        cy.get('input[formcontrolname=lastName]').type('Doe');
        cy.get('input[formcontrolname=address]').type('Main Street');
        cy.get('input[formcontrolname=houseNumber]').type('123');
        cy.get('input[formcontrolname=postcode]').type('1234AB');
        cy.get('input[formcontrolname=phoneNumber]').type('0612345678');
        cy.get('input[formcontrolname=email]').type('jan@email.com');

        cy.get('button.submit-button').should('not.be.disabled');

        cy.get('form').then(($form) => {
            const isValid = $form[0].checkValidity();
            console.log('Form valid:', isValid);
            expect(isValid).to.be.true;
        });


        // Klik op 'pay'
        cy.get('button.submit-button').should('not.be.disabled').click();

        cy.get('@createOrder.all').then(console.log);


        cy.wait('@createOrder').then((interception) => {
            console.log('Intercepted createOrder:', interception);
        });

        // Controleer of je wordt doorgestuurd naar de profielpagina
        cy.url().should('include', '/userProfile/jan@email.com');
    });
});
