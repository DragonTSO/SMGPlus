package me.waleks.simplematerialgenerators.config;

import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Represents a generator configuration loaded from generators.yml
 */
public class GeneratorConfig {

    private final String id;
    private final boolean enabled;
    private final String name;
    private final Material material;
    private final Material output;
    private final int rate;
    private final boolean isBroken;
    private final String category; // "materials" or "generators"
    private final List<String> lore;
    private final String recipeType;
    private final List<String> recipeShape;
    private final Map<String, String> recipeIngredients;

    public GeneratorConfig(
            @Nonnull String id,
            boolean enabled,
            @Nonnull String name,
            @Nonnull Material material,
            @Nullable Material output,
            int rate,
            boolean isBroken,
            @Nonnull String category,
            @Nonnull List<String> lore,
            @Nonnull String recipeType,
            @Nonnull List<String> recipeShape,
            @Nonnull Map<String, String> recipeIngredients
    ) {
        this.id = id;
        this.enabled = enabled;
        this.name = name;
        this.material = material;
        this.output = output;
        this.rate = rate;
        this.isBroken = isBroken;
        this.category = category;
        this.lore = lore;
        this.recipeType = recipeType;
        this.recipeShape = recipeShape;
        this.recipeIngredients = recipeIngredients;
    }

    @Nonnull
    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public Material getMaterial() {
        return material;
    }

    @Nullable
    public Material getOutput() {
        return output;
    }

    public int getRate() {
        return rate;
    }

    public boolean isBroken() {
        return isBroken;
    }

    /**
     * Get the category for this item
     * @return "materials" or "generators"
     */
    @Nonnull
    public String getCategory() {
        return category;
    }

    /**
     * Check if this item should be in the Materials category
     */
    public boolean isMaterialCategory() {
        return "materials".equalsIgnoreCase(category);
    }

    /**
     * Check if this item should be in the Generators category
     */
    public boolean isGeneratorCategory() {
        return "generators".equalsIgnoreCase(category) || category.isEmpty();
    }

    @Nonnull
    public List<String> getLore() {
        return lore;
    }

    @Nonnull
    public String getRecipeType() {
        return recipeType;
    }

    @Nonnull
    public List<String> getRecipeShape() {
        return recipeShape;
    }

    @Nonnull
    public Map<String, String> getRecipeIngredients() {
        return recipeIngredients;
    }

    /**
     * Get the Slimefun item ID for this generator
     */
    @Nonnull
    public String getSlimefunId() {
        return "SMG_GENERATOR_" + id.toUpperCase();
    }
}
