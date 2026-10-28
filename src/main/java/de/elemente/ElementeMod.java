package de.elemente;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.core.registries.Registries;

@Mod(ElementeMod.ID)
public class ElementeMod {
    public static final String ID = "elemente";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ID);
    public static final RegistryObject<Item> WATER = ITEMS.register("wasser_fokus", () -> new ElementItem(Element.WATER));
    public static final RegistryObject<Item> EARTH = ITEMS.register("erde_fokus", () -> new ElementItem(Element.EARTH));
    public static final RegistryObject<Item> FIRE = ITEMS.register("feuer_fokus", () -> new ElementItem(Element.FIRE));
    public static final RegistryObject<Item> AIR = ITEMS.register("luft_fokus", () -> new ElementItem(Element.AIR));
    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("elemente", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.elemente"))
        .icon(() -> new ItemStack(AIR.get()))
        .displayItems((parameters, output) -> { output.accept(WATER.get()); output.accept(EARTH.get()); output.accept(FIRE.get()); output.accept(AIR.get()); })
        .build());

    public ElementeMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        TABS.register(bus);
    }

    private enum Element { WATER, EARTH, FIRE, AIR }

    private static class ElementItem extends Item {
        private final Element element;
        ElementItem(Element element) { super(new Item.Properties().stacksTo(1)); this.element = element; }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            ItemStack stack = player.getItemInHand(hand);
            if (!level.isClientSide && level instanceof ServerLevel server) {
                if (player.isShiftKeyDown()) {
                    special(server, player);
                    player.getCooldowns().addCooldown(this, 160);
                } else {
                    cast(server, player);
                    player.getCooldowns().addCooldown(this, 30);
                }
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        private ParticleOptions particle() {
            return switch (element) {
                case WATER -> ParticleTypes.SPLASH;
                case EARTH -> ParticleTypes.CRIT;
                case FIRE -> ParticleTypes.FLAME;
                case AIR -> ParticleTypes.CLOUD;
            };
        }

        private void cast(ServerLevel level, Player player) {
            Vec3 start = player.getEyePosition();
            Vec3 direction = player.getLookAngle().normalize();
            for (double d = 0.6; d <= 12.0; d += 0.35) {
                Vec3 pos = start.add(direction.scale(d));
                BlockPos block = BlockPos.containing(pos);
                if (!level.getBlockState(block).getCollisionShape(level, block).isEmpty()) break;
                level.sendParticles(particle(), pos.x, pos.y, pos.z, 2, 0.08, 0.08, 0.08, 0.01);
                AABB area = new AABB(pos.x - 0.55, pos.y - 0.55, pos.z - 0.55, pos.x + 0.55, pos.y + 0.55, pos.z + 0.55);
                var targets = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player && e.isAlive() && !e.isSpectator());
                if (!targets.isEmpty()) {
                    LivingEntity target = targets.get(0);
                    switch (element) {
                        case WATER -> { target.hurt(level.damageSources().playerAttack(player), 4.0F); target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1)); }
                        case EARTH -> { target.hurt(level.damageSources().playerAttack(player), 6.0F); target.knockback(0.9, -direction.x, -direction.z); }
                        case FIRE -> { if (target.hurt(level.damageSources().playerAttack(player), 5.0F)) target.setSecondsOnFire(3); }
                        case AIR -> { target.hurt(level.damageSources().playerAttack(player), 2.0F); target.push(direction.x * 1.4, 0.35, direction.z * 1.4); target.hurtMarked = true; }
                    }
                    break;
                }
            }
            level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.7F, element == Element.FIRE ? 0.7F : 1.2F);
        }

        private void special(ServerLevel level, Player player) {
            switch (element) {
                case WATER -> { player.heal(4.0F); player.clearFire(); }
                case EARTH -> player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 1));
                case FIRE -> player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0));
                case AIR -> { Vec3 dir = player.getLookAngle(); player.push(dir.x * 0.8, 0.8, dir.z * 0.8); player.hurtMarked = true; player.fallDistance = 0; player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0)); }
            }
            level.sendParticles(particle(), player.getX(), player.getY() + 1, player.getZ(), 30, 0.5, 0.7, 0.5, 0.04);
            level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.8F, 1.0F);
        }
    }
}
