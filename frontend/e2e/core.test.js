const puppeteer = require('puppeteer');

jest.setTimeout(60000);

test('login', async () => {
	  const browser = await puppeteer.launch();
	  const page = await browser.newPage();
	  await page.goto('http://localhost:4200');
	  await page.$x("//nav//*[contains(., 'Login')]");
	  await browser.close();
});
