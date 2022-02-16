package cat.nyaa.aolib.npc;

import com.comphenix.protocol.wrappers.Pair;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class NpcPlayerMap {
    Multimap<String, UUID> npcPlayerMap = HashMultimap.create();
    Multimap<UUID, String> playerNpcMap = HashMultimap.create();

    public boolean deepCheck() {
        var nameSet1 = new HashSet<>(npcPlayerMap.keySet());
        var nameSet2 = new HashSet<>(playerNpcMap.values());
        if (!nameSet1.equals(nameSet2)) return false;
        var idSet1 = new HashSet<>(playerNpcMap.keySet());
        var idSet2 = new HashSet<>(npcPlayerMap.values());
        if (!idSet1.equals(idSet2)) return false;
        var pairHashSet1 = new HashSet<Pair<UUID, String>>();
        npcPlayerMap.entries().forEach(stringUUIDEntry -> pairHashSet1.add(new Pair<>(stringUUIDEntry.getValue(), stringUUIDEntry.getKey())));
        var pairHashSet2 = new HashSet<Pair<UUID, String>>();
        playerNpcMap.entries().forEach(stringUUIDEntry -> pairHashSet2.add(new Pair<>(stringUUIDEntry.getKey(), stringUUIDEntry.getValue())));
        return pairHashSet1.equals(pairHashSet2);
    }

    public void checkInvalid(Set<UUID> onlinePlayers, Set<String> loadedNpcNames) {
        var uuids = new HashSet<>(playerNpcMap.keySet());
        uuids.removeAll(onlinePlayers);
        uuids.forEach(this::removeAll);

        var names = new HashSet<>(npcPlayerMap.keySet());
        names.removeAll(loadedNpcNames);
        names.forEach(this::removeAll);
        //retainAll
    }

    public void putNpc(@NotNull Player player, @NotNull IAoEntityNpc npc) {
        npcPlayerMap.put(npc.getUniqueNpcName(), player.getUniqueId());
        playerNpcMap.put(player.getUniqueId(), npc.getUniqueNpcName());
    }

    public void removeNpc(@NotNull Player player, @NotNull IAoEntityNpc npc) {
        npcPlayerMap.remove(npc.getUniqueNpcName(), player.getUniqueId());
        playerNpcMap.remove(player.getUniqueId(), npc.getUniqueNpcName());
    }

    private void removeAll(UUID playerId) {
        playerNpcMap.removeAll(playerId);
        npcPlayerMap.entries().removeIf(stringUUIDEntry -> stringUUIDEntry.getValue().equals(playerId));
    }

    private void removeAll(String npcName) {
        npcPlayerMap.removeAll(npcName);
        playerNpcMap.entries().removeIf(uuidStringEntry -> uuidStringEntry.getValue().equals(npcName));
    }

    public @NotNull Collection<String> getPlayerHoldNpcNames(UUID playerId) {
        return playerNpcMap.get(playerId);
    }

    public @NotNull Collection<UUID> getNpcHoldPlayerIds(String npcName) {
        return npcPlayerMap.get(npcName);
    }

    public Set<Player> getNpcHoldOnlinePlayer(String npcName) {
        return getNpcHoldPlayerIds(npcName).stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public Set<String> getActiveNpcNameSet() {
        return npcPlayerMap.keySet();
    }
}
