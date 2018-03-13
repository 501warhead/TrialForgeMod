package io.github.warhead.craftingdeadtrial.energy;

/**
 * Represents a specific action that, when done by a player, consumes a certain amount of energy.
 *
 * @author 501warhead
 * @see EnergyHandler
 */
public enum EnergyActionType {
    /**
     * When the player mines ore
     */
    MINE_ORE(3),
    /**
     * When the player chops wood
     */
    CHOP_WOOD(1),
    /**
     * When the player punches the punching bag
     */
    PUNCH_BAG(10);

    /**
     * The energy used by this action
     */
    public final int energyUsed;

    EnergyActionType(int energyUsed) {
        this.energyUsed = energyUsed;
    }
}
