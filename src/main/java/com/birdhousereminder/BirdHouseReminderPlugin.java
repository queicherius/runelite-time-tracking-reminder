package com.birdhousereminder;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
		name = "Bird House Reminder"
)
public class BirdHouseReminderPlugin extends Plugin {
    @Inject
    private ItemManager itemManager;

    @Inject
    private InfoBoxManager infoBoxManager;

    private BirdHouseReminderInfoBox infoBox;

    @Override
    protected void startUp() throws Exception {
        log.info("Bird House Reminder started!");
        this.showInfoBox();
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Bird House Reminder stopped!");
        this.hideInfoBox();
    }

    private void showInfoBox() {
        final BufferedImage image = itemManager.getImage(21521);
        infoBox = new BirdHouseReminderInfoBox(this, image);
        infoBoxManager.addInfoBox(infoBox);
    }

    private void hideInfoBox() {
        infoBoxManager.removeInfoBox(infoBox);
    }
}
