package me.waleks.simplematerialgenerators.config;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import me.waleks.simplematerialgenerators.SMGItems;
import me.waleks.simplematerialgenerators.SimpleMaterialGenerators;
import me.waleks.simplematerialgenerators.items.BrokenGenerator;
import me.waleks.simplematerialgenerators.items.GeneratorMultiblock;
import me.waleks.simplematerialgenerators.items.MaterialGenerator;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;

/**
 * Manages loading and registering generators from config
 */
public class ConfigManager {

    private final SimpleMaterialGenerators plugin;
    private final Map<String, GeneratorConfig> generators = new LinkedHashMap<>();
    private final Map<String, SlimefunItemStack> generatorItems = new HashMap<>();

    public ConfigManager(@Nonnull SimpleMaterialGenerators plugin) {
        this.plugin = plugin;
    }

    /**
     * Load all generators from config and register them
     */
    public void loadAndRegister() {
        loadGeneratorsConfig();
        registerAllGenerators();
    }

    /**
     * Load generators from generators.yml
     */
    private void loadGeneratorsConfig() {
        File configFile = new File(plugin.getDataFolder(), "generators.yml");
        
        // Always save default if not exists
        if (!configFile.exists()) {
            plugin.getLogger().info("Creating default generators.yml...");
            plugin.saveResource("generators.yml", false);
        }

        plugin.getLogger().info("Loading generators from: " + configFile.getAbsolutePath());

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        ConfigurationSection generatorsSection = config.getConfigurationSection("generators");
        if (generatorsSection == null) {
            plugin.getLogger().severe("ERROR: No 'generators' section found in generators.yml!");
            plugin.getLogger().severe("Please check your generators.yml file format!");
            return;
        }

        Set<String> keys = generatorsSection.getKeys(false);
        plugin.getLogger().info("Found " + keys.size() + " generator entries in config.");

        for (String id : keys) {
            try {
                plugin.getLogger().info("Loading generator: " + id);
                GeneratorConfig genConfig = loadGeneratorConfig(id, generatorsSection.getConfigurationSection(id));
                if (genConfig != null) {
                    generators.put(id, genConfig);
                    plugin.getLogger().info("  -> Loaded: " + id + " [" + genConfig.getCategory() + "] (enabled=" + genConfig.isEnabled() + ")");
                } else {
                    plugin.getLogger().warning("  -> Failed to load: " + id + " (returned null)");
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "  -> ERROR loading generator: " + id, e);
            }
        }

        plugin.getLogger().info("Total generators loaded: " + generators.size());
    }

    /**
     * Load a single generator config
     */
    @Nullable
    private GeneratorConfig loadGeneratorConfig(@Nonnull String id, @Nullable ConfigurationSection section) {
        if (section == null) {
            plugin.getLogger().warning("    Section is null for: " + id);
            return null;
        }

        boolean enabled = section.getBoolean("enabled", true);
        String name = section.getString("name", "&7Generator");
        String materialStr = section.getString("material", "STONE");
        String outputStr = section.getString("output", "NONE");
        int rate = section.getInt("rate", 4);
        boolean isBroken = section.getBoolean("is_broken", false);
        String category = section.getString("category", "generators"); // Default to generators
        List<String> lore = section.getStringList("lore");

        plugin.getLogger().info("    name=" + name + ", category=" + category + ", material=" + materialStr);

        // Recipe
        ConfigurationSection recipeSection = section.getConfigurationSection("recipe");
        String recipeType = "ENHANCED_CRAFTING_TABLE";
        List<String> recipeShape = new ArrayList<>(Arrays.asList("   ", "   ", "   "));
        Map<String, String> recipeIngredients = new HashMap<>();

        if (recipeSection != null) {
            recipeType = recipeSection.getString("type", "ENHANCED_CRAFTING_TABLE");
            List<String> configShape = recipeSection.getStringList("shape");
            if (!configShape.isEmpty()) {
                recipeShape = new ArrayList<>(configShape);
            }
            ConfigurationSection ingredientsSection = recipeSection.getConfigurationSection("ingredients");
            if (ingredientsSection != null) {
                for (String key : ingredientsSection.getKeys(false)) {
                    recipeIngredients.put(key, ingredientsSection.getString(key, "AIR"));
                }
            }
        }

        Material material = Material.matchMaterial(materialStr);
        if (material == null) {
            plugin.getLogger().warning("    Invalid material: " + materialStr + ", using STONE");
            material = Material.STONE;
        }

        Material output = null;
        if (!outputStr.equalsIgnoreCase("NONE")) {
            output = Material.matchMaterial(outputStr);
            if (output == null) {
                plugin.getLogger().warning("    Invalid output material: " + outputStr);
            }
        }

        return new GeneratorConfig(id, enabled, name, material, output, rate, isBroken,
                category, lore, recipeType, recipeShape, recipeIngredients);
    }

    /**
     * Register all loaded generators
     */
    private void registerAllGenerators() {
        plugin.getLogger().info("Creating SlimefunItemStacks...");
        
        // First, create all SlimefunItemStacks
        for (GeneratorConfig config : generators.values()) {
            if (!config.isEnabled()) {
                plugin.getLogger().info("  Skipping disabled generator: " + config.getId());
                continue;
            }
            SlimefunItemStack itemStack = createItemStack(config);
            generatorItems.put(config.getId(), itemStack);
            plugin.getLogger().info("  Created item: " + config.getSlimefunId());
        }

        // Register multiblock example in Generators category
        plugin.getLogger().info("Registering multiblock guide...");
        new GeneratorMultiblock(SMGItems.SMG_GENERATORS_GROUP, SMGItems.SMG_GENERATOR_MULTIBLOCK)
                .register(plugin);

        // Then register all generators
        plugin.getLogger().info("Registering generators...");
        for (GeneratorConfig config : generators.values()) {
            if (!config.isEnabled()) continue;
            try {
                registerGenerator(config);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to register generator: " + config.getId(), e);
            }
        }
        
        plugin.getLogger().info("Registration complete! Total registered: " + generatorItems.size());
    }

    /**
     * Create SlimefunItemStack from config
     */
    @Nonnull
    private SlimefunItemStack createItemStack(@Nonnull GeneratorConfig config) {
        String[] loreArray = config.getLore().toArray(new String[0]);
        return new SlimefunItemStack(
                config.getSlimefunId(),
                config.getMaterial(),
                config.getName(),
                loreArray
        );
    }

    /**
     * Get the appropriate ItemGroup for a generator
     */
    @Nonnull
    private ItemGroup getItemGroup(@Nonnull GeneratorConfig config) {
        if (config.isMaterialCategory()) {
            return SMGItems.SMG_MATERIALS_GROUP;
        }
        return SMGItems.SMG_GENERATORS_GROUP;
    }

    /**
     * Register a single generator
     */
    private void registerGenerator(@Nonnull GeneratorConfig config) {
        SlimefunItemStack itemStack = generatorItems.get(config.getId());
        if (itemStack == null) {
            plugin.getLogger().warning("  ItemStack not found for: " + config.getId());
            return;
        }

        ItemGroup itemGroup = getItemGroup(config);
        RecipeType recipeType = getRecipeType(config.getRecipeType());
        ItemStack[] recipe = buildRecipe(config);

        if (config.isBroken()) {
            // Broken generator - cannot be placed, goes to Materials category
            new BrokenGenerator(itemGroup, itemStack, recipeType, recipe)
                    .register(plugin);
            plugin.getLogger().info("  Registered BROKEN: " + config.getId() + " -> [" + config.getCategory() + "]");
        } else {
            // Normal generator with BlockMenu
            MaterialGenerator generator = new MaterialGenerator(
                    itemGroup,
                    itemStack,
                    recipeType,
                    recipe
            );

            if (config.getOutput() != null) {
                generator.setItem(config.getOutput());
            }
            generator.setRate(config.getRate());
            generator.register(plugin);
            plugin.getLogger().info("  Registered: " + config.getId() + " -> [" + config.getCategory() + "]");
        }
    }

    /**
     * Get RecipeType from string
     */
    @Nonnull
    private RecipeType getRecipeType(@Nonnull String type) {
        return switch (type.toUpperCase()) {
            case "SMELTERY" -> RecipeType.SMELTERY;
            case "GRIND_STONE" -> RecipeType.GRIND_STONE;
            case "ORE_CRUSHER" -> RecipeType.ORE_CRUSHER;
            case "MAGIC_WORKBENCH" -> RecipeType.MAGIC_WORKBENCH;
            case "ARMOR_FORGE" -> RecipeType.ARMOR_FORGE;
            case "COMPRESSOR" -> RecipeType.COMPRESSOR;
            case "PRESSURE_CHAMBER" -> RecipeType.PRESSURE_CHAMBER;
            default -> RecipeType.ENHANCED_CRAFTING_TABLE;
        };
    }

    /**
     * Build recipe from config shape and ingredients
     */
    @Nonnull
    private ItemStack[] buildRecipe(@Nonnull GeneratorConfig config) {
        ItemStack[] recipe = new ItemStack[9];
        Arrays.fill(recipe, null);

        List<String> shape = new ArrayList<>(config.getRecipeShape());
        Map<String, String> ingredients = config.getRecipeIngredients();

        // Pad shape to 3 rows
        while (shape.size() < 3) {
            shape.add("   ");
        }

        for (int row = 0; row < 3; row++) {
            String rowStr = shape.get(row);
            // Pad row to 3 characters
            while (rowStr.length() < 3) {
                rowStr += " ";
            }

            for (int col = 0; col < 3; col++) {
                int index = row * 3 + col;
                char key = rowStr.charAt(col);

                if (key == ' ') {
                    recipe[index] = null;
                } else {
                    String ingredient = ingredients.get(String.valueOf(key));
                    if (ingredient != null) {
                        recipe[index] = parseIngredient(ingredient);
                    }
                }
            }
        }

        return recipe;
    }

    /**
     * Parse ingredient string to ItemStack
     */
    @Nullable
    private ItemStack parseIngredient(@Nonnull String ingredient) {
        if (ingredient.startsWith("SF:")) {
            String sfId = ingredient.substring(3);
            ItemStack item = getSlimefunItem(sfId);
            if (item == null) {
                plugin.getLogger().warning("    Slimefun item not found: " + sfId);
            }
            return item;
        } else if (ingredient.startsWith("GEN:")) {
            String genId = ingredient.substring(4);
            SlimefunItemStack genItem = generatorItems.get(genId);
            if (genItem != null) {
                return genItem;
            }
            plugin.getLogger().warning("    Generator not found: " + genId);
            return null;
        } else {
            Material material = Material.matchMaterial(ingredient);
            if (material != null) {
                return new ItemStack(material);
            }
            plugin.getLogger().warning("    Unknown material: " + ingredient);
            return null;
        }
    }

    /**
     * Get Slimefun item by ID
     */
    @Nullable
    private ItemStack getSlimefunItem(@Nonnull String id) {
        try {
            Field field = SlimefunItems.class.getField(id);
            Object value = field.get(null);
            if (value instanceof SlimefunItemStack) {
                return (SlimefunItemStack) value;
            } else if (value instanceof ItemStack) {
                return (ItemStack) value;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            SlimefunItem sfItem = SlimefunItem.getById(id);
            if (sfItem != null) {
                return sfItem.getItem();
            }
        }
        return null;
    }

    @Nonnull
    public Map<String, GeneratorConfig> getGenerators() {
        return Collections.unmodifiableMap(generators);
    }

    @Nullable
    public SlimefunItemStack getGeneratorItem(@Nonnull String id) {
        return generatorItems.get(id);
    }
}
