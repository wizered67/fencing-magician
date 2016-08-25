package com.wizered67.game.Entities;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.wizered67.game.Collisions.ContactData;
import com.wizered67.game.Constants;
import com.wizered67.game.EntityManager;
import com.wizered67.game.Enums.Direction;
import com.wizered67.game.GameManager;
import com.wizered67.game.Screens.GameScreen;
import com.wizered67.game.WorldManager;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Adam on 8/24/2016.
 */
public class HomingProjectileEntity extends Entity{
    private float x, y, radius;
    private double angle;
    private double targetAngle;
    private int deathTimer;
    private Entity creator;
    private Direction direction;
    private Comparator<Entity> distanceComparator = new Comparator<Entity>() {
        @Override
        public int compare(Entity e1, Entity e2) {
            double distance1 = Math.pow(e1.getX() - getX(), 2) + Math.pow(e1.getY() - getY(), 2);
            double distance2 = Math.pow(e2.getX() - getX(), 2) + Math.pow(e2.getY() - getY(), 2);
            return (int) (distance1 - distance2);
        }
    };

    public HomingProjectileEntity(Entity creator, float x, float y, float radius, double angle, Direction direction){
        this.creator = creator;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.angle = angle;
        this.targetAngle = angle;
        this.direction = direction;
        deathTimer = 0;
        WorldManager.addNewObjectBody(this);
    }

    public void makeBody(){
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x, y);
        bdef.angle = (float) angle;
        bdef.bullet = true;
        bdef.fixedRotation = true;
        body = WorldManager.world.createBody(bdef);
        body.setUserData(this);

        CircleShape circle = new CircleShape();
        circle.setPosition(new Vector2(0, 0));
        circle.setRadius(radius);
        FixtureDef mb = new FixtureDef();
        mb.isSensor = true;
        mb.shape = circle;
        mb.density = 0.5f;
        mb.friction = 0f;
        mb.restitution = 0;
        mb.filter.categoryBits = Constants.CATEGORY_PLAYER_ATTACK;
        mb.filter.maskBits = Constants.MASK_PLAYER_ATTACK;
        body.createFixture(mb);
        super.makeBody();
    }

    @Override
    public void beginContact(ContactData c) {
        if (c.getOther().getFilterData().categoryBits == Constants.CATEGORY_SCENERY) {
            EntityManager.removeEntity(this);
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

    @Override
    public void updatePhysics(float delta) {
        Screen currentScreen = GameManager.game.getScreen();
        if (currentScreen instanceof GameScreen){
            GameScreen screen = (GameScreen) currentScreen;
            if (!screen.inWorld(Constants.toPixels(body.getPosition())))
                EntityManager.removeEntity(this);
        }

        homeOnTagged();
        if (targetAngle != angle){
            angle = MathUtils.lerpAngle((float)angle, (float)targetAngle, 0.15f);
        }
        body.setTransform(body.getPosition(), (float) angle);
        float speed = 6f;
        body.setLinearVelocity((float)Math.cos(Constants.toBox2DAngle(angle)) * speed, (float)Math.sin(Constants.toBox2DAngle(angle)) * speed);
    }

    private void homeOnTagged(){
        if (creator instanceof PlayerEntity){
            final PlayerEntity player = (PlayerEntity) creator;
            final ArrayList<Entity> nearby = new ArrayList<Entity>();
            QueryCallback callback = new QueryCallback() {
                @Override
                public boolean reportFixture(Fixture fixture) {
                    Object other = fixture.getBody().getUserData();
                    if (other instanceof EnemyEntity){
                        if (player.getTaggedEntities().contains(other))
                            nearby.add((Entity) other);
                    }
                    return true;
                }
            };
            float width = Constants.toMeters(96);
            float height = Constants.toMeters(96);
            WorldManager.world.QueryAABB(callback, getX() - width / 2, getY() - height / 2, getX() + width / 2, getY() + height / 2);
            nearby.sort(distanceComparator);
            if (!nearby.isEmpty()){
                Entity closest = nearby.get(0);
                targetAngle = Constants.toBox2DAngle(Math.atan2(getY() - closest.getY(), getX() - closest.getX()));
            }
        }
    }

    @Override
    public void updateTimers() {
        deathTimer = Math.max(deathTimer - 1, 0);
    }

    @Override
    public void destroy() {

    }
}
