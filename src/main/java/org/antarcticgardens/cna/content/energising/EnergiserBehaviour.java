package org.antarcticgardens.cna.content.energising;

import com.zurrtum.create.content.kinetics.belt.BeltHelper;
import com.zurrtum.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.zurrtum.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.zurrtum.create.content.kinetics.belt.transport.TransportedItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.antarcticgardens.cna.CNARecipeTypes;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;

import java.util.List;

/**
 * Holds items under an energiser until enough energy went into them, then converts them by recipe.
 * Sequenced assembly steps are found the same way, as Create Fly registers each step as a recipe.
 */
public class EnergiserBehaviour extends BeltProcessingBehaviour {
    protected int tier;
    protected EnergiserBlockEntity be;

    public EnergisingRecipe currentRecipe;
    public long charged;
    public long needed;
    public long sinceUpdate = 0;

    private boolean shouldCreateParticles = false;

    public EnergiserBehaviour(EnergiserBlockEntity be) {
        super(be);
        whenItemEnters(this::itemEnter);
        whileItemHeld(this::itemHeld);
        this.be = be;
    }

    public EnergisingRecipe getRecipe(ItemStack stack) {
        if (!(be.getLevel() instanceof ServerLevel level))
            return null;
        return level.recipeAccess()
                .getRecipeFor(CNARecipeTypes.ENERGISING.<SingleRecipeInput, EnergisingRecipe>getType(), new SingleRecipeInput(stack), level)
                .map(RecipeHolder::value)
                .orElse(null);
    }

    @Override
    public void read(ValueInput nbt, boolean clientPacket) {
        charged = nbt.getLongOr("charged", 0);
        needed = nbt.getLongOr("needed", 0);
        shouldCreateParticles = nbt.getBooleanOr("shouldCreateParticles", false);
        super.read(nbt, clientPacket);
    }

    @Override
    public void write(ValueOutput nbt, boolean clientPacket) {
        nbt.putLong("charged", charged);
        nbt.putLong("needed", needed);
        nbt.putBoolean("shouldCreateParticles", shouldCreateParticles);
        if (clientPacket)
            shouldCreateParticles = false;
        super.write(nbt, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();
        if (be.getLevel() == null)
            return;

        if (!be.getLevel().isClientSide() && needed > 0) {
            sinceUpdate--;
            if (sinceUpdate <= 0) {
                needed = 0;
                be.getEnergyStorage().internalInsert(charged, false);
                charged = 0;
                currentRecipe = null;
                blockEntity.sendData();
            }
        }

        be.lastCharged = -1;
        if (needed > 0) {
            be.lastCharged = be.getEnergyStorage().internalExtract(Math.min(eSpeed(), needed - charged), false);
            charged += be.lastCharged;
        }

        if (be.getLevel().isClientSide()) {
            be.size -= 0.15f;
            be.size = Math.clamp(be.size, 0, 1);
            if (needed > 0 && charged > 0) {
                be.size = net.minecraft.util.Mth.lerp(0.6f, be.size, (float) charged / needed);
            } else if (shouldCreateParticles) {
                shouldCreateParticles = false;
                var rand = be.getLevel().getRandom();
                for (int i = 0; i < 6; i++) {
                    be.getLevel().addParticle(ParticleTypes.GLOW,
                            be.getBlockPos().getX() + 0.5 + (rand.nextFloat() - 0.5) * 0.4,
                            be.getBlockPos().getY() - 1.4 + (rand.nextFloat() - 0.5) * 0.4,
                            be.getBlockPos().getZ() + 0.5 + (rand.nextFloat() - 0.5) * 0.4,
                            0.0, 0.5, 0.0);
                }
                be.getLevel().playLocalSound(be.getBlockPos().getX() + 0.5,
                        be.getBlockPos().getY() - 1.4,
                        be.getBlockPos().getZ() + 0.5,
                        SoundEvents.ENDER_EYE_DEATH, SoundSource.BLOCKS, 0.3f, 2.0f, true);
            }
        }
    }

    private ProcessingResult itemHeld(TransportedItemStack transportedItemStack, TransportedItemStackHandlerBehaviour handler) {
        if (currentRecipe == null)
            currentRecipe = getRecipe(transportedItemStack.stack);
        if (be.getSpeed() == 0 || currentRecipe == null || be.getLevel() == null)
            return ProcessingResult.PASS;

        int count = transportedItemStack.stack.getCount();
        needed = (long) count * currentRecipe.getEnergyNeeded();
        sinceUpdate = 10;

        if (charged >= needed) {
            List<TransportedItemStack> out = currentRecipe.assemble(new SingleRecipeInput(transportedItemStack.stack), be.getLevel().getRandom())
                    .stream()
                    .map(stack -> {
                        TransportedItemStack copy = transportedItemStack.copy();
                        boolean centered = BeltHelper.isItemUpright(stack);
                        stack.setCount(stack.getCount() * count);
                        copy.stack = stack;
                        copy.angle = centered ? 180 : be.getLevel().getRandom().nextInt(360);
                        return copy;
                    })
                    .toList();

            if (charged > needed)
                be.getEnergyStorage().internalInsert(charged - needed, false);

            charged = 0;
            needed = 0;

            if (out.isEmpty()) {
                handler.handleProcessingOnItem(transportedItemStack, TransportedItemStackHandlerBehaviour.TransportedResult.removeItem());
            } else {
                handler.handleProcessingOnItem(transportedItemStack, TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(out));
            }
            shouldCreateParticles = true;
            blockEntity.sendData();
            currentRecipe = null;
        }

        return ProcessingResult.HOLD;
    }

    private long eSpeed() {
        return EnergiserBlock.getStrength(be.tier) * (long) Math.abs(be.getSpeed() * 0.1);
    }

    private ProcessingResult itemEnter(TransportedItemStack transportedItemStack, TransportedItemStackHandlerBehaviour handler) {
        if (be.getSpeed() == 0)
            return ProcessingResult.PASS;

        currentRecipe = getRecipe(transportedItemStack.stack);
        sinceUpdate = 10;
        if (currentRecipe == null)
            return ProcessingResult.PASS;

        return ProcessingResult.HOLD;
    }
}
