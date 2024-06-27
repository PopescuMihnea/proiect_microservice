# Use the official Alpine image from the Docker Hub
FROM alpine:latest

# Install networking tools
RUN apk update && apk add --no-cache iputils curl netcat-openbsd

# Set the default command to an interactive shell
CMD ["sh"]