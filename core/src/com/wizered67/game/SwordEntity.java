package com.wizered67.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

/**
 * Created by Adam on 8/12/2016.
 */
public class SwordEntity extends Entity {

    private float x, y, width, height;
    private double angle;
    private int deathTimer;
    private Entity creator;
    private Direction direction;
    private float knockback = 7f;

    public SwordEntity(Entity creator, float x, float y, float width, float height, double angle, Direction dir){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.creator = creator;
        this.direction = dir;
        deathTimer = 12;
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

        PolygonShape rect = new PolygonShape();
        rect.setAsBox(width / 2, height / 2, new Vector2(0, height / 2), 0);
        FixtureDef mb = new FixtureDef();
        mb.isSensor = true;
        mb.shape = rect;
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
        Entity otherEntity = (Entity) c.getOther().getBody().getUserData();
        if (otherEntity == null)
            return;
        if (otherEntity instanceof EnemyEntity){
            EnemyEntity enemy = (EnemyEntity) otherEntity;
            if (enemy.canBeHit()){
                float impulseAmount = enemy.getBody().getMass() * knockback;
                enemy.getBody().setLinearVelocity(0, 0);
                enemy.getBody().applyLinearImpulse(direction.getVector().scl(impulseAmount), enemy.getBody().getPosition(), true);
                enemy.setStun(60);
                enemy.damage(1);
                enemy.getBody().setLinearDamping(1f);
                if (creator instanceof PlayerEntity){
                    ((PlayerEntity) creator).tagEntity(enemy);
                }
            }
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
        double angleBetween = angle + Math.PI / 2;
        Vector2 swordPosition = new Vector2(creator.getBody().getPosition().x + (float)Math.cos(angleBetween) * creator.getBoundingWidth() / 2,
                creator.getBody().getPosition().y + (float)Math.sin(angleBetween) * creator.getBoundingHeight() / 2);
        body.setTransform(swordPosition, body.getAngle());
        System.out.println(swordPosition);
        System.out.println(body.getPosition());
    }

    @Override
    public void updateTimers() {
        deathTimer = Math.max(0, deathTimer - 1);
        if (deathTimer <= 0){
            EntityManager.removeEntity(this);
        }
    }

    @Override
    public void destroy() {

    }
}
