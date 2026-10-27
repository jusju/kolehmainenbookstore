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
# Upload to the user's home first; sudo then copies it to JAR_REMOTE.
JAR_UPLOAD="/home/${REMOTE_USER}/${APP_NAME}.jar.upload"

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
if ! scp "${SSH_OPTS[@]}" "$JAR_LOCAL" "${REMOTE}:${JAR_UPLOAD}"; then
  echo "ERROR: could not copy jar to ${REMOTE}:${JAR_UPLOAD}" >&2
  exit 1
fi

echo "==> 6) sudo copy jar into place"
if ! ssh "${SSH_OPTS[@]}" "$REMOTE" "sudo cp -f '${JAR_UPLOAD}' '${JAR_REMOTE}' && rm -f '${JAR_UPLOAD}'"; then
  echo "ERROR: could not copy jar to ${JAR_REMOTE} on ${REMOTE_HOST}" >&2
  exit 1
fi

echo "==> 7) restart ${SERVICE_NAME} (runs java -jar ${JAR_REMOTE})"
if ! ssh "${SSH_OPTS[@]}" "$REMOTE" "sudo systemctl restart '${SERVICE_NAME}'"; then
  echo "ERROR: could not restart ${SERVICE_NAME} on ${REMOTE_HOST}" >&2
  exit 1
fi
sleep 5
if ! ssh "${SSH_OPTS[@]}" "$REMOTE" "systemctl is-active --quiet '${SERVICE_NAME}'"; then
  echo "ERROR: ${SERVICE_NAME} is not running after restart on ${REMOTE_HOST}" >&2
  ssh "${SSH_OPTS[@]}" "$REMOTE" "systemctl --no-pager status '${SERVICE_NAME}' | head -n 15" || true
  exit 1
fi

echo "==> Deployed. App should be at https://${REMOTE_HOST}:8090/"
