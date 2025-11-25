# Time Tracking Reminder

A [RuneLite](https://runelite.net/) plugin which extends the "Time Tracking" plugin to show an infobox when
bird houses or farming patches are ready.

![Screenshot](./screenshot.png)

Current supported infoboxes, can be individually enabled and disabled:

- **Miscellaneous**
  - Bird houses
  - Farming contract
  - Hespori
  - Giant compost bin
- **Farming patches**
  - Herb patches
  - Tree patches
  - Fruit tree patches
  - Celastrus patch
  - Hardwood patches
  - Calquat patches
  - Redwood patch
  - Crystal tree patch
  - Seaweed patches
  - Hops patches
  - Bush patches
  - Cactus patches
  - Mushroom patch
  - Belladonna patch
  - Allotment patches

## Configuration

This plugin inherits the "Prefer soonest completion" setting of the "Time Tracking" plugin:

- **Checked:** Show an infobox when *any* patches of a specific type are ready
- **Unchecked:** Show an infobox when *all* patches of a specific type are ready

## Contributing

This plugin is partially generated out of RuneLite sources.
Please check out the newest RuneLite tag, and run `update.js`. Don't make any changes to files in the `/runelite` folder directly.
