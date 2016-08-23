package com.wizered67.game;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;

/**
 * Created by Adam on 8/3/2016.
 */
public class CustomQueryCallback implements QueryCallback {
    private Fixture other;
    private boolean found = false;
    public CustomQueryCallback(Fixture other){
        this.other = other;
    }

    @Override
    public boolean reportFixture(Fixture fixture) {

        if (fixture == other) {
            System.out.println("AABB Collision with " + fixture.toString());
            found = true;
            return false;
        }
        return true;
    }

    public boolean wasFound(){
        return found;
    }
}
