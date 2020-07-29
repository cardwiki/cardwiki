describe('Login', () => {
    beforeEach('setup route aliases', () => {
        cy.server()
        cy.route('**/auth/providers').as('getProviders')
    })

    it('Login and Logout as Test Admin', () => {
        cy.visit('/')
        cy.contains('Welcome to CardWiki')

        // Login as Test Admin
        cy.get('nav').contains('Login').click()
        cy.location('pathname').should('equal', '/login')
        cy.wait('@getProviders')
        cy.contains('Test Admin').click()
        cy.location('pathname').should('equal', '/')

        // Test if the login is persisted
        cy.reload()

        // Logout
        cy.get('nav').contains('anja').click()
        cy.contains('Logout').click()
        cy.contains('Welcome to CardWiki')
        cy.location('pathname').should('equal', '/')
    })

    it('Login page contains all auth providers', () => {
        cy.visit('/login')
        cy.wait('@getProviders')
        cy.contains('GitHub')
        cy.contains('GitLab')
        cy.contains('Google')
        cy.contains('Test Admin')
        cy.contains('Test User')
    })
})
