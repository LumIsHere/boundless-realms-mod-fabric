package com.boundless_realms.entity;

import com.boundless_realms.BoundlessRealmsMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import java.util.HashMap;
import java.util.Map;

public class ModEntities {
    private static final Map<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> ATTRIBUTE_REGISTRY = new HashMap<>();

    public static final EntityType<LunchTicketEntity> LUNCH_TICKET = registerEntity(
            entityId("lunch_ticket"),
            entityKey(entityId("lunch_ticket")),
            EntityType.Builder.<LunchTicketEntity>of(LunchTicketEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
    );

    public static final EntityType<TicketInspectorEntity> TICKET_INSPECTOR = registerLivingEntity(
            "ticket_inspector",
            EntityType.Builder.<TicketInspectorEntity>of(TicketInspectorEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F),
            TicketInspectorEntity.createAttributes()
    );

    public static void registerModEntities() {
        ATTRIBUTE_REGISTRY.forEach(FabricDefaultAttributeRegistry::register);
    }

    private static <T extends LivingEntity> EntityType<T> registerLivingEntity(String name, EntityType.Builder<T> builder, AttributeSupplier.Builder attributes) {
        Identifier id = entityId(name);
        EntityType<T> type = registerEntity(id, entityKey(id), builder);
        ATTRIBUTE_REGISTRY.put(type, attributes);
        return type;
    }

    private static Identifier entityId(String name) {
        return Identifier.fromNamespaceAndPath(BoundlessRealmsMod.MOD_ID, name);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> ResourceKey<EntityType<T>> entityKey(Identifier id) {
        return (ResourceKey<EntityType<T>>) (ResourceKey<?>) ResourceKey.create(Registries.ENTITY_TYPE, id);
    }

    private static <T extends Entity> EntityType<T> registerEntity(Identifier id, ResourceKey<EntityType<T>> key, EntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, builder.build(asWildcardKey(key)));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> ResourceKey<EntityType<?>> asWildcardKey(ResourceKey<EntityType<T>> key) {
        return (ResourceKey<EntityType<?>>) (ResourceKey<?>) key;
    }
}