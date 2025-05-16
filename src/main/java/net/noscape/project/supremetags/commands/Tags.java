package net.noscape.project.supremetags.commands;

import net.noscape.project.supremetags.SupremeTags;
import net.noscape.project.supremetags.guis.MainMenu;
import net.noscape.project.supremetags.guis.TagMenu;
import net.noscape.project.supremetags.guis.tageditor.TagEditorMenu;
import net.noscape.project.supremetags.handlers.Tag;
import net.noscape.project.supremetags.storage.UserData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import static net.noscape.project.supremetags.utils.Utils.msgPlayer;
import static net.noscape.project.supremetags.utils.Utils.removePerm;

public class Tags implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {

        String reload = SupremeTags.getInstance().getConfig().getString("messages.reload");
        String noperm = SupremeTags.getInstance().getConfig().getString("messages.no-permission");
        String notags = SupremeTags.getInstance().getConfig().getString("messages.no-tags");
        String commanddisabled = SupremeTags.getInstance().getConfig().getString("messages.tag-command-disabled");
        String invalidtag = SupremeTags.getInstance().getConfig().getString("messages.invalid-tag");

        if (!(sender instanceof Player player)) {
            if (cmd.getName().equalsIgnoreCase("tags")) {
                if (args.length == 0) {
                    sendHelp(sender);
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("create")) {
                        String name = args[1];
                        String tag = args[2];

                        SupremeTags.getInstance().getTagManager().createTag(sender, name, tag, "&7My tag is " + name, "supremetags.tag." + name, 0);
                    } else if (args[0].equalsIgnoreCase("settag")) {
                        String name = args[1];
                        String tag = args[2];
                        SupremeTags.getInstance().getTagManager().setTag(sender, name, tag);
                    } else if (args[0].equalsIgnoreCase("set")) {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                        String identifier = args[2];

                        if (SupremeTags.getInstance().getTagManager().getTags().containsKey(identifier)) {
                            UserData.setActive(target, identifier);
                            msgPlayer(sender, "&8[&6&lTag&8] &7Set &b" + target.getName() + "'s &7tag to &b" + identifier);
                        } else {
                            msgPlayer(sender, invalidtag);
                        }
                    }
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {

                        SupremeTags.getInstance().reload();

                        SupremeTags.getInstance().getTagManager().getDataItem().clear();

                        SupremeTags.getInstance().getTagManager().setCost(SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system"));

                        SupremeTags.getInstance().getCategoryManager().loadCategories();
                        SupremeTags.getInstance().getCategoryManager().loadCategoriesTags();
                        msgPlayer(sender, reload);
                    } else if (args[0].equalsIgnoreCase("help")) {
                        sendHelp(sender);
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("delete")) {
                        String name = args[1];
                        SupremeTags.getInstance().getTagManager().deleteTag(sender, name);
                    } else if (args[0].equalsIgnoreCase("reset")) {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                        Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
                        if (vault != null) {
                            for (Tag tag : SupremeTags.getInstance().getTagManager().getTags().values()) {
                                String permission = tag.getPermission();
                                for (World world : Bukkit.getWorlds())
                                    if (SupremeTags.getPermissions().playerHas(world.getName(), target, permission)) {
                                        removePerm(target, permission);
                                    }
                            }
                        }

                        if (SupremeTags.getInstance().getConfig().isBoolean("settings.forced-tag")) {
                            String defaultTag = SupremeTags.getInstance().getConfig().getString("settings.default-tag");

                            UserData.setActive(target, defaultTag);
                            msgPlayer(sender, "&8[&6&lTag&8] &7Reset &b" + target.getName() + "'s &7tag back to " + defaultTag);
                        } else {
                            UserData.setActive(target, "None");
                            msgPlayer(sender, "&8[&6&lTag&8] &7Reset &b" + target.getName() + "'s &7tag back to None.");
                        }
                    } else {
                        sendHelp(sender);
                    }
                }
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("tags")) {
            if (args.length == 0) {
                if (player.hasPermission("supremetags.menu")) {
                    if (!SupremeTags.getInstance().isDisabledWorldsTag()) {
                        boolean lockedView = SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view");
                        boolean costSystem = SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system");
                        boolean useCategories = SupremeTags.getInstance().getConfig().getBoolean("settings.categories");

                        if ((!lockedView && !costSystem)) {
                            if (hasTags(player)) {
                                if (useCategories) {
                                    new MainMenu(SupremeTags.getMenuUtil(player)).open();
                                } else {
                                    new TagMenu(SupremeTags.getMenuUtil(player)).open();
                                }
                            } else {
                                msgPlayer(player, notags);
                            }
                        } else {
                            if (useCategories) {
                                new MainMenu(SupremeTags.getMenuUtil(player)).open();
                            } else {
                                new TagMenu(SupremeTags.getMenuUtil(player)).open();
                            }
                        }
                    } else {
                        for (String world : SupremeTags.getInstance().getConfig().getStringList("settings.disabled-worlds")) {
                            if (player.getWorld().getName().equalsIgnoreCase(world)) {
                                msgPlayer(player, commanddisabled);
                            } else {
                                boolean lockedView = SupremeTags.getInstance().getConfig().getBoolean("settings.locked-view");
                                boolean costSystem = SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system");
                                boolean useCategories = SupremeTags.getInstance().getConfig().getBoolean("settings.categories");

                                if ((!lockedView && !costSystem)) {
                                    if (hasTags(player)) {
                                        if (useCategories) {
                                            new MainMenu(SupremeTags.getMenuUtil(player)).open();
                                        } else {
                                            new TagMenu(SupremeTags.getMenuUtil(player)).open();
                                        }
                                    } else {
                                        msgPlayer(player, notags);
                                    }
                                } else {
                                    if (useCategories) {
                                        new MainMenu(SupremeTags.getMenuUtil(player)).open();
                                    } else {
                                        new TagMenu(SupremeTags.getMenuUtil(player)).open();
                                    }
                                }
                            }
                            break;
                        }
                    }
                } else {
                    msgPlayer(player, noperm);
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (player.hasPermission("supremetags.admin")) {
                        String name = args[1];
                        String tag = args[2];

                        SupremeTags.getInstance().getTagManager().createTag(player, name, tag, "&7My tag is " + name, "supremetags.tag." + name, 0);
                    } else {
                        msgPlayer(player, noperm);
                    }
                } else if (args[0].equalsIgnoreCase("settag")) {
                    if (player.hasPermission("supremetags.admin")) {
                        String name = args[1];
                        String tag = args[2];

                        SupremeTags.getInstance().getTagManager().setTag(player, name, tag);
                    } else {
                        msgPlayer(player, noperm);
                    }
                } else if (args[0].equalsIgnoreCase("setcategory")) {
                        if (player.hasPermission("supremetags.admin")) {
                            String name = args[1];
                            String category = args[2];

                            SupremeTags.getInstance().getTagManager().setCategory(player, name, category);
                        } else {
                            msgPlayer(player, noperm);
                        }
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (player.hasPermission("supremetags.admin")) {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                        String identifier = args[2];

                        if (SupremeTags.getInstance().getTagManager().getTags().containsKey(identifier)) {
                            UserData.setActive(target, identifier);
                            msgPlayer(player, "&8[&6&lTag&8] &7Set &b" + target.getName() + "'s &7tag to &b" + identifier);
                        } else {
                            msgPlayer(player, invalidtag);
                        }
                    } else {
                        msgPlayer(player, noperm);
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (player.hasPermission("supremetags.admin")) {
                        SupremeTags.getInstance().reload();

                        SupremeTags.getInstance().getTagManager().getDataItem().clear();

                        SupremeTags.getInstance().getTagManager().setCost(SupremeTags.getInstance().getConfig().getBoolean("settings.cost-system"));

                        SupremeTags.getInstance().getCategoryManager().loadCategories();
                        SupremeTags.getInstance().getCategoryManager().loadCategoriesTags();
                        msgPlayer(player, reload);
                    } else {
                        msgPlayer(player, noperm);
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    if (player.hasPermission("supremetags.admin")) {
                        sendHelp(player);
                    } else {
                        msgPlayer(player, noperm);
                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    if (player.hasPermission("supremetags.admin")) {
                        msgPlayer(player,
                                "&8[&6Tags&8] &7There are &f" + SupremeTags.getInstance().getTagManager().getTags().size() + " &7loaded!",
                                "&8[&6Tags&8] &7Do &f/tags editor &7to see/edit all tags loaded!");
                    } else {
                        msgPlayer(player, noperm);
                    }
                } else if (args[0].equalsIgnoreCase("editor")) {
                        if (player.hasPermission("supremetags.admin")) {
                            new TagEditorMenu(SupremeTags.getMenuUtil(player)).open();
                        } else {
                            msgPlayer(player, noperm);
                        }
                } else if (args[0].equalsIgnoreCase("merge")) {
                    if (player.hasPermission("supremetags.admin")) {
                        SupremeTags.getInstance().getMergeManager().merge(player);
                    } else {
                        msgPlayer(player, noperm);
                    }
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("delete")) {
                    if (player.hasPermission("supremetags.admin")) {
                        String name = args[1];

                        SupremeTags.getInstance().getTagManager().deleteTag(player, name);
                    } else {
                        msgPlayer(player, noperm);
                    }
                } else if (args[0].equalsIgnoreCase("reset")) {
                    if (player.hasPermission("supremetags.admin")) {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                        if (SupremeTags.getInstance().getConfig().isBoolean("settings.forced-tag")) {
                            String defaultTag = SupremeTags.getInstance().getConfig().getString("settings.default-tag");

                            UserData.setActive(target, defaultTag);

                            // Check if LuckPerms is available
                            Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
                            if (vault != null) {
                                for (Tag tag : SupremeTags.getInstance().getTagManager().getTags().values()) {
                                    String permission = tag.getPermission();
                                    for (World world : Bukkit.getWorlds())
                                        if (SupremeTags.getPermissions().playerHas(world.getName(), target, permission)) {
                                            removePerm(target, permission);
                                        }
                                }
                            }

                            msgPlayer(player, "&8[&6&lTag&8] &7Reset &b" + target.getName() + "'s &7tag back to " + defaultTag);
                        } else {
                            UserData.setActive(target, "None");
                            msgPlayer(player, "&8[&6&lTag&8] &7Reset &b" + target.getName() + "'s &7tag back to None.");
                        }
                    } else {
                        msgPlayer(player, noperm);
                    }
                } else {
                    sendHelp(player);
                }
            } else {
                sendHelp(sender);
            }
        }
        return false;
    }

    public boolean hasTags(Player player) {
        for (Tag tag : SupremeTags.getInstance().getTagManager().getTags().values()) {
            if (player.hasPermission(tag.getPermission())) {
                return true;
            }
        }
        return false;
    }

    public void sendHelp(Player player) {
        msgPlayer(player, "",
                "&6&lSupremeTags Help:",
                "",
                "&e/tags &7- will open the tag menu.",
                "&e/tags create <identifier> <tag> &7- creates a new tag.",
                "&e/tags delete <identifier> &7- creates a new tag.",
                "&e/tags settag <identifier> <tag> &7- sets tag style for the existing tag.",
                "&e/tags set <player> <identifier> &7- sets a new tag for that player.",
                "&e/tags reset <player> &7- resets the players tag to None.",
                "&e/tags merge &7- merges deluxetags into supremetags.",
                "&e/tags reload &7- reloads the config.yml & unloads/loads tags.",
                "&e/tags help &7- displays this help message.",
                "&e/tags list &7- see how many tags are loaded.",
                "");
    }

    public void sendHelp(CommandSender player) {
        msgPlayer(player, "",
                "&6&lSupremeTags Help:",
                "",
                "&e/tags &7- will open the tag menu.",
                "&e/tags create <identifier> <tag> &7- creates a new tag.",
                "&e/tags delete <identifier> &7- creates a new tag.",
                "&e/tags settag <identifier> <tag> &7- sets tag style for the existing tag.",
                "&e/tags set <player> <identifier> &7- sets a new tag for that player.",
                "&e/tags reset <player> &7- resets the players tag to None.",
                "&e/tags merge &7- merges deluxetags into supremetags.",
                "&e/tags reload &7- reloads the config.yml & unloads/loads tags.",
                "&e/tags help &7- displays this help message.",
                "");
    }
}