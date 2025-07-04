describe('Login tests', () => {
    beforeEach(() => {
        cy.visit('/login');
    });

    it('logs in successfully with correct credentials', () => {
        cy.intercept('POST', '/api/auth/login', { fixture: 'login-success.json' }).as('loginRequest');

        cy.get('#email').type('jan@email.com');
        cy.get('#password').type('Password12!');
        cy.get('#button-login').click();

        cy.wait('@loginRequest').its('response.statusCode').should('eq', 200);

        cy.url().should('include', '/userProfile/jan@email.com');
    });

    it('shows error message on invalid credentials', () => {
        cy.intercept('POST', '/api/auth/login', {
            statusCode: 403,
            fixture: 'login-fail.json'
        }).as('loginRequestFail');

        cy.get('#email').type('jan@email.com');
        cy.get('#password').type('WrongPassword!');
        cy.get('#button-login').click();

        cy.wait('@loginRequestFail');

        cy.get('.invalid-password-text').should('be.visible')
            .and('contain.text', 'incorrect');
    });
});
