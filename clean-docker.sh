#!/bin/bash
docker-compose down
docker system prune -f -a --volumes
# docker system prune -f --volumes