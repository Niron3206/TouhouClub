FROM openjdk:17
WORKDIR /app
COPY . .
CMD ["java","-jar","TouhouClub-1.5.jar"]
