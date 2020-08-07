package software.bigbade.playervaults.managers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;
import software.bigbade.playervaults.BetterPlayerVaults;
import software.bigbade.playervaults.messages.MessageBundle;
import software.bigbade.playervaults.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class MessageManager {
    private static final String TRANSLATION_URL = "https://gitcdn.link/repo/BigBadE/BPVTranslations/master/";

    private final List<MessageBundle> messageBundles = new ArrayList<>();
    @Getter
    private final List<String> translations = new ArrayList<>();
    private MessageBundle current;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private static MessageManager instance;

    public MessageManager() {
        setInstance(this);
    }

    public void setMainLanguage(String language) {
        for(MessageBundle bundle : messageBundles) {
            if(bundle.getLanguage().equalsIgnoreCase(language)) {
                current = bundle;
                return;
            }
        }
        if(current == null) {
            if(messageBundles.isEmpty()) {
                BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "No valid translation files found!");
                current = new MessageBundle("english");
            } else {
                current = messageBundles.get(0);
            }
        }
    }

    public void loadMessages(File dataFolder) {
        File translationFolder = new File(dataFolder, "translations");
        if(!translationFolder.exists() || translationFolder.listFiles() == null) {
            FileUtils.createDirectory(translationFolder);
            MessageManager.downloadTranslations(translationFolder);
        } else {
            File versionFile = new File(translationFolder, "version.yml");
            if(!versionFile.exists()) {
                MessageManager.downloadTranslations(translationFolder);
            } else {
                byte[] version = FileUtils.read(versionFile);
                URL ymlURL = FileUtils.getURL(TRANSLATION_URL + "version.yml");
                FileUtils.copyURLToFile(ymlURL, versionFile);
                if (!Arrays.equals(version, FileUtils.read(versionFile))) {
                    MessageManager.downloadTranslations(translationFolder);
                }
            }
        }

        for(File file : translationFolder.listFiles()) {
            if(!file.getName().endsWith(".yml")) {
                continue;
            }
            YamlConfiguration yamlConfiguration = FileUtils.loadYamlFile(file);
            MessageBundle bundle = new MessageBundle(file.getName().substring(0, file.getName().length()-4));
            for(String key : yamlConfiguration.getKeys(true)) {
                String value = yamlConfiguration.getString(key);
                if(value != null) {
                    bundle.addMessage(key, yamlConfiguration.getString(key));
                }
            }
            messageBundles.add(bundle);
        }
    }

    private static void downloadTranslations(File translationFolder) {
        BetterPlayerVaults.getPluginLogger().log(Level.INFO, "Downloading translations from {0}", TRANSLATION_URL);
        List<String> languages = MessageManager.getLanguages();
        for(String language : languages) {
            URL ymlURL = FileUtils.getURL(TRANSLATION_URL + language.toLowerCase() + ".yml");
            FileUtils.copyURLToFile(ymlURL, new File(translationFolder, language.toLowerCase() + ".yml"));
        }
    }

    private static List<String> getLanguages() {
        URL translationsURL = FileUtils.getURL(TRANSLATION_URL + "languages.txt");
        if(translationsURL == null) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not find translations at {0}", TRANSLATION_URL);
            return Collections.emptyList();
        }
        try(InputStream stream = translationsURL.openStream();
            InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            BufferedReader read = new BufferedReader(streamReader)) {
            List<String> languages = new ArrayList<>();
            String i;
            while ((i = read.readLine()) != null) {
                languages.add(i);
            }
            return languages;
        } catch (IOException e) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Error downloading translations from {0}", TRANSLATION_URL);
        }
        return Collections.emptyList();
    }

    public String getMessage(String key) {
        return current.getMessage(key);
    }
}
