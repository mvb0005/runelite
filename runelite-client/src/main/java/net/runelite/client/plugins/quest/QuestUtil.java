package net.runelite.client.plugins.quest;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.util.Text;
import sun.misc.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
class QuestUtil {

    class Quest {
        String name;
        int varPlayer;
        int completed;
        int[] items;
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

    static Quest loadQuest(String questFileName){
        Gson g = new Gson();
        URL resource = QuestUtil.class.getResource(questFileName);
        if (resource == null){
            return null;
        }
        URI uri = null;
        try {
            uri = resource.toURI();
            String jsonFile = new String(Files.readAllBytes(Paths.get(uri)));
            return g.fromJson(jsonFile, Quest.class);
        } catch (URISyntaxException e) {
            log.debug("Error creating URI");
            return null;
        } catch (IOException e) {
            log.debug("Error Reading JSON File");
            return null;
        }
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
