# TouhouClub
![Downloads](https://img.shields.io/github/downloads/Niron3206/TouhouClub/v1.6/total?style=flat-square)

This is local Java project for discord bot, based on JDA library.

**Requires Java 25 and a [Lavalink](https://lavalink.dev) node.** Playback and the voice
connection are handled by the node, not by the bot: Discord requires the DAVE protocol
for voice, and Lavalink 4.2+ implements it. `docker compose` starts a node for you.
The bot cannot play anything without one.

You also have to specify token and prefix in `.env` file, which must be created in the root of the project.
The easiest way to do it is to copy the `.env-example` and rename it to `.env`.
Alternatively, both can be passed as environment variables. Useful for containers.

## Building

Don't forget to install maven if you haven't downloaded it yet:

`mvn package`

This produces a single self-contained `target/TouhouClub.jar` with all dependencies inside.

Run it with `java -jar TouhouClub.jar`. Put your `.env` next to the jar, or pass
`TOKEN` and `PREFIX` through the environment. Point `LAVALINK_URL` and
`LAVALINK_PASSWORD` at your node.

## Docker

`docker compose up -d --build`

This starts four services on a shared network: the bot, a Lavalink node, the
cipher server the node needs for YouTube and an [Xray](https://github.com/XTLS/Xray-core)
proxy. Put your `.env` next to `docker-compose.yml`.
The image builds the project itself, so a local JDK is not required.

The proxy is for hosts, where YouTube does not work and YouTube DNS
is spoofed. The node sends its HTTP traffic through a VLESS tunnel, voice goes direct (due to UDP).
All containers resolve names through the proxy's DNS over HTTPS.
Copy `proxy/config.example.json` to `proxy/config.json` and put your VLESS outbound
there **before** `docker compose up`.

The node reads `lavalink/application.yml`, which pins the youtube-source plugin and
picks up `YT_OAUTH_REFRESH_TOKEN` and `YT_CIPHER_URL` from the environment.

## YouTube playback

YouTube is served by the youtube-source plugin **on the Lavalink node**, configured in
`lavalink/application.yml`. It does not serve audio to anonymous requests, you will see
`Sign in to confirm you're not a bot` or `This video requires login`.
Two settings are needed, and they only work together:

- `YT_CIPHER_URL` a [yt-cipher](https://github.com/kikkia/yt-cipher) server.
  `docker compose` starts one for you; it supplies the `signatureTimestamp`
  that the bundled player-script parser cannot extract.
- `YT_OAUTH_REFRESH_TOKEN` gets past the login wall via the TV client, the only
  one supporting OAuth. Set `YT_OAUTH=true` once and the log prints a link, a code
  and then the refresh token. **Use a burner Google account**, not your main one.

See `.env-example` for details.
