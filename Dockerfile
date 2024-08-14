# Etapa 1: Construir o frontend React
FROM node:18-alpine AS build-frontend
WORKDIR /app
COPY src/main/frontend .
RUN npm install
RUN npm run build

# Etapa 2: Construir o backend Java
FROM openjdk:21-ea-1-slim AS build-backend
VOLUME /tmp
COPY target/calcEmprestimos-0.0.1-SNAPSHOT.jar app.jar

# Etapa 3: Combinar o frontend e o backend
FROM openjdk:21-ea-1-slim
VOLUME /tmp

# Copiar o frontend buildado para o diretório de recursos estáticos do backend
COPY --from=build-frontend /app/build /public

# Copiar o JAR do backend
COPY --from=build-backend /app.jar /app.jar

# Expor a porta 8080
EXPOSE 8080

ENTRYPOINT ["java", "--enable-preview", "-jar", "/app.jar"]