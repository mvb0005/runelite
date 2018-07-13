package net.runelite.client.plugins.quest;

import com.google.common.eventbus.EventBus;
import net.runelite.api.Client;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class QuestPanel extends PluginPanel {

	@Inject
	@Nullable
	private Client client;

	@Inject
	private EventBus eventBus;


    void init() {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel questInfoPanel = new JPanel();
        questInfoPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        questInfoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        questInfoPanel.setLayout(new GridLayout(2,1));

        final Font font = FontManager.getRunescapeFont();

        JLabel questName = new JLabel("Quest Name");
        questName.setFont(font);

        questInfoPanel.add(questName);

        add(questInfoPanel, BorderLayout.SOUTH);
        eventBus.register(this);
    }

}
