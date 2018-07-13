package net.runelite.client.plugins.quest;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.*;
import net.runelite.api.*;

@Slf4j
@Getter
@Setter
public class QuestSession {
    private String name;
    private int varPlayer;
    private int items;
    private int step = -1;
    private QuestUtil.Quest quest;

    QuestSession(QuestUtil.Quest quest){
        this.quest = quest;
        this.name = quest.name;
        this.varPlayer = quest.varPlayer;
    }

    int getPlayerStep(Client client){
        if (varPlayer >= 0) {
            int[] varps = client.getVarps();
            if (varps.length > varPlayer){
                int questVar = varps[varPlayer];
                log.debug("{}", questVar);
                return questVar;
            }
        }
        return -1;
    }

    boolean playerStepChanged(Client client){
        int newPlayerStep = getPlayerStep(client);
        if (newPlayerStep != step){
            step = newPlayerStep;
            return true;
        }
        return false;
    }

    QuestUtil.State getCurrentState(){
        return getState(step);
    }

    QuestUtil.State getState(int state){
        if (quest.states != null && quest.states.length > state){
            return quest.states[state];
        }
        return null;
    }


}
