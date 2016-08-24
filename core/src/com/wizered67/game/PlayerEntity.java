package com.wizered67.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import java.util.ArrayList;

public class PlayerEntity extends Entity{
	private String id;
	private GameScreen screen;
	private Direction direction = Direction.UP;
	private Direction previousDirection = Direction.UP;
	private Vector2 directionVector = new Vector2(0, 0);
	private Vector2 movementVector = new Vector2(0, 0);
	private float walkSpeed = 0.5f;
	private Fixture mainBody;
	public float defaultFriction = 0f;
	private Vector2 drawOffset = new Vector2(0, -1f);
	private int depth = 50;
	private boolean walking = false;
	private boolean destroyed = false;
	private int movementPreventionTimer = 0;
	private int attackCooldown = 0;
	private int previousDirectionTimer = 5;
	private ArrayList<Entity> taggedEntities = new ArrayList<Entity>();

	public PlayerEntity(String id, GameScreen screen){
		super();
		sprite = new Sprite(new Texture(Gdx.files.internal("Knight.png")));
		sprite.setOriginCenter();
		sprite.setScale(0.75f, 0.75f);
		isAnimated = false;
		this.id = id;
		this.screen = screen;
		addAnimations();
		makeBody();
		//persistant = true;
		EntityManager.setPlayer(this);
	}

	protected void addAnimations(){
		/*
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("Wheatley.pack"));
		Array<AtlasRegion> walk = atlas.findRegions("Walking-Frame");
		Array<AtlasRegion> airUp = atlas.findRegions("Up-Jump");
		Array<AtlasRegion> airDown = atlas.findRegions("Down-Jump");
		Array<AtlasRegion> idle = atlas.findRegions("Frame");
		Animation walkAnim = new Animation(0.1f, walk);
		Animation airUpAnim = new Animation(0.1f, airUp);
		Animation airDownAnim = new Animation(0.1f, airDown);
		Animation idleAnim = new Animation(0.1f, idle);
		animationProcessor.addAnimation(Animations.WALK, walkAnim);
		animationProcessor.addAnimation(Animations.JUMP, airUpAnim);
		animationProcessor.addAnimation(Animations.FALL, airDownAnim);
		animationProcessor.addAnimation(Animations.IDLE, idleAnim);
		animationProcessor.setAnimation(Animations.IDLE);
		inputs = new HashMap<Integer, InputInfo>();
		animationProcessor.updateSprite();
		*/
	}

	public void updateSprite(){
		super.updateSprite();
		sprite.setRotation((float)Math.toDegrees(direction.getBox2dDir()));
	}

	public void makeBody(){
		CircleShape circ = new CircleShape();
		PolygonShape rect = new PolygonShape();

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		//bodyDef.position.set(Constants.toMeters(350), Constants.toMeters(500));
		bodyDef.fixedRotation = true;
		body = WorldManager.world.createBody(bodyDef);
		body.setUserData(this);
		body.setBullet(true);


		float rectWidthHalf = Constants.toMeters((getWidth() - 9) / 2);
		float rectHeightHalf = Constants.toMeters((getHeight() - 4) / 2);
		rect.setAsBox(rectWidthHalf, rectHeightHalf, new Vector2(0, 0), 0);

		//circ.setPosition(new Vector2(0, 0));
		//circ.setRadius(Constants.toMeters(16));
		FixtureDef mb = new FixtureDef();
		mb.shape = rect;
		mb.density = 0.5f;
		mb.friction = defaultFriction;
		mb.restitution = 0;
		mb.filter.categoryBits = Constants.CATEGORY_PLAYER;
		mb.filter.maskBits = Constants.MASK_PLAYER;
		boundingWidth = rectWidthHalf * 2;
		boundingHeight = rectHeightHalf * 2;
		mainBody = body.createFixture(mb);
		//mainBody.setUserData(new FixtureData(Fixtures.BODY));


		/*
		rect.setAsBox(rectWidthHalf - Constants.toMeters(0.5f), Constants.toMeters(1), new Vector2(0, Constants.toMeters((-getHeight() + 2) / 2)), 0);
		FixtureDef foot = new FixtureDef();
		foot.shape = rect;
		foot.isSensor = true;
		foot.filter.categoryBits = Constants.CATEGORY_PLAYER;
		foot.filter.maskBits = Constants.MASK_PLAYER;
		Fixture footFixture = body.createFixture(foot);
		//footFixture.setUserData(new FixtureData(Fixtures.FOOT));
		*/
		circ.dispose();
		rect.dispose();
		super.makeBody();
	}

	public void updateTimers(){
		movementPreventionTimer = Math.max(0, movementPreventionTimer - 1);
		attackCooldown = Math.max(0, attackCooldown - 1);
		previousDirectionTimer = Math.max(0, previousDirectionTimer - 1);
	}

    @Override
    public void chooseAnimation() {
		/*
		if (walking){
			animationProcessor.setAnimation(Animations.WALK);
		}
		else{
			animationProcessor.setAnimation(Animations.IDLE);
		}
		*/
    }

    @Override
    public void destroy() {

    }

    public void updatePhysics(float delta){
		if (movementPreventionTimer > 0 || attackCooldown > 0){
			return;
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)){
			GameScreen gs = ((GameScreen) GameManager.game.getScreen());
			gs.setDebugRendererDrawInactive(!gs.getDebugRendererDrawInactive());
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
			GameScreen gs = ((GameScreen) GameManager.game.getScreen());
			if (gs.getMapName() != "grasslands1.tmx")
				gs.loadMap("grasslands1.tmx");
			else
				gs.loadMap("testmap.tmx");
		}

		boolean right = getInputPressed(Input.Keys.D, false);
		boolean left = getInputPressed(Input.Keys.A, false);
		boolean up = getInputPressed(Input.Keys.W, false);
		//boolean justUp = getInputPressed(Input.Keys.UP, true);
		boolean down = getInputPressed(Input.Keys.S, false);
		boolean justSpace = getInputPressed(Input.Keys.SPACE, true);
		//boolean attackKey = Gdx.input.justTouched();
		boolean attackKey = getInputPressed(Input.Keys.H, true);
		walking = false;
		int xDir = 0;
		int yDir = 0;
		if (right && !left){
			xDir = 1;
			if (getVelocity().x < 0){
				body.setLinearVelocity(0.0f, getVelocity().y);
			}
			body.applyLinearImpulse(walkSpeed * body.getMass(), 0, getX(), getY(), true);
			walking = true;
		}
		if (left && !right){
			xDir = -1;
			if (getVelocity().x > 0){
				body.setLinearVelocity(0.0f, getVelocity().y);
			}
			body.applyLinearImpulse(-walkSpeed * body.getMass(), 0, getX(), getY(), true);
			walking = true;
		}

        if (up && !down){
            yDir = 1;
            if (getVelocity().y < 0){
                body.setLinearVelocity(getVelocity().x, 0.0f);
            }
            body.applyLinearImpulse(0, walkSpeed * body.getMass(), getX(), getY(), true);
            walking = true;
        }
        if (down && !up){
            yDir = -1;
            if (getVelocity().y > 0){
                body.setLinearVelocity(getVelocity().x, 0.0f);
            }
            body.applyLinearImpulse(0, -walkSpeed * body.getMass(), getX(), getY(), true);
            walking = true;
        }

		if ((!right && !left) || (right && left)){
			xDir = 0;
			body.setLinearVelocity(0.0f, getVelocity().y);
            //walking = false;
		}

        if ((!up && !down) || (up && down)){
			yDir = 0;
            body.setLinearVelocity(getVelocity().x, 0.0f);
            //walking = false;
        }
		
		if (Math.abs(getVelocity().x) > Constants.MAX_VELOCITY){
			body.setLinearVelocity(Constants.MAX_VELOCITY * Math.signum(getVelocity().x), getVelocity().y);
		}

        if (Math.abs(getVelocity().y) > Constants.MAX_VELOCITY){
            body.setLinearVelocity(getVelocity().x, Constants.MAX_VELOCITY * Math.signum(getVelocity().y));
        }
		if (xDir != 0 || yDir != 0) {
			directionVector.set(xDir, yDir);
			if (xDir == 1){
				if (yDir == 1) direction = Direction.RIGHT_UP;
				else if (yDir == -1) direction = Direction.RIGHT_DOWN;
				else direction = Direction.RIGHT;
			}
			else if (xDir == -1){
				if (yDir == 1) direction = Direction.LEFT_UP;
				else if (yDir == -1) direction = Direction.LEFT_DOWN;
				else direction = Direction.LEFT;
			}
			else if (yDir == 1)
				direction = Direction.UP;
			else if (yDir == -1)
				direction = Direction.DOWN;
		}
		else {
			directionVector.set(previousDirection.getVector());
			direction = previousDirection;
		}
		movementVector.set(Math.signum(getVelocity().x), Math.signum(getVelocity().y));



		if (justSpace){
			teleport();
		}

		if (attackKey && attackCooldown <= 0){
			/*
			Vector3 unprojectedMouse = screen.unproject(Gdx.input.getX(), Gdx.input.getY());
			double radiansAngle = Math.atan2(unprojectedMouse.y - Constants.toPixels(getY()), unprojectedMouse.x - Constants.toPixels(getX()));
			double angleBetween = Math.toDegrees(radiansAngle);
			direction = angleToDirection(angleBetween);
			directionVector = direction.getVector();
			System.out.println(angleBetween);
			System.out.println(direction);
			System.out.println(direction.getVector());
			Vector3 unprojectedMouseMeters = Constants.toMeters(unprojectedMouse);
			double box2dAngle = radiansAngle - Math.PI / 2;//direction.getBox2dDir();
			Vector2 swordPosition = new Vector2(getX() + (float)Math.cos(Math.toRadians(angleBetween)) * boundingWidth / 2,
					getY() + (float)Math.sin(Math.toRadians(angleBetween)) * boundingHeight / 2);
			screen.addEntity(new SwordEntity(this, swordPosition.x, swordPosition.y, Constants.toMeters(8), Constants.toMeters(32), box2dAngle));//-Math.PI / 2 - Math.atan2(directionVector.y, directionVector.x)));
			attackCooldown = 14;
			float attackImpulse = walkSpeed * 3 * body.getMass();
			body.setLinearVelocity(0, 0);
			body.applyLinearImpulse(new Vector2((float)Math.cos(radiansAngle), (float)Math.sin(radiansAngle)).scl(attackImpulse), body.getPosition(), true);
		*/
			double box2dAngle = direction.getBox2dDir();
			double radiansAngle = box2dAngle + Math.PI / 2;
			double angleBetween = Math.toDegrees(radiansAngle);

			Vector2 swordPosition = new Vector2(getX() + (float)Math.cos(Math.toRadians(angleBetween)) * boundingWidth / 2,
					getY() + (float)Math.sin(Math.toRadians(angleBetween)) * boundingHeight / 2);
			EntityManager.addEntity(new SwordEntity(this, swordPosition.x, swordPosition.y, Constants.toMeters(8), Constants.toMeters(32), box2dAngle, direction));
			attackCooldown = 14;
			float attackImpulse = walkSpeed * 3 * body.getMass();
			body.setLinearVelocity(0, 0);
			body.applyLinearImpulse(new Vector2((float)Math.cos(radiansAngle), (float)Math.sin(radiansAngle)).scl(attackImpulse), body.getPosition(), true);
		}
		//System.out.println(direction);
		final ArrayList<Fixture> collided = new ArrayList<Fixture>();
		RayCastCallback raycastCallback = new RayCastCallback() {
			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
				collided.add(fixture);
				return 1;
			}
		};
		if (previousDirectionTimer <= 0) {
			previousDirection = direction;
			previousDirectionTimer = 5;
		}
		//Vector3 mouse = screen.unproject(Gdx.input.getX(), Gdx.input.getY());
		//WorldManager.world.rayCast(raycastCallback, new Vector2(getX(), getY() + 0.01f), new Vector2(Constants.toMeters(mouse.x), Constants.toMeters(mouse.y)));


		//for (Fixture f : collided)
			//System.out.println(f);

	}

	public Direction angleToDirection(double angle){
		if (angle >= -22.5 && angle < 22.5)
			return Direction.RIGHT;
		else if (angle >= 22.5 && angle < 67.5)
			return Direction.RIGHT_UP;
		else if (angle >= 67.5 && angle < 112.5)
			return Direction.UP;
		else if (angle >= 112.5 && angle < 157.5)
			return Direction.LEFT_UP;
		else if ((angle >= 157.5 && angle <= 180) || (angle <= -157.5 && angle >= -180 ))
			return Direction.LEFT;
		else if (angle <= -112.5 && angle > -157.5)
			return Direction.LEFT_DOWN;
		else if (angle <= -67.5 && angle > -112.5)
			return Direction.DOWN;
		else if (angle <= -22.5 && angle > -67.5)
			return Direction.RIGHT_DOWN;
		else
			return Direction.UP;
	}

	public void teleport(){
		final float teleportDistance = Constants.toMeters(96);
		//Vector2 p1 = body.getPosition().cpy().add()
		//Set first starting point to the side of the body corresponding to the center minus half the bounding shape and second to plus.
		// Points are perpendicular to raycast lines. Eg. If moving down points are to left and right.
		//
		Vector2 firstStartingPoint =
				body.getPosition().cpy().add(-boundingWidth / 2 * directionVector.y, -boundingHeight / 2 * directionVector.x);
		Vector2 secondStartingPoint =
				body.getPosition().cpy().add(boundingWidth / 2 * directionVector.y, boundingHeight / 2 * directionVector.x);
		//Set ending points to corresponding starting points plus the teleport distance in the direction of the direction vector
		Vector2 firstEndingPoint =
				firstStartingPoint.cpy().add(teleportDistance * directionVector.x, teleportDistance * directionVector.y);
		Vector2 secondEndingPoint =
				secondStartingPoint.cpy().add(teleportDistance * directionVector.x, teleportDistance * directionVector.y);
		System.out.println(firstStartingPoint + ", " + firstEndingPoint + "| " + secondStartingPoint + ", " + secondEndingPoint);
		//Tentatively set teleport location to current location with the teleport distance multiplied by the direction vector added
		final Vector2 teleportVector = new Vector2(directionVector.x * teleportDistance, directionVector.y * teleportDistance);
		teleportVector.add(directionVector.x * boundingWidth / 2, directionVector.y * boundingHeight / 2);
		RayCastCallback raycastCallback = new RayCastCallback() {
			float latestFraction = 100;
			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
				if (!fixture.isSensor() && fixture.getFilterData().categoryBits == Constants.CATEGORY_SCENERY){
					if (fraction < latestFraction) {
						teleportVector.set(directionVector.x * fraction * teleportDistance, directionVector.y * fraction * teleportDistance);
						latestFraction = fraction;
					}
				}
				return fraction;
			}
		};
		WorldManager.world.rayCast(raycastCallback, firstStartingPoint, firstEndingPoint);
		WorldManager.world.rayCast(raycastCallback, secondStartingPoint, secondEndingPoint);
		teleportVector.sub(directionVector.x * boundingWidth / 2, directionVector.y * boundingHeight / 2);
		teleportVector.sub(directionVector.x * Constants.toMeters(1), directionVector.y * Constants.toMeters(1));
		body.setTransform(body.getPosition().add(teleportVector), body.getAngle());
		body.setLinearVelocity(0, 0);
		movementPreventionTimer = 10;
	}
		
	public void beginContact(ContactData c){

	}
	
	public void endContact(ContactData c){
	}

    @Override
    public void preSolveCollision(ContactData c, Manifold m) {

		Vector2[] points = c.getContact().getWorldManifold().getPoints();

		final Fixture currentCollision = c.getOther();
		final Vector2 raycastCollision = new Vector2(0, 0);
		RayCastCallback raycastCallback = new RayCastCallback() {
			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
				if (!fixture.isSensor() && fixture.getFilterData().categoryBits == Constants.CATEGORY_SCENERY){
					raycastCollision.x = 0;
					raycastCollision.y = 0;
					//System.out.println(raycastCollision.y);
				}
				return 1;
			}
		};
		if (Math.abs(movementVector.x) != Math.abs(movementVector.y) && //Make sure only 4 directional movement
				(Math.signum(points[0].x - getX()) == movementVector.x || Math.signum(points[0].y - getY()) == movementVector.y ) //make sure moving closer to collision
				&& (c.getOther().getShape().getType() == Shape.Type.Circle || (movementVector.y != 0 && points[0].x >= getX() - boundingWidth / 2 && points[0].x <= getX() + boundingWidth / 2)
				|| (movementVector.x != 0 && points[0].y >= getY() - boundingHeight / 2 && points[0].y <= getY() + boundingHeight / 2)) //make sure colliding on correct side (unless circle)
				) {
			int[] signs = new int[]{1, -1};
			Vector2 p1 = new Vector2(0, 0);
			Vector2 p2 = new Vector2(0, 0);
			for (int sign : signs) {
				raycastCollision.x = Math.abs(movementVector.y) * sign;
				raycastCollision.y = Math.abs(movementVector.x) * sign;
				Vector2 p = body.getPosition().cpy().add(boundingWidth / 2 * -raycastCollision.x, boundingHeight / 2 * -raycastCollision.y);
				p1 = p.cpy().add(new Vector2(boundingWidth / 3 * raycastCollision.x, boundingHeight / 3 * raycastCollision.y));
				p2 = p1.cpy().add(new Vector2(boundingWidth * movementVector.x, boundingHeight * movementVector.y));
				//System.out.println(p1 + ", " + p2);
				WorldManager.world.rayCast(raycastCallback, p1, p2);
				screen.drawLine(Constants.toPixels(p1.x), Constants.toPixels(p1.y), Constants.toPixels(p2.x), Constants.toPixels(p2.y));
				//System.out.println(raycastCollision.y);
				if (raycastCollision.x == 0 && raycastCollision.y == 0)
					break;
			}

			if (raycastCollision.x != 0 || raycastCollision.y != 0){
				int xSign = 1;
				int ySign = 1;
				if (p1.x <= points[0].x && p2.x <= points[0].x)
					xSign = -1;
				else
					xSign = 1;
				if (p1.y <= points[0].y && p2.y <= points[0].y)
					ySign = -1;
				else
					ySign = 1;
				float xImpulse = Math.abs(raycastCollision.x) * xSign * walkSpeed * 4 * body.getMass();
				float yImpulse = Math.abs(raycastCollision.y) * ySign * walkSpeed * 4 * body.getMass();
				System.out.println("Applying impulse of: " + xImpulse + ", " + yImpulse);
				body.applyLinearImpulse(xImpulse, yImpulse, getX(), getY(), true);
			}


		}
    }

    @Override
    public void postSolveCollision(ContactData c, ContactImpulse impulse) {

    }

	public ArrayList<Entity> getTaggedEntities() {
		return taggedEntities;
	}

	public void tagEntity(Entity e) {
		if (!taggedEntities.contains(e)){
			taggedEntities.add(e);
		}
	}

}
