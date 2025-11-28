package me.waleks.simplematerialgenerators.items;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class MaterialGenerator extends SlimefunItem {

    // Output slots - nơi items được tạo ra (slot 13 là giữa GUI 3x9)
    private static final int[] OUTPUT_SLOTS = {13};

    // Border slots để trang trí
    private static final int[] BORDER_SLOTS = {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 11, 12, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26
    };

    // Chest types that can receive items
    private static final Material[] CHEST_TYPES = {
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.BARREL
    };

    private int rate = 2;
    private Material outputMaterial = Material.COBBLESTONE;

    @ParametersAreNonnullByDefault
    public MaterialGenerator(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        // Tạo BlockMenu preset
        new BlockMenuPreset(getId(), getItemName()) {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {
                return p.hasPermission("slimefun.inventory.bypass")
                        || Slimefun.getProtectionManager().hasPermission(p, b.getLocation(), Interaction.INTERACT_BLOCK);
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                // Cho phép Networks Grabber và Cargo lấy items từ OUTPUT_SLOTS
                if (flow == ItemTransportFlow.WITHDRAW) {
                    return OUTPUT_SLOTS;
                }
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
                if (flow == ItemTransportFlow.WITHDRAW) {
                    return OUTPUT_SLOTS;
                }
                return new int[0];
            }
        };

        // Handler khi block bị phá - drop items trong inventory
        addItemHandler(new BlockBreakHandler(false, false) {
            @Override
            @ParametersAreNonnullByDefault
            public void onPlayerBreak(BlockBreakEvent e, ItemStack item, List<ItemStack> drops) {
                Block b = e.getBlock();
                BlockMenu inv = BlockStorage.getInventory(b);
                if (inv != null) {
                    inv.dropItems(b.getLocation(), OUTPUT_SLOTS);
                }
            }
        });
    }

    private void constructMenu(@Nonnull BlockMenuPreset preset) {
        // Tạo border với glass pane
        for (int slot : BORDER_SLOTS) {
            preset.addItem(slot, new CustomItemStack(Material.GRAY_STAINED_GLASS_PANE, " "),
                    ChestMenuUtils.getEmptyClickHandler());
        }
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            @ParametersAreNonnullByDefault
            public void tick(Block b, SlimefunItem sf, Config data) {
                MaterialGenerator.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return true;
            }
        });
    }

    private void tick(@Nonnull Block b) {
        BlockMenu menu = BlockStorage.getInventory(b);
        
        // Kiểm tra có chest/barrel phía trên không
        Block aboveBlock = b.getRelative(BlockFace.UP);
        Inventory chestInventory = getChestInventory(aboveBlock);

        // Nếu có chest phía trên - ưu tiên đẩy vào chest
        if (chestInventory != null) {
            tickWithChest(b, chestInventory);
        } else if (menu != null) {
            // Không có chest - tạo item vào GUI (nếu có menu)
            tickWithoutChest(b, menu);
        }
        // Nếu không có cả chest và menu, không làm gì
    }

    /**
     * Tick khi có chest phía trên - đẩy item lên chest
     */
    private void tickWithChest(@Nonnull Block b, @Nonnull Inventory chestInventory) {
        // Kiểm tra chest còn chỗ không
        if (chestInventory.firstEmpty() == -1) {
            // Chest đầy, kiểm tra có thể stack không
            boolean canStack = false;
            for (ItemStack item : chestInventory.getContents()) {
                if (item != null && item.getType() == this.outputMaterial 
                        && item.getAmount() < item.getMaxStackSize()) {
                    canStack = true;
                    break;
                }
            }
            if (!canStack) {
                return; // Chest đầy hoàn toàn
            }
        }

        // Lấy và update progress
        int currentProgress = getProgress(b);

        if (currentProgress >= this.rate) {
            // Tạo item và đẩy vào chest
            ItemStack generatedItem = new ItemStack(this.outputMaterial);
            chestInventory.addItem(generatedItem);
            currentProgress = 0;
        } else {
            currentProgress++;
        }

        setProgress(b, currentProgress);
    }

    /**
     * Tick khi không có chest - tạo item vào GUI, dừng khi đủ 1 stack
     */
    private void tickWithoutChest(@Nonnull Block b, @Nonnull BlockMenu menu) {
        ItemStack outputItem = menu.getItemInSlot(OUTPUT_SLOTS[0]);

        // Kiểm tra đã đủ 1 stack chưa
        if (outputItem != null && outputItem.getAmount() >= outputItem.getMaxStackSize()) {
            return; // Đã đủ stack, dừng tạo
        }

        // Lấy và update progress
        int currentProgress = getProgress(b);

        if (currentProgress >= this.rate) {
            // Tạo item vào GUI
            if (outputItem == null) {
                menu.replaceExistingItem(OUTPUT_SLOTS[0], new ItemStack(this.outputMaterial));
            } else if (outputItem.getType() == this.outputMaterial) {
                outputItem.setAmount(outputItem.getAmount() + 1);
            }
            currentProgress = 0;
        } else {
            currentProgress++;
        }

        setProgress(b, currentProgress);
    }

    /**
     * Lấy inventory của chest/barrel nếu có
     */
    @javax.annotation.Nullable
    private Inventory getChestInventory(@Nonnull Block block) {
        Material type = block.getType();
        
        for (Material chestType : CHEST_TYPES) {
            if (type == chestType) {
                BlockState state = block.getState();
                if (state instanceof InventoryHolder) {
                    return ((InventoryHolder) state).getInventory();
                }
            }
        }
        
        return null;
    }

    /**
     * Lấy progress từ BlockStorage
     */
    private int getProgress(@Nonnull Block b) {
        String progressData = BlockStorage.getLocationInfo(b.getLocation(), "progress");
        if (progressData != null) {
            try {
                return Integer.parseInt(progressData);
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    /**
     * Lưu progress vào BlockStorage
     */
    private void setProgress(@Nonnull Block b, int progress) {
        BlockStorage.addBlockInfo(b, "progress", String.valueOf(progress));
    }

    /**
     * Set the material that this generator will produce
     */
    public final MaterialGenerator setItem(@Nonnull Material material) {
        this.outputMaterial = material;
        return this;
    }

    /**
     * Set the rate (in Slimefun ticks) at which this generator produces items
     */
    public final MaterialGenerator setRate(int rateTicks) {
        this.rate = Math.max(rateTicks, 1);
        return this;
    }
}
