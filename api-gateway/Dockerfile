# Stage 1: Build the application using Maven
# We use a specific Maven image that includes a JDK.
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the parent pom.xml first to leverage Docker's layer caching.
# If the parent pom doesn't change, Docker won't re-download all dependencies.
COPY ../pom.xml .

# Copy the specific module's pom.xml
COPY api-gateway/pom.xml ./api-gateway/

# We need to run a Maven command that downloads dependencies without building.
# This creates a cache layer.
RUN mvn -f api-gateway/pom.xml dependency:go-offline

# Now, copy the rest of the source code for the module
COPY api-gateway/src ./api-gateway/src

# Build the application JAR file
RUN mvn -f api-gateway/pom.xml clean package -DskipTests


# Stage 2: Create the final, lightweight runtime image
# We use a slim JRE image, which is much smaller than a full JDK.
FROM eclipse-temurin:17-jre-jammy

# Set the working directory
WORKDIR /app

# Copy the built JAR from the 'build' stage into this new stage.
# The path to the JAR is inside the 'target' directory of the module.
COPY --from=build /app/api-gateway/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 9999

# The command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
