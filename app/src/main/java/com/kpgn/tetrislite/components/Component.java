package com.kpgn.tetrislite.components;


import com.kpgn.tetrislite.activity.GameActivity;

public abstract class Component {

    protected GameActivity host;

    public Component(GameActivity ga) {
        host = ga;
    }

    public void reconnect(GameActivity ga) {
        host = ga;
    }

    public void disconnect() {
        host = null;
    }

}
