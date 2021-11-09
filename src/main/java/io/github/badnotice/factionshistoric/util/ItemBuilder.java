package io.github.badnotice.factionshistoric.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Henry Fábio
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemBuilder {

    private final ItemStack itemStack;

    // creation static methods

    public static ItemBuilder of(ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    public static ItemBuilder of(Material material) {
        return of(new ItemStack(material));
    }

    public static ItemBuilder of(Material material, int durability) {
        return of(new ItemStack(material, 1, (short) durability));
    }

    public static ItemBuilder of(MaterialData materialData) {
        return of(materialData.toItemStack(1));
    }

    public static ItemBuilder of(Material material, int durability, int amount) {
        return of(new ItemStack(material, amount, (short) durability));
    }

    public static ItemBuilder of(String url) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        if (url == null || url.isEmpty())
            return of(skull);

        SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();


        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

        Field profileField;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
            return  of(skull);
        }

        profileField.setAccessible(true);

        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return  of(skull);
        }


        skull.setItemMeta(skullMeta);
        return of(skull);
    }

    // apply methods

    public ItemBuilder type(Material material) {
        return modifyItemStack($ -> $.setType(material));
    }

    public ItemBuilder amount(int amount) {
        return modifyItemStack($ -> $.setAmount(amount));
    }

    public ItemBuilder durability(int durability) {
        return modifyItemStack($ -> $.setDurability((short) durability));
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        return modifyItemStack($ -> $.addUnsafeEnchantment(enchantment, level));
    }

    public ItemBuilder unbreakable() {
        return modifyItemMeta(itemMeta -> itemMeta.spigot().setUnbreakable(true));
    }

    public ItemBuilder displayName(String displayName) {
        return modifyItemMeta(itemMeta -> itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName)));
    }

    /*
        public ItemBuilder lore(List<String> lore) {
            return modifyItemMeta(itemMeta -> itemMeta.setLore(lore));
        }


     */
    public ItemBuilder itemFlags(ItemFlag... itemFlags) {
        return modifyItemMeta(itemMeta -> itemMeta.addItemFlags(itemFlags));
    }

    public ItemBuilder glow() {
        enchantment(Enchantment.ARROW_DAMAGE, 1);
        return itemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    public ItemBuilder removeItemFlags(ItemFlag... itemFlags) {
        return modifyItemMeta(itemMeta -> itemMeta.removeItemFlags(itemFlags));
    }

    public ItemBuilder lore(List<String> lore) {
        if (lore == null || lore.isEmpty() || lore.get(0).equalsIgnoreCase("nulo")) return this;
        modifyItemMeta(meta -> meta.setLore(lore.stream().map(string -> ChatColor.translateAlternateColorCodes('&', string)).collect(Collectors.toList())));
        return this;
    }

    // retrieve methods

    public Material type() {
        return adaptItemStack(ItemStack::getType);
    }

    public int amount() {
        return adaptItemStack(ItemStack::getAmount);
    }

    public short durability() {
        return adaptItemStack(ItemStack::getDurability);
    }

    public String displayName() {
        return adaptItemMeta(ItemMeta::getDisplayName);
    }

    public List<String> lore() {
        return adaptItemMeta(ItemMeta::getLore);
    }

    public Set<ItemFlag> itemFlags() {
        return adaptItemMeta(ItemMeta::getItemFlags);
    }

    // private methods

    private ItemBuilder modifyItemStack(Consumer<ItemStack> consumer) {
        consumer.accept(this.itemStack);
        return this;
    }

    private <T> T adaptItemStack(Function<ItemStack, T> consumer) {
        return consumer.apply(this.itemStack);
    }

    private ItemBuilder modifyItemMeta(Consumer<ItemMeta> consumer) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        consumer.accept(itemMeta);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    private <T> T adaptItemMeta(Function<ItemMeta, T> consumer) {
        return consumer.apply(this.itemStack.getItemMeta());
    }

}
