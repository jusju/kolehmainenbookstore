#!/usr/bin/env bash
# Commit, push, build and deploy kolehmainenbookstore to softala.haaga-helia.fi.
# Relies on an SSH key already authorized for iinak@softala.haaga-helia.fi.
#
# Usage: ./commit-and-deploy.sh "commit message"
set -euo pipefail

REMOTE_USER="iinak"
REMOTE_HOST="softala.haaga-helia.fi"
REMOTE="${REMOTE_USER}@${REMOTE_HOST}"
APP_NAME="kolehmainenbookstore"
REMOTE_DIR="/home/${REMOTE_USER}/${APP_NAME}"
SERVICE_NAME="${APP_NAME}.service"
JAR_LOCAL="target/${APP_NAME}-0.0.1-SNAPSHOT.jar"
JAR_REMOTE="${REMOTE_DIR}/${APP_NAME}.jar"

COMMIT_MSG="${1:-deploy $(date '+%Y-%m-%d %H:%M:%S')}"
SSH_OPTS=(-o BatchMode=yes)

cd "$(dirname "$0")"

echo "==> 1) git add"
git add -A

echo "==> 2) git commit"
if git diff --cached --quiet; then
  echo "Nothing to commit, continuing."
else
  git commit -m "$COMMIT_MSG"
fi

echo "==> 3) git push"
git push origin "$(git rev-parse --abbrev-ref HEAD)"

echo "==> 4) build jar"
# Tests skipped: they need the MySQL database that only exists on the server.
./mvnw -q clean package -DskipTests
[ -f "$JAR_LOCAL" ] || { echo "Jar not found: $JAR_LOCAL" >&2; exit 1; }

echo "==> 5) scp jar to ${REMOTE}"
ssh "${SSH_OPTS[@]}" "$REMOTE" "mkdir -p '${REMOTE_DIR}'"
scp "${SSH_OPTS[@]}" "$JAR_LOCAL" "${REMOTE}:${JAR_REMOTE}.new"

echo "==> 6) + 7) replace jar, install java -jar service and restart"
ssh "${SSH_OPTS[@]}" "$REMOTE" bash -s <<EOF
set -euo pipefail

# Keep previous jar as a backup and swap in the new one.
if [ -f '${JAR_REMOTE}' ]; then
  mv -f '${JAR_REMOTE}' '${JAR_REMOTE}.bak'
fi
mv -f '${JAR_REMOTE}.new' '${JAR_REMOTE}'

# systemd unit running the app with java -jar (replaced on every deploy).
cat > '${REMOTE_DIR}/${SERVICE_NAME}' <<UNIT
[Unit]
Description=${APP_NAME} Spring Boot application
After=network.target mysql.service

[Service]
User=${REMOTE_USER}
WorkingDirectory=${REMOTE_DIR}
ExecStart=/usr/bin/env java -jar ${JAR_REMOTE}
SuccessExitStatus=143
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
UNIT

sudo mv -f '${REMOTE_DIR}/${SERVICE_NAME}' '/etc/systemd/system/${SERVICE_NAME}'
sudo systemctl daemon-reload
sudo systemctl enable '${SERVICE_NAME}'
sudo systemctl restart '${SERVICE_NAME}'

sleep 5
sudo systemctl --no-pager status '${SERVICE_NAME}' | head -n 15
EOF

echo "==> Deployed. App should be at https://${REMOTE_HOST}:8090/"
