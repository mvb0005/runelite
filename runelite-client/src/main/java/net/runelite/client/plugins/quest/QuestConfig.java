package net.runelite.client.plugins.quest;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("quest")
public interface QuestConfig extends Config {

    @ConfigItem(
            keyName = "drawNPC",
            name = "Draw NPC Overlay",
            description = "Enable/Disable Drawing of NPC Overlays",
            position = 1
    )
    default boolean drawNPCOverlay()
    {
        return true;
    }

    @ConfigItem(
            keyName = "drawWidget",
            name = "Draw Chat Overlay",
            description = "Enable/Disable Drawing of Chat Overlays",
            position = 2
    )
    default boolean drawWidgetOverlay()
    {
        return true;
    }

    @ConfigItem(
            keyName = "getOverlayColor",
            name = "Overlay Color",
            description = "Color of the overlays",
            position = 3
    )
    default Color getOverlayColor()
    {
        return Color.green;
    }
}
