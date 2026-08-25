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

`docker compose up -d --build`

This starts the bot together with the cipher server it needs for YouTube,
wiring them on a shared network. Put your `.env` next to `docker-compose.yml`.
The image builds the project itself, so a local JDK is not required.

## YouTube playback

YouTube does not serve audio to anonymous requests, you will see
`Sign in to confirm you're not a bot` or `This video requires login`.
Two settings are needed, and they only work together:

- `YT_CIPHER_URL` a [yt-cipher](https://github.com/kikkia/yt-cipher) server.
  `docker compose` starts one for you; it supplies the `signatureTimestamp`
  that the bundled player-script parser cannot extract.
- `YT_OAUTH_REFRESH_TOKEN` gets past the login wall via the TV client, the only
  one supporting OAuth. Set `YT_OAUTH=true` once and the log prints a link, a code
  and then the refresh token. **Use a burner Google account**, not your main one.

See `.env-example` for details.
