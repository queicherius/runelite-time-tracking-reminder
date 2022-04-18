package com.birdhousereminder;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BirdHouseReminderPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BirdHouseReminderPlugin.class);
		RuneLite.main(args);
	}
}