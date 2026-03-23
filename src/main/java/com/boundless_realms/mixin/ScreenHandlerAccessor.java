package com.boundless_realms.mixin;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ScreenHandler.class)
public interface ScreenHandlerAccessor {
    @Accessor("slots")
    DefaultedList<Slot> boundlessRealms$getSlots();

    @Invoker("addSlot")
    Slot boundlessRealms$invokeAddSlot(Slot slot);
}
