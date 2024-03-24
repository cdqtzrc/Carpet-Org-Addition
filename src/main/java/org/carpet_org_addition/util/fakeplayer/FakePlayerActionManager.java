package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;

public class FakePlayerActionManager {
    public static final String PLAYER_DATA = "player_data";
    private final EntityPlayerMPFake fakePlayer;
    private FakePlayerAction action = FakePlayerAction.STOP;

    public FakePlayerActionManager(EntityPlayerMPFake fakePlayer) {
        this.fakePlayer = fakePlayer;
    }

    public void executeAction() {
    }
}
