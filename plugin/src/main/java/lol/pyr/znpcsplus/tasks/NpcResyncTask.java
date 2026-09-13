package lol.pyr.znpcsplus.tasks;

import lol.pyr.znpcsplus.npc.NpcEntryImpl;
import lol.pyr.znpcsplus.npc.NpcImpl;
import lol.pyr.znpcsplus.npc.NpcRegistryImpl;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Periodically forces npcs to resend their spawn packets to everyone currently viewing them.
 * <p>
 * Some client-side performance mods (e.g. entity culling mods) can incorrectly decide a npc
 * is no longer visible and stop rendering it without informing the server. Since the server
 * has no way of detecting that this happened, our {@link lol.pyr.znpcsplus.util.Viewable}
 * state and the client's rendering state can silently desync, leaving the npc permanently
 * invisible to that player until something forces a respawn.
 * <p>
 * This task acts as a safety net by periodically re-sending the spawn packets to already
 * visible viewers, which resolves the desync at the cost of a brief flicker.
 */
public class NpcResyncTask extends BukkitRunnable {
    private final NpcRegistryImpl npcRegistry;

    public NpcResyncTask(NpcRegistryImpl npcRegistry) {
        this.npcRegistry = npcRegistry;
    }

    @Override
    public void run() {
        for (NpcEntryImpl entry : npcRegistry.getProcessable()) {
            NpcImpl npc = entry.getNpc();
            if (!npc.isEnabled()) continue;
            if (npc.getViewers().isEmpty()) continue;
            npc.respawn();
        }
    }
}
