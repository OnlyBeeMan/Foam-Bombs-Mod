package net.beeman.foambombs.mixin;

import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SulfurCube.class)
public interface SulfurCubeInvoker {
    @Invoker("setFuse")
    void callSetFuse(int fuse);

    @Invoker("primeTime")
    boolean callPrimeTime(boolean flag);
}
