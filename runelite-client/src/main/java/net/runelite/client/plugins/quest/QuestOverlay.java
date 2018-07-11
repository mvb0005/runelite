package net.runelite.client.plugins.quest;


import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

@Slf4j
public class QuestOverlay extends Overlay {

    private final Client client;
    private final QuestPlugin plugin;

    @Inject
    QuestOverlay(Client client, QuestPlugin plugin){
        this.client = client;
        this.plugin = plugin;
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.chatOption >= 0 && plugin.widget != null && !plugin.widget.isHidden()){
            renderWidget(graphics);
        }

        List<NPC> targets = plugin.getHighlightedTargets();
        for (NPC npc: targets){
            renderTargetOverlay(graphics, npc, plugin.getOverlayColor());
        }

        return null;
    }

    private void renderTargetOverlay(Graphics2D graphics, NPC actor, Color color)
    {
        Polygon objectClickbox = actor.getConvexHull();
        if (objectClickbox != null)
        {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(objectClickbox);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
            graphics.fill(objectClickbox);
        }
    }

    private void renderWidget(Graphics2D graphics) {
        Rectangle bounds = plugin.widget.getBounds();
        graphics.setColor(plugin.getOverlayColor());
        graphics.draw(bounds);
        graphics.setFont(new Font(graphics.getFont().getFontName(), Font.PLAIN, 50));
        graphics.setColor(Color.BLACK);
        graphics.drawString(String.valueOf(plugin.chatOption), bounds.x + 10, bounds.y + bounds.height / 2);
    }
}
