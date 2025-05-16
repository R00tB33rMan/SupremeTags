package net.noscape.project.supremetags.listeners;

import lombok.Getter;
import net.noscape.project.supremetags.SupremeTags;
import net.noscape.project.supremetags.checkers.UpdateChecker;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.storage.UserData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;

import static net.noscape.project.supremetags.utils.Utils.*;

@Getter
public class PlayerEvents implements Listener {

    private final Map<String, Tag> tags;

    public PlayerEvents() {
        tags = SupremeTags.getInstance().getTagManager().getTags();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UserData.createPlayer(player);

        if (SupremeTags.getInstance().getConfig().getBoolean("settings.forced-tag")) {
            String activeTag = UserData.getActive(player.getUniqueId());
            if (activeTag.equalsIgnoreCase("None")) {
                String defaultTag = SupremeTags.getInstance().getConfig().getString("settings.default-tag");
                UserData.setActive(player, defaultTag);
            }
        }

        if (!UserData.getActive(player.getUniqueId()).equalsIgnoreCase("None") && !tags.containsKey(UserData.getActive(player.getUniqueId()))) {
            String defaultTag = SupremeTags.getInstance().getConfig().getString("settings.default-tag");
            UserData.setActive(player, defaultTag);
        }

        if (!UserData.getActive(player.getUniqueId()).equalsIgnoreCase("None") && tags.containsKey(UserData.getActive(player.getUniqueId()))) {
            Tag tag = SupremeTags.getInstance().getTagManager().getTag(UserData.getActive(player.getUniqueId()));
            if (!player.hasPermission(tag.getPermission())) {
                String defaultTag = SupremeTags.getInstance().getConfig().getString("settings.default-tag");
                UserData.setActive(player, defaultTag);
            }
        }

        if (SupremeTags.getInstance().getConfig().getBoolean("settings.update-check")) {
            if (player.isOp()) {
                new UpdateChecker(SupremeTags.getInstance()).getVersionAsync(version -> {
                    if (!SupremeTags.getInstance().getDescription().getVersion().equals(version)) {
                        msgPlayer(player, "&6&lSupremeTags &8&l> &7An update is available! &b" + version,
                                "&eDownload at &bhttps://www.spigotmc.org/resources/103140/");
                    }
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String format = e.getFormat();

        if (!UserData.getActive(player.getUniqueId()).equalsIgnoreCase("None") && tags.containsKey(UserData.getActive(player.getUniqueId()))) {
            Tag tag = SupremeTags.getInstance().getTagManager().getTag(UserData.getActive(player.getUniqueId()));
            if (!player.hasPermission(tag.getPermission())) {
                String defaultTag = SupremeTags.getInstance().getConfig().getString("settings.default-tag");
                UserData.setActive(player, defaultTag);
            }
        }

        // Store the value of UserData.getActive(player.getUniqueId()) in a local variable
        String activeTag = UserData.getActive(player.getUniqueId());
        String replace = format.replace("{tag}", "").replace("{supremetags_tag}", "").replace("{TAG}", "");
        if (activeTag == null || activeTag.equalsIgnoreCase("None")) {
            e.setFormat(replace);
        } else {
            // Store the value of SupremeTags.getInstance().getTagManager().getTags().get(activeTag) in a local variable
            Tag tag = SupremeTags.getInstance().getTagManager().getTags().get(activeTag);
            if (tag == null) {
                e.setFormat(replace);
            } else {
                // Store the value of format(tag.getTag()) in a local variable
                String formattedTag = format(tag.getTag()); // Escaping $
                formattedTag = replacePlaceholders(player, formattedTag);

                e.setFormat(format.replace("{tag}", formattedTag).replace("{supremetags_tag}", formattedTag).replace("{TAG}", formattedTag));
            }
        }
    }

}