package me.waleks.simplematerialgenerators;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

/**
 * Contains the ItemGroups and special items for SMG.
 * Structure: SMG (parent) -> Materials (sub) + Generators (sub)
 */
public final class SMGItems {

    private SMGItems() {
    }

    // ============ PARENT ITEM GROUP ============

    /**
     * Main SMG category (parent) - shows in Slimefun Guide
     */
    public static final NestedItemGroup SMG_MAIN_GROUP = new NestedItemGroup(
            new NamespacedKey(SimpleMaterialGenerators.getInstance(), "smg"),
            new CustomItemStack(Material.SMOOTH_STONE, "&9&lSMG", "&7Simple Material Generators")
    );

    // ============ SUB ITEM GROUPS ============

    /**
     * Sub-category for Materials (broken generators, crafting items)
     */
    public static final SubItemGroup SMG_MATERIALS_GROUP = new SubItemGroup(
            new NamespacedKey(SimpleMaterialGenerators.getInstance(), "smg_materials"),
            SMG_MAIN_GROUP,
            new CustomItemStack(Material.IRON_INGOT, "&6SMG Materials", "&7Broken generators & materials")
    );

    /**
     * Sub-category for Generators (working generators)
     */
    public static final SubItemGroup SMG_GENERATORS_GROUP = new SubItemGroup(
            new NamespacedKey(SimpleMaterialGenerators.getInstance(), "smg_generators"),
            SMG_MAIN_GROUP,
            new CustomItemStack(Material.FURNACE, "&9SMG Generators", "&7Working generators")
    );

    // ============ SPECIAL ITEMS ============

    /**
     * Multiblock example - shows how to set up generators
     */
    public static final SlimefunItemStack SMG_GENERATOR_MULTIBLOCK = new SlimefunItemStack(
            "SMG_GENERATOR_MULTIBLOCK",
            Material.BEDROCK,
            "&9Generator Setup Guide",
            "",
            "&7Place any SMG generator block",
            "&7and it will automatically",
            "&7produce items in its inventory.",
            "",
            "&aExtract items using:",
            "&e- Networks Grabber",
            "&e- Slimefun Cargo",
            "&e- Chest above",
            "",
            "&9&oSimpleMaterialGenerators"
    );
}
