package com.boundless_realms.entity;

import com.boundless_realms.BoundlessRealmsMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ModEntities {
    private static final Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer.Builder> ATTRIBUTE_REGISTRY = new HashMap<>();

    public static final EntityType<LunchTicketEntity> LUNCH_TICKET = registerEntity(
            entityId("lunch_ticket"),
            entityKey(entityId("lunch_ticket")),
            EntityType.Builder.<LunchTicketEntity>create(LunchTicketEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25F, 0.25F)
    );

    public static final EntityType<TicketInspectorEntity> TICKET_INSPECTOR = registerLivingEntity(
            "ticket_inspector",
            EntityType.Builder.<TicketInspectorEntity>create(TicketInspectorEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.6F, 1.95F),
            TicketInspectorEntity.createAttributes()
    );

    public static void registerModEntities() {
        ATTRIBUTE_REGISTRY.forEach(FabricDefaultAttributeRegistry::register);
    }

    private static <T extends LivingEntity> EntityType<T> registerLivingEntity(String name, EntityType.Builder<T> builder, DefaultAttributeContainer.Builder attributes) {
        Identifier id = entityId(name);
        EntityType<T> type = registerEntity(id, entityKey(id), builder);
        ATTRIBUTE_REGISTRY.put(type, attributes);
        return type;
    }

    private static Identifier entityId(String name) {
        return Identifier.of(BoundlessRealmsMod.MOD_ID, name);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> RegistryKey<EntityType<T>> entityKey(Identifier id) {
        return (RegistryKey<EntityType<T>>) (RegistryKey<?>) RegistryKey.of(RegistryKeys.ENTITY_TYPE, id);
    }

    private static <T extends Entity> EntityType<T> registerEntity(Identifier id, RegistryKey<EntityType<T>> key, EntityType.Builder<T> builder) {
        return Registry.register(Registries.ENTITY_TYPE, id, builder.build(asWildcardKey(key)));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> RegistryKey<EntityType<?>> asWildcardKey(RegistryKey<EntityType<T>> key) {
        return (RegistryKey<EntityType<?>>) (RegistryKey<?>) key;
    }
}