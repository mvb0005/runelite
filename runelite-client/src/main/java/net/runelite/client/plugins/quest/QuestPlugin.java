package net.runelite.client.plugins.quest;

import com.google.common.eventbus.Subscribe;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;
import java.util.*;
import java.util.List;

@Slf4j
@PluginDescriptor(
        name = "Quest Assist",
        description = "Makes Questing a Breeze",
        tags = {"Quest", "Guide", "Assist"}
)
public class QuestPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private QuestOverlay overlay;

    @Getter(AccessLevel.PACKAGE)
    private List<NPC> highlightedTargets = new ArrayList<>();

    @Getter(AccessLevel.PACKAGE)
    Color overlayColor = new Color(50,250,60);

    Widget widget;
    int chatOption;
    private Boolean updateWidget = false;
    private int npcID = 4626;
    private ArrayList<String> chatOptions = new ArrayList<>();


    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        chatOptions.add("What's wrong?");
        chatOptions.add("I'm always happy to help a cook in distress.");
        QuestUtil.loadQuest();
        super.startUp();
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        super.shutDown();
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded){
        if (widgetLoaded.getGroupId() == 219){
            widget = client.getWidget(219,0);
            updateWidget = true;
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (updateWidget){
            log.debug("{}", widget.getDynamicChildren().length);
            chatOption = 0;
            for (Widget w: widget.getDynamicChildren()){
                log.debug(w.getText());
                if (chatOptions.contains(w.getText())){
                    widget = w;
                    updateWidget = false;
                    return;
                }
                chatOption++;
            }
        }

        highlightedTargets = buildTargetstoHighlight();
    }

    private List<NPC> buildTargetstoHighlight() {
        if (npcID < 0){
            return new ArrayList<>();
        }
        List<NPC> npcs = new ArrayList<>();

        for (NPC npc: client.getNpcs()){
            if (npc.getId() == npcID){
                npcs.add(npc);
            }
        }
        return npcs;

    }

    @Subscribe
    public void onWidgetClicked(MenuOptionClicked event){
        if (widget != null && widget.getId() == event.getWidgetId()){
            widget = null;
            chatOption = -1;
        }
        log.debug("{}", QuestUtil.toFileName(event.getMenuTarget()));

    }


}
