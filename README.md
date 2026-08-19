# TouhouClub
![Downloads](https://img.shields.io/github/downloads/Niron3206/TouhouClub/v1.6/total?style=flat-square)

This is local Java project for discord bot, based on JDA library.

**Requires Java 25.** Voice support relies on the DAVE protocol
([JDAVE](https://github.com/MinnDevelopment/jdave)), which uses the FFM API and does not
work on older JDKs. Discord blocks voice connections without DAVE.

You also have to specify token and prefix in `.env` file, which must be created in the root of the project.
The easiest way to do it is to copy the `.env-example` and rename it to `.env`.
Alternatively, both can be passed as environment variables. Useful for containers.

## Building

Don't forget to install maven if you haven't downloaded it yet:

`mvn package`

This produces a single self-contained `target/TouhouClub.jar` with all dependencies inside.

Run it with `java -jar TouhouClub.jar`. Put your `.env` next to the jar, or pass
`TOKEN` and `PREFIX` through the environment.

## Docker

`docker build -t touhouclub .`\
`docker run --rm --env-file .env touhouclub`

The image builds the project itself, so a local JDK is not required.
