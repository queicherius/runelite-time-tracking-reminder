package com.timetrackingreminder;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TimeTrackingReminderPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(TimeTrackingReminderPlugin.class);
		RuneLite.main(args);
	}
}