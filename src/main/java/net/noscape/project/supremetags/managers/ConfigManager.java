package net.noscape.project.supremetags.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public class ConfigManager {

    private final JavaPlugin plugin;
    private final HashMap<String, Config> configs = new HashMap<>();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;

        loadConfig("tags.yml");
        loadConfig("categories.yml");
    }

    /**
     * Get the config by the name (Don't forget the .yml)
     *
     * @param name the name of the config file
     * @return the Config object
     */
    public Config getConfig(String name) {
        return configs.computeIfAbsent(name, Config::new);
    }

    /**
     * Save the config by the name (Don't forget the .yml)
     *
     * @param name the name of the config file
     */
    public void saveConfig(String name) {
        getConfig(name).save();
    }

    /**
     * Load the config, ensuring defaults are copied if the file does not exist
     *
     * @param name the name of the config file
     */
    private void loadConfig(String name) {
        Config config = getConfig(name);
        config.saveDefaultConfig(); // Only saves the default config if the file does not exist
        config.reload(); // Reload to ensure the config is properly loaded
    }

    /**
     * Reload the config by the name (Don't forget the .yml)
     *
     * @param name the name of the config file
     */
    public void reloadConfig(String name) {
        getConfig(name).reload();
    }

    public class Config {

        private final String name;
        private File file;
        private YamlConfiguration config;

        public Config(String name) {
            this.name = name;
        }

        /**
         * Saves the config to file
         *
         * @return this Config object
         */
        public Config save() {
            if (config == null || file == null) {
                return this;
            }
            try {
                config.save(file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return this;
        }

        /**
         * Gets the YamlConfiguration instance of this config, loading from the file if necessary
         *
         * @return YamlConfiguration instance
         */
        public YamlConfiguration get() {
            if (config == null) {
                reload();
            }
            return config;
        }

        /**
         * Saves the default config if it doesn't exist
         *
         * @return this Config object
         */
        public Config saveDefaultConfig() {
            this.file = new File(plugin.getDataFolder(), this.name);
            if (!file.exists()) {
                plugin.saveResource(this.name, false);
            }
            return this;
        }

        /**
         * Reloads the config from the file
         */
        public void reload() {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Config name cannot be null or empty");
            }
            this.file = new File(plugin.getDataFolder(), this.name);
            this.config = YamlConfiguration.loadConfiguration(file);

            try (Reader defConfigStream = new InputStreamReader(Objects.requireNonNull(plugin.getResource(name)), "UTF8")) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                config.setDefaults(defConfig);
                config.options().copyDefaults(false); // Do not overwrite existing values
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        /**
         * An easy way to set a value into the config
         *
         * @param key   the key
         * @param value the value
         * @return this Config object
         */
        public Config set(String key, Object value) {
            get().set(key, value);
            save(); // Save changes immediately
            return this;
        }

        /**
         * An easy way to get a value from the config
         *
         * @param key the key
         * @return the value associated with the key
         */
        public Object get(String key) {
            return get().get(key);
        }
    }
}
