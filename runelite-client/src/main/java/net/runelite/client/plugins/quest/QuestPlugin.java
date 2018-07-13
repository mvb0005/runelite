package net.runelite.client.plugins.quest;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Provides;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.info.InfoPanel;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.PluginToolbar;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
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
    private QuestConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private QuestOverlay overlay;

    @Inject
    private PluginToolbar pluginToolbar;

    @Getter
    private QuestSession session;

    @Getter(AccessLevel.PACKAGE)
    private List<NPC> highlightedTargets = new ArrayList<>();

    @Getter(AccessLevel.PACKAGE)
    Color overlayColor = new Color(50,250,60);

    Widget widget;
    int chatOption;
    private Boolean updateWidget = false;
    private int npcID;
    private List<String> chatOptions;

    private NavigationButton navButton;

    @Provides
    QuestConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(QuestConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);

        final QuestPanel panel = injector.getInstance(QuestPanel.class);
        panel.init();

        BufferedImage icon;
        synchronized (ImageIO.class)
        {
            icon = ImageIO.read(getClass().getResourceAsStream("quest_icon.png"));
        }

        navButton = NavigationButton.builder()
                .tooltip("Quest")
                .icon(icon)
                .priority(1)
                .panel(panel)
                .build();

        pluginToolbar.addNavigation(navButton);

        log.debug("{}", QuestUtil.loadQuest("cooks-assistant.json"));
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        session = null;
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded){
        if (session == null){
            return;
        }

        if (widgetLoaded.getGroupId() == 219){
            widget = client.getWidget(219,0);
            updateWidget = true;
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (session == null){
            return;
        }

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
            chatOption = -1;
        }

        if (session.playerStepChanged(client)){
            QuestUtil.State state = session.getCurrentState();
            if (state.chatOptions != null){
                chatOptions = state.chatOptions;
            }
            switch (state.type){
                case "npc":
                    npcID = state.id;
                    break;
                default:
                    log.debug("Unknown State");
                    break;
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

        if (event.getMenuOption().equalsIgnoreCase("guide:")){
            String questFileName = QuestUtil.toFileName(event.getMenuTarget());
            QuestUtil.Quest quest = QuestUtil.loadQuest(questFileName);
            if (quest == null){
                return;
            }
            session = new QuestSession(quest);
            session.getPlayerStep(client);
        }

        if (event.getMenuOption().equalsIgnoreCase("Remove Guide")){
            session = null;
            npcID = -1;
            highlightedTargets = null;
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event){

        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        String option = Text.removeTags(event.getOption()).toLowerCase();
        String target = Text.removeTags(event.getTarget()).toLowerCase();

        if (option.equals("read journal:")){
            ArrayList<MenuEntry> menuEntries = new ArrayList<>(Arrays.asList(client.getMenuEntries()));
            MenuEntry m = new MenuEntry();
            m.setOption("Guide:");
            m.setTarget(event.getTarget());
            m.setType(0);
            menuEntries.add(1,m);
            MenuEntry[] newMenuEntries = new MenuEntry[menuEntries.size()];
            client.setMenuEntries(menuEntries.toArray(newMenuEntries));
        }

        if (session != null && option.equalsIgnoreCase("quest list")){
            ArrayList<MenuEntry> menuEntries = new ArrayList<>(Arrays.asList(client.getMenuEntries()));
            MenuEntry m = new MenuEntry();
            m.setOption("Remove Guide");
            m.setTarget(event.getTarget());
            m.setType(0);
            menuEntries.add(1,m);
            MenuEntry[] newMenuEntries = new MenuEntry[menuEntries.size()];
            client.setMenuEntries(menuEntries.toArray(newMenuEntries));
        }
    }

}
