# BREVENTS CORE

An extensive event core plugin for BlockParty server.

**Made with Kotlin**

## FEATURES
- Full event handling, starting from even creating to giving rewards.
- Admin commands to fully control the plugin.
- Everything is customizable; Messages, titles and every single value (you can view config.yml!)
- Currently features the following events: SurvivalGames, Spleef, OITC and FloorIsLava
- Designed with scaleablility in mind; anyone can easily add new types of events to the plugin, its as simple as creating a java class!
- Highly optimized; uses the best optimization practices, and uses HikariCP for effencient database connection pooling.
- PlaceholdeAPI support.
- API featuring events.

## Requirements

- Spigot/Paper 1.20.6+
- MySql database.
- PlaceholderAPI plugin.


## How to build
Ensure maven is installed then run the following command in the project's directory:

``` mvn clean install```

## API
BREvents has the following events:
```
me.titan.blockpartyevents.api.event.EventPhaseStart
me.titan.blockpartyevents.api.event.EventCancelEvent
me.titan.blockpartyevents.api.event.EventPreEndEvent
me.titan.blockpartyevents.api.event.ventPostEndEvent
```

