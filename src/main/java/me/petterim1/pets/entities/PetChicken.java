package me.petterim1.pets.entities;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import me.petterim1.pets.EntityPet;
import me.petterim1.pets.Main;
import me.petterim1.pets.Utils;

public class PetChicken extends EntityPet {

    public PetChicken(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return 10;
    }

    @Override
    public float getWidth() {
        return 0.4f;
    }

    @Override
    public float getHeight() {
        return 0.7f;
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        switch (player.getInventory().getItemInHand().getId()) {
            case Item.SEEDS:
            case Item.PUMPKIN_SEEDS:
            case Item.MELON_SEEDS:
            case Item.BEETROOT_SEEDS:
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                this.level.addParticle(new ItemBreakParticle(
                        this.add(Utils.rand(-0.5, 0.5), this.getMountedYOffset(), Utils.rand(-0.5, 0.5)),
                        player.getInventory().getItemInHand()));

                this.inLoveTicks = 10;
                this.setDataFlag(DATA_FLAGS, DATA_FLAG_INLOVE);
                player.addExperience(Main.getInstance().getPluginConfig().getInt("feedXp"));
                return true;
            default:
                return false;
        }
    }
}