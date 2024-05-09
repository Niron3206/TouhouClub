FROM openjdk:17
WORKDIR /app
COPY . .
CMD ["java","-jar","TouhouClub-1.6.jar"]
