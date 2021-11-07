package com.adamk33n3r.runelite.narration;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class NarrationPluginLauncher
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(NarrationPlugin.class);
		RuneLite.main(args);
	}
}