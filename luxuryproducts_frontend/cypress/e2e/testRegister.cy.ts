describe('Registration Flow Tests', () => {
    beforeEach(() => {
        cy.visit('/register');
    });

    it('successfully registers with valid data', () => {
        cy.intercept('POST', '/api/auth/register', { fixture: 'register-success.json' }).as('registerRequest');

        cy.get('#firstName').type('John');
        cy.get('#lastName').type('Doe');
        cy.get('#dateOfBirth').type('1990-01-01');
        cy.get('#address').type('Main Street');
        cy.get('#houseNumber').type('123');
        cy.get('#postcode').type('1234AB');
        cy.get('#phoneNumber').type('0612345678');
        cy.get('#email').type('johndoe@test.com');
        cy.get('#password').type('StrongPass123!');

        cy.get('form').submit();

        cy.wait('@registerRequest').its('response.statusCode').should('eq', 200);
        cy.url().should('include', '/');
    });

    it('shows validation errors for empty required fields', () => {
        cy.get('form').submit();

        cy.get('#firstName').type('J');
        cy.get('#lastName').type('DD');
        cy.get('#firstName').parent().find('.wrong-message').should('be.visible');
    });

    it('validates email format', () => {
        cy.get('#email').type('invalid-email');
        cy.get('#email').blur();
        cy.get('#email').parent().find('.wrong-message').should('be.visible');

        cy.get('#email').clear().type('valid@email.com');
        cy.get('#email').blur();
        cy.get('#email').parent().find('.correct-message').should('be.visible');
    });

    it('validates password strength', () => {
        cy.get('#password').type('weak');
        cy.get('#password').blur();
        cy.get('#password').parent().find('.wrong-message').should('be.visible');

        cy.get('#password').clear().type('StrongPass!');
        cy.get('#password').blur();
        cy.get('#password').parent().find('.wrong-message').should('be.visible');

        cy.get('#password').clear().type('StrongPass123');
        cy.get('#password').blur();
        cy.get('#password').parent().find('.wrong-message').should('be.visible');

        cy.get('#password').clear().type('StrongPass123!');
        cy.get('#password').blur();
        cy.get('#password').parent().find('.correct-message').should('be.visible');
    });

    it('handles registration error', () => {
        cy.on('window:alert', (text) => {
            expect(text).to.contain('Error:');
        });

        cy.intercept('POST', '/api/auth/register', {
            statusCode: 404,
            body: {
                message: 'Registration failed'
            }
        }).as('registerFailRequest');

        cy.get('#firstName').type('John');
        cy.get('#lastName').type('Doe');
        cy.get('#dateOfBirth').type('1990-01-01');
        cy.get('#address').type('Main Street');
        cy.get('#houseNumber').type('123');
        cy.get('#postcode').type('1234AB');
        cy.get('#phoneNumber').type('0612345678');
        cy.get('#email').type('johndoe@test.com');
        cy.get('#password').type('StrongPass123!');

        cy.get('form').submit();

        cy.wait('@registerFailRequest').its('response.statusCode').should('eq', 404);
    });
});