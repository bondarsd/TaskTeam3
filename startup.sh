#!/usr/bin/env bash

CONTAINER_ENGINE="${CONTAINER_ENGINE:-docker}"
COMPOSE_ENGINE="${COMPOSE_ENGINE:-docker compose}"
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.yml}"
PROJECT_NAME="${PROJECT_NAME:-team3}"
DB_SERVICE="${DB_SERVICE:-postgres}"

if ! command -v $CONTAINER_ENGINE &> /dev/null
then
    echo "docker could not be found, switch to podman"
    CONTAINER_ENGINE=podman
    COMPOSE_ENGINE=podman-compose
fi

"${COMPOSE_ENGINE}" -f "${COMPOSE_FILE}" --project-name "${PROJECT_NAME}" up -d "${DB_SERVICE}"
