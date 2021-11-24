package io.github.badnotice.factionshistoric.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public final class SkullHelper {

    private final static Map<String, ItemStack> HEAD_CACHE;

    static {
        HEAD_CACHE = Maps.newConcurrentMap();
    }

    public static ItemStack getTextureHead(String url) {
        return HEAD_CACHE.computeIfAbsent(url, k -> createTexture(url)).clone();
    }

    public static ItemStack getPlayerHead(Player player) {
        return getPlayerHead(player.getName());
    }

    public static ItemStack getPlayerHead(String playerName){
        return HEAD_CACHE.computeIfAbsent(playerName, k -> createPlayerHead(playerName)).clone();
    }

    private static ItemStack createPlayerHead(String playerName) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();

        skullMeta.setOwner(playerName);
        skull.setItemMeta(skullMeta);

        return skull;
    }

    private static ItemStack createTexture(String url) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
        gameProfile.getProperties().put("textures", new Property("textures", url));

        try{
            Field gameProfileField = skullMeta.getClass().getDeclaredField("profile");
            gameProfileField.setAccessible(true);
            gameProfileField.set(skullMeta, gameProfile);
        } catch (IllegalAccessException | NoSuchFieldException error) {
            error.printStackTrace();
        }

        skull.setItemMeta(skullMeta);
        return skull;
    }

}
