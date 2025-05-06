# Build stage
FROM gradle:8.13.0-jdk21-noble AS builder

WORKDIR /app/sort-benchmark-cli

COPY . .

RUN gradle assemble --no-daemon

# Deployment stage
FROM ubuntu:24.04

# Install JRE
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    openjdk-21-jre-headless && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Create a non-root user
RUN groupadd --gid 1001 cli-user && \
    useradd --uid 1001 --gid 1001 --create-home cli-user

# Copy the application from the build stage
COPY --from=builder /app/sort-benchmark-cli/build/libs/sort-benchmark-cli.jar /app/sort-benchmark-cli.jar

# Copy the dependencies from the build stage
COPY --from=builder /app/sort-benchmark-cli/build/libs/lib/picocli-4.7.7.jar /app/lib/picocli-4.7.7.jar

# Set permissions in the application directory for the non-root user
RUN chown -R cli-user:cli-user /app

# Switch to the non-root user
USER cli-user

WORKDIR /app

ENTRYPOINT ["java", "-cp", "sort-benchmark-cli.jar:lib/picocli-4.7.7.jar", "com.github.rlacher.sortbench.MainCommand"]

CMD ["-h"]

