package net.runelite.client.plugins.quest;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.util.Text;
import sun.misc.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
class QuestUtil {

    class Quest {
        int varPlayer;
        int completed;
        State[] states;

        @Override
        public String toString() {
            return varPlayer + " " + completed + " " + states.length;
        }
    }

    class State {
        String type;
        int id;
        int[] location;
        List<String> chatOptions;
    }

    static Quest loadQuest() throws Exception{
        Gson g = new Gson();
        URI uri = QuestUtil.class.getResource("cooks-assistant.json").toURI();
        String jsonFile = new String(Files.readAllBytes(Paths.get(uri)));
        return g.fromJson(jsonFile, Quest.class);
    }

    static String toFileName(String string){
        string = Text.removeTags(string);
        string = string.replaceAll("'", "");
        string = string.replaceAll(" ", "-");
        string = string.toLowerCase();
        string += ".json";
        log.debug(string);
        return string;
    }
}
