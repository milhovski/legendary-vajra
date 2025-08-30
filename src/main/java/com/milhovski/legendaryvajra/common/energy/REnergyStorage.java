package com.milhovski.legendaryvajra.common.energy;

import com.milhovski.legendaryvajra.init.CDataComponents;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

public record REnergyStorage(ItemStack stack, int capacity, int maxReceive,
                             int maxExtract) implements IEnergyStorage {

    private int getStored() {
        return stack.getOrDefault(CDataComponents.ENERGY.get(), 0);
    }

    private void setStored(int energy) {
        stack.set(CDataComponents.ENERGY.get(), Mth.clamp(energy, 0, capacity));
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) return 0;
        int stored = getStored();
        int received = Math.min(capacity - stored, Math.min(this.maxReceive, maxReceive));
        if (!simulate && received > 0) setStored(stored + received);
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;
        int stored = getStored();
        int extracted = Math.min(stored, Math.min(this.maxExtract, maxExtract));
        if (!simulate && extracted > 0) setStored(stored - extracted);
        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return getStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }

}
