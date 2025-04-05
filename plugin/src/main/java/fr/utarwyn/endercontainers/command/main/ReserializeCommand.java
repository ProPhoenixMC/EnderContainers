package fr.utarwyn.endercontainers.command.main;

import com.google.common.io.Files;
import fr.utarwyn.endercontainers.EnderContainers;
import fr.utarwyn.endercontainers.Managers;
import fr.utarwyn.endercontainers.command.AbstractCommand;
import fr.utarwyn.endercontainers.enderchest.EnderChestManager;
import fr.utarwyn.endercontainers.enderchest.context.PlayerContext;
import fr.utarwyn.endercontainers.util.PluginMsg;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ReserializeCommand extends AbstractCommand {
    private final EnderContainers plugin;
    private final EnderChestManager manager;

    public ReserializeCommand(Plugin plugin) {
        super("reserialize");
        this.plugin = (EnderContainers) plugin;
        this.setPermission("endercontainers.reserialize");

        this.manager = Managers.get(EnderChestManager.class);
    }

    @Override
    public void perform(CommandSender sender) {
        plugin.executeTaskOnOtherThread(() -> {
            PluginMsg.pluginBar(sender);

            File dataDir = new File(plugin.getDataFolder(), "data");
            File[] files = dataDir.listFiles();
            Set<UUID> uuids = Arrays.stream(files == null ? new File[0] : files)
                    .map(file -> Files.getNameWithoutExtension(file.getName()))
                    .map(uuidNoDashes -> uuidNoDashes.replaceFirst( "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5" ))
                    .map(UUID::fromString)
                    .collect(Collectors.toSet());

            sender.sendMessage(" ");
            sender.sendMessage("  §7   Re-serializing all players data...");
            sender.sendMessage("  §7   Found §e" + uuids.size() + "§7 profiles");


            Consumer<PlayerContext> playerContextConsumer = playerContext -> {
                manager.savePlayerContext(playerContext.getOwner());
                manager.deletePlayerContextIfUnused(playerContext.getOwner());
            };

            for (UUID uuid : uuids) {
                manager.loadPlayerContext(uuid, playerContextConsumer);
            }

            sender.sendMessage("  §7   Reserialized all saved data");
            sender.sendMessage(" ");
        });
    }
}
