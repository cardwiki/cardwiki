# CardWiki Frontend

## First Steps

Navigate to the root folder of the project and execute `npm install`. Based on the _package.json_ file, npm will download all required node_modules to run a Angular application.
Afterwards, execute `npm install -g @angular/cli` to install the Angular CLI globally.

## Development

### Development server

Run `ng serve` to start the web application. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

### Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

### Deployment

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

Configure the following headers in the web server:

- `content-security-policy: script-src 'self'; object-src 'none';`

### Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).
