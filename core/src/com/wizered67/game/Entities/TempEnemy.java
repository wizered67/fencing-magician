package com.wizered67.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.wizered67.game.Collisions.ContactData;
import com.wizered67.game.Constants;
import com.wizered67.game.EntityManager;
import com.wizered67.game.Enums.Direction;
import com.wizered67.game.WorldManager;

/**
 * Created by Adam on 8/13/2016.
 */
public class TempEnemy extends EnemyEntity {

    private final int DIRECTION_CHANGE_MAX = 90;
    private float x, y, width, height, mapWidth, mapHeight;
    private int directionChangeTimer = DIRECTION_CHANGE_MAX;
    private Direction currentDirection = Direction.UP;

    public TempEnemy(float x, float y, float width, float height, float mapWidth, float mapHeight){
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        WorldManager.addNewObjectBody(this);
        health = 3;
        persistant = true;
        sprite = new Sprite(new Texture(Gdx.files.internal("batman.png")));
        sprite.setOriginCenter();
        sprite.setScale(1f, 1f);
        isAnimated = false;
    }

    public void makeBody(){
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x, y);
        bdef.fixedRotation = true;
        body = WorldManager.world.createBody(bdef);
        body.setUserData(this);
        PolygonShape rect = new PolygonShape();
        rect.setAsBox(width / 2, height / 2, new Vector2(0,0), 0);
        FixtureDef mb = new FixtureDef();
        mb.isSensor = false;
        mb.shape = rect;
        mb.density = 0.5f;
        mb.friction = 0f;
        mb.restitution = 0;
        mb.filter.categoryBits = Constants.CATEGORY_ENEMY;
        mb.filter.maskBits = Constants.MASK_ENEMY;
        body.createFixture(mb);
        super.makeBody();
    }

    public void damage(int amount){
        super.damage(amount);
        if (health <= 0)
            EntityManager.removeEntity(this);
    }

    @Override
    public void beginContact(ContactData c) {
        if (c.getOther().getFilterData().categoryBits == Constants.CATEGORY_SCENERY){
            //changeDirection();
            currentDirection = Direction.getDirectionFromVector(currentDirection.getVector().scl(-1));
            Vector2 directionVector = currentDirection.getVector();
            body.setLinearVelocity(3.5f * directionVector.x, 3.5f * directionVector.y);
        }
    }

    @Override
    public void endContact(ContactData c) {

    }

    @Override
    public void preSolveCollision(ContactData c, Manifold m) {

    }

    @Override
    public void postSolveCollision(ContactData c, ContactImpulse impulse) {

    }

    public void changeDirection(){
        Direction[] dirs = Direction.values();
        currentDirection = dirs[MathUtils.random(dirs.length - 1)];
    }

    @Override
    public void updatePhysics(float delta) {
        if (stunned)
            return;
        body.setLinearDamping(0);
        if (directionChangeTimer <= 0){
            directionChangeTimer = DIRECTION_CHANGE_MAX;
            changeDirection();
        }
        if (getX() < 0 || getX() > mapWidth || getY() < 0 || getY() > mapHeight){
            currentDirection = Direction.getDirectionFromVector(currentDirection.getVector().scl(-1));
        }
        Vector2 directionVector = currentDirection.getVector();
        float speed = 0;
        body.setLinearVelocity(speed * directionVector.x, speed * directionVector.y);
    }

    @Override
    public void updateTimers() {
        super.updateTimers();
        directionChangeTimer = Math.max(0, directionChangeTimer - 1);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
