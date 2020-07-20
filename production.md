# Production Installation

## Install backend dependencies

The environment is assumed to be Debian buster. For other environments some details may vary.

```
sudo useradd -r -m cardwiki
sudo loginctl enable-linger cardwiki
sudo apt install openjdk-11-jre-headless

# Upload cardwiki.service
mkdir -p ~/.config/systemd/user/
cp cardwiki.service ~/.config/systemd/user/
XDG_RUNTIME_DIR=/run/user/$UID systemctl --user daemon-reload
XDG_RUNTIME_DIR=/run/user/$UID systemctl --user enable cardwiki
```

## Configuration

To configure the backend upload `application-prod.yml` to `/home/cardwiki/` and configure your secrets.

To configure the frontend copy `src/environments/config.ts` to `src/environments/config.prod.ts` and adjust the `backendBaseUri`.

Create your web server config, e.g. for NGINX:

```
server {
	root /home/cardwiki/frontend;

	location / {
		try_files $uri $uri/ /index.html;
	}

	location ~ ^/(api|login/oauth2/code|swagger-ui.html|webjars) {
		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
		proxy_set_header X-Forwarded-Proto $scheme;
		proxy_set_header X-Forwarded-Port $server_port;
		proxy_set_header Host $host;
		proxy_pass http://localhost:8080;
	}

	...
}
```

## Deploying

1. Build the backend with `mvn package`.
2. Upload `target/backend-*.jar` to `/home/cardwiki/backend.jar`.
3. Restart the backend with `XDG_RUNTIME_DIR=/run/user/$UID systemctl --user restart cardwiki`
4. Build the frontend with `npx ng build --prod`.
5. Upload the files in `dist/cardwiki/` to `/home/cardwiki/frontend/`.
