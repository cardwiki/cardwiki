/**
 * Tests grouped together as they use a common database
 *  and therefore are interdependend
 */
const deckName = 'Banana Factory';
const categoryName = 'fruits';
const adminName = 'anja';

describe('E2E Core functionalities', () => {
  it('core functionalities', () => {
    cy.server();
    cy.route('**/auth/providers').as('getProviders');

    // Login
    cy.visit('/');
    cy.get('nav').contains('Login').click();
    cy.wait('@getProviders');
    cy.contains('Test Admin').click();
    cy.location('pathname').should('equal', '/');

    // Create Category
    cy.get('nav').contains('Categories').click();
    cy.contains('Add category').click();
    cy.get('form').contains('Name').click();
    cy.focused().type(`${categoryName}{enter}`);
    cy.location('pathname').should('match', /\/categories\/\d+$/);

    // Create Deck
    cy.get('nav').contains('Create new deck').click();
    cy.focused().type(`${deckName}{enter}`);
    cy.location('pathname').should('match', /\/decks\/\d+$/);
    cy.contains('h1', deckName);

    // Save to Favorites and open deck from Favorites
    cy.contains('Save').click();
    cy.contains('Saved');
    cy.get('nav').contains('CardWiki').click();
    cy.location('pathname').should('equal', '/');
    cy.contains(deckName).click();
    cy.location('pathname').should('match', /\/decks\/\d+$/);

    // Add Card
    cy.contains('Add new card').click();
    cy.get('textarea').eq(0).type('How many Bananas?');
    cy.get('textarea').eq(1).type('About 3 to 4');
    cy.get('form').contains('Create').click();
    cy.contains('h1', deckName);

    // Edit Card
    cy.contains('How many Bananas?')
      .closest('tr')
      .find('a[title="Edit card"]')
      .click({ force: true }); // not visible because css hover currently can't be tested. see cypress issue #10
    cy.get('textarea').eq(1).clear().type('Definitely more than 5');
    cy.get('form').contains('Update').click();
    cy.contains('h1', deckName);

    // Add Category to Deck
    cy.contains('Edit').first().click();
    cy.contains('Add Category').click();
    cy.focused().type(categoryName);
    cy.contains('ul', 'fruits');
    cy.focused().type('{enter}');
    cy.contains('Save').click();
    cy.location('pathname').should('match', /\/decks\/\d+$/);
    cy.contains('h1', deckName);
    cy.contains(categoryName);

    // Preview
    cy.contains('Learn').siblings('[aria-haspopup="true"]').click();
    cy.contains('Preview').click();
    cy.location('pathname').should('include', 'preview');
    cy.contains('How many Bananas?');
    cy.document().trigger('keydown', { key: ' ' });
    cy.contains('Definitely more than 5');
    cy.contains('Back to').click();
    cy.location('pathname').should('match', /\/decks\/\d+$/);

    // Learn
    cy.contains('Learn').click();
    cy.location('pathname').should('include', '/learn');
    cy.contains('How many Bananas');
    cy.document().trigger('keydown', { key: ' ' });
    cy.contains('Definitely more than 5');
    cy.document().trigger('keydown', { key: '2' });
    cy.contains('no more cards');

    // Dashboard
    cy.get('nav').contains('CardWiki').click();
    cy.location('pathname').should('equal', '/');
    cy.contains('Learned Decks')
      .parent()
      .find('ul')
      .should('include.text', deckName)
      .and('include.text', '1 learning');

    // Search for Decks
    cy.get('input[placeholder*="Find decks"]').type(`${deckName}{enter}`);
    cy.location('pathname').should('match', /^\/search/);
    cy.contains('ul', deckName).click();
    cy.location('pathname').should('match', /^\/decks\/\d+/);
    cy.contains('h1', deckName);

    // Search for Categories
    cy.get('nav').contains('Categories').click();
    cy.location('pathname').should('match', /^\/categories/);
    cy.get('input[placeholder*="Find categories"]').type(
      `${categoryName}{enter}`
    );
    cy.contains('ul', categoryName).click();
    cy.location('pathname').should('match', /^\/categories\/\d+/);
    cy.contains('h1', categoryName);
    cy.contains(deckName);

    // Search for Users
    cy.get('nav').contains('Users').click();
    cy.location('pathname').should('match', /^\/users/);
    cy.get('input[placeholder*="Find users"]').type(`${adminName}{enter}`);
    cy.contains('main ul', adminName).click();
    cy.location('pathname').should('equal', '/users/anja/profile');
    cy.contains('h1', adminName);
    cy.contains('Bio');
    cy.contains(deckName);
    cy.contains('Created card');
    cy.contains('Edited card');

    // Logout
    cy.get('nav').contains(adminName).click();
    cy.contains('Logout').click();
    cy.contains('Welcome to CardWiki');
    cy.location('pathname').should('equal', '/');
  });
});
