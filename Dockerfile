# Sử dụng hình ảnh Maven để build ứng dụng
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Sử dụng hình ảnh JDK để chạy ứng dụng
FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /app/target/WareHouseManagementApplication-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 6060