# Running the bookstore with Docker on softala

To run the Dockerfile on `softala.haaga-helia.fi`, the server needs Docker installed and the project files, and then a `docker build` and `docker run`. The current systemd service must be stopped first, because the container uses the same port **8090**.

## 1. Check whether Docker is installed

```bash
docker --version
```

If it's missing, someone with `sudo` has to install it. Since softala is a school server, ask the admin first. On Ubuntu:

```bash
sudo apt update
sudo apt install docker.io
sudo systemctl enable --now docker
```

To run `docker` without `sudo`, add the user to the `docker` group, then log out and back in:

```bash
sudo usermod -aG docker iinak
```

> **Note:** Being in the `docker` group is effectively the same as having root access, so treat it like `sudo` rights.

## 2. Get the project onto the server

The Dockerfile builds from source, so it needs the whole project, not just the jar:

```bash
cd ~
git clone https://github.com/jusju/kolehmainenbookstore.git
cd kolehmainenbookstore
```

The repo's remote is an SSH address (`git@github.com:...`). The HTTPS address above works without a GitHub key if the repo is public. If it's private, the server needs a GitHub key or access token.

## 3. Build the image

```bash
docker build -t kolehmainenbookstore .
```

## 4. Stop the current service

The service and the container would both use port 8090, so stop and disable the service:

```bash
sudo systemctl stop iinakbookapp.service
sudo systemctl disable iinakbookapp.service
```

## 5. Run the container

```bash
docker run -d --name bookstore \
  --network host \
  --restart unless-stopped \
  -e SPRING_DATASOURCE_USERNAME=iinak \
  -e SPRING_DATASOURCE_PASSWORD='real-password' \
  kolehmainenbookstore
```

- `--network host` lets the container share the server's network. That way `localhost:3306` in `application.properties` still reaches the MySQL running on softala, and the app is directly on port 8090.
- `--restart unless-stopped` starts the container again after a crash or a server reboot, which replaces what systemd did.

## 6. Check that it works

```bash
docker ps
docker logs -f bookstore
```

Then open <https://softala.haaga-helia.fi:8090/>.

## Deploying a new version later

```bash
cd ~/kolehmainenbookstore && git pull
docker build -t kolehmainenbookstore .
docker rm -f bookstore
docker run -d --name bookstore --network host --restart unless-stopped \
  -e SPRING_DATASOURCE_USERNAME=iinak -e SPRING_DATASOURCE_PASSWORD='real-password' \
  kolehmainenbookstore
```

This replaces steps 4–7 of `commit-and-deploy.sh`, which copy the jar and restart the systemd service.
