package com.example.testgame;

import java.util.ArrayList;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXObjectGroup;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.Constants;
import org.andengine.util.debug.Debug;

import android.graphics.Typeface;
import android.opengl.GLES20;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Testgame extends SimpleBaseGameActivity {

	// ===========================================================
	// Constants
	// ===========================================================

	private int CAMERA_WIDTH;
	private int CAMERA_HEIGHT;
	private Scene scene;
	private HUD hud;
	private ButtonSprite aButton;
	private ButtonSprite bButton;
	private Text bt1txt, bt2txt;
	private String nowTalkstr = "xx";
	private ArrayList<Upc> UPChandle;
	private boolean gettalk = false;
	// ===========================================================
	// Fields
	// ===========================================================

	private BoundCamera mBoundChaseCamera;

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mPlayerTextureRegion;
	private BitmapTextureAtlas aButtonTextureAtlas;
	private TiledTextureRegion aButtonTextureRegion;
	private BitmapTextureAtlas bButtonTextureAtlas;
	private TiledTextureRegion bButtonTextureRegion;
	private TMXTiledMap mTMXTiledMap;
	protected int mCactusCount;
	private Font mFont;

	private BitmapTextureAtlas mOnScreenControlTexture;
	private ITextureRegion mOnScreenControlBaseTextureRegion;
	private ITextureRegion mOnScreenControlKnobTextureRegion;

	private DigitalOnScreenControl mDigitalOnScreenControl;

	private ITMXTilePropertiesListener TilePropertiesListener;

	private int accel = 4;

	private final static long SPEED = 200;

	private Body mPlayerBody;
	private int direction = 0;

	private PhysicsWorld mPhysicsWorld;

	private float centerX, centerY;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {

	
		CAMERA_WIDTH = 800;
		CAMERA_HEIGHT = 480;

		this.mBoundChaseCamera = new BoundCamera(0, 0, CAMERA_WIDTH,
				CAMERA_HEIGHT);

		final EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), this.mBoundChaseCamera);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);

		if (!MultiTouch.isSupported(this)) {
			Toast.makeText(this,
					"Sorry your device does NOT support MultiTouch!.",
					Toast.LENGTH_LONG).show();
		}

		return engineOptions;
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 96, 192, TextureOptions.DEFAULT);
		this.mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mBitmapTextureAtlas, this,
						"maidchar01.png", 0, 0, 3, 4);
		this.mBitmapTextureAtlas.load();

		this.aButtonTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 64, TextureOptions.DEFAULT);
		this.aButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.aButtonTextureAtlas, this,
						"face_circle_tiled.png", 0, 0, 2, 1);
		this.aButtonTextureAtlas.load();

		this.bButtonTextureAtlas = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 64, TextureOptions.DEFAULT);
		this.bButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.bButtonTextureAtlas, this,
						"face_circle_tiled_b.png", 0, 0, 2, 1);
		this.bButtonTextureAtlas.load();

		this.mFont = FontFactory.create(this.getFontManager(),
				this.getTextureManager(), 256, 256, TextureOptions.BILINEAR,
				Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 40);
		this.mFont.load();

		this.mOnScreenControlTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlTexture.load();

	}

	@Override
	public Scene onCreateScene() {

		this.mEngine.registerUpdateHandler(new FPSLogger());
		bt1txt = new Text(0, 20, this.mFont, "A button : none",
				"A button : xxxxxxxxxxxxxxx ".length(),
				this.getVertexBufferObjectManager());
		bt2txt = new Text(0, 65, this.mFont, "B button : none",
				"B button : xxxxxxxxxxxxxxx ".length(),
				this.getVertexBufferObjectManager());
		final Text fpsTextY = new Text(0, 110, this.mFont,
				"pvalueY : xxxxxxxxxxxxxxx ",
				"pvalueY : xxxxxxxxxxxxxxx ".length(),
				this.getVertexBufferObjectManager());

		this.mPhysicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0),
				false, 8, 1);

		scene = new Scene();

		scene.registerUpdateHandler(this.mPhysicsWorld);
		hud = new HUD();
		hud.attachChild(bt1txt);
		hud.attachChild(bt2txt);
		hud.attachChild(fpsTextY);

		try {
			final TMXLoader tmxLoader = new TMXLoader(this.getAssets(),
					this.mEngine.getTextureManager(),
					TextureOptions.BILINEAR_PREMULTIPLYALPHA,
					this.getVertexBufferObjectManager(), TilePropertiesListener);

			this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/desert.tmx");

		} catch (final TMXLoadException e) {
			Debug.e(e);
		}

	
		for (int i = 0; i < this.mTMXTiledMap.getTMXLayers().size(); i++) {
			TMXLayer layer = this.mTMXTiledMap.getTMXLayers().get(i);
			if (!layer.getTMXLayerProperties()
					.containsTMXProperty("walk", "no"))
				scene.attachChild(layer);

		}

		TilePropertiesListener = new ITMXTilePropertiesListener() {

			@Override
			public void onTMXTileWithPropertiesCreated(
					final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer,
					final TMXTile pTMXTile,
					final TMXProperties<TMXTileProperty> pTMXTileProperties) {
				/*
				 * if (pTMXTileProperties.containsTMXProperty("walk", "no")) {
				 * 
				 * }
				 * 
				 * if (pTMXTileProperties.containsTMXProperty("gate", "yes")) {
				 * 
				 * }
				 */
			}

		};

		centerX = (CAMERA_WIDTH - this.mPlayerTextureRegion.getWidth()) / 2;
		centerY = (CAMERA_HEIGHT - this.mPlayerTextureRegion.getHeight()) / 2;

		this.readMapObjects(mTMXTiledMap);
		
		final TMXLayer tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);

		this.mBoundChaseCamera.setBounds(0, 0, tmxLayer.getHeight(),
				tmxLayer.getWidth());
		this.mBoundChaseCamera.setBoundsEnabled(true);
		this.addBounds(tmxLayer.getWidth(), tmxLayer.getHeight());

		final AnimatedSprite player = new AnimatedSprite(centerX, centerY,
				this.mPlayerTextureRegion, this.getVertexBufferObjectManager());
		this.mBoundChaseCamera.setChaseEntity(player);
		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(0,
				0, 0.5f);

		final Rectangle pbox = new Rectangle(player.getX(), player.getY(),
				player.getWidth() - 10, player.getHeight() / 4,
				this.getVertexBufferObjectManager());
		pbox.setVisible(false);

		mPlayerBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, pbox,
				BodyType.DynamicBody, playerFixtureDef);
		
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
				player, mPlayerBody, true, false) {

			@Override
			public void onUpdate(float pSecondsElapsed) {
				
				final IShape shape = this.mShape;
				final Body body = this.mBody;

				if (this.mUpdatePosition) {
					final Vector2 position = body.getPosition();
					final float pixelToMeterRatio = this.mPixelToMeterRatio;
					shape.setPosition(position.x * pixelToMeterRatio
							- this.mShapeHalfBaseWidth, position.y
							* pixelToMeterRatio - (player.getHeight() - 5));
				}
				mBoundChaseCamera.updateChaseEntity();
			}
		});
		
		scene.attachChild(player);

		
		aButton = new ButtonSprite(CAMERA_WIDTH - 80, CAMERA_HEIGHT - 130,
				aButtonTextureRegion, mEngine.getVertexBufferObjectManager()) {

			@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch (pTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
				case TouchEvent.ACTION_MOVE:
					if (gettalk) {
						bt1txt.setText("He said : " + nowTalkstr);
						gettalk = false;
					} else {
						bt1txt.setText("He said : ...");
					}

					break;
				default:
					
				}
				return true;
			}

		};
		bButton = new ButtonSprite(CAMERA_WIDTH - 150, CAMERA_HEIGHT - 70,
				bButtonTextureRegion, mEngine.getVertexBufferObjectManager()) {

			@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch (pTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
				case TouchEvent.ACTION_MOVE:
					accel = 8;
					break;
				default:
					accel = 4;
				}
				bt2txt.setText("B button : speed " + accel);
				return true;
			}

		};

		hud.setOnAreaTouchTraversalFrontToBack();
		hud.attachChild(aButton);
		hud.registerTouchArea(aButton);
		hud.attachChild(bButton);
		hud.registerTouchArea(bButton);
		hud.setTouchAreaBindingOnActionDownEnabled(true);

		getEngine().getCamera().setHUD(hud);

		this.mDigitalOnScreenControl = new DigitalOnScreenControl(0,
				CAMERA_HEIGHT
						- this.mOnScreenControlBaseTextureRegion.getHeight(),
				this.mBoundChaseCamera, this.mOnScreenControlBaseTextureRegion,
				this.mOnScreenControlKnobTextureRegion, 0.1f,
				this.getVertexBufferObjectManager(),
				new IOnScreenControlListener() {
					@Override
					public void onControlChange(
							final BaseOnScreenControl pBaseOnScreenControl,
							final float pValueX, final float pValueY) {

						if (pValueY == 1) {
							// Up
							if (direction != 1) {
								player.animate(
										new long[] { SPEED, SPEED, SPEED }, 0,
										2, true);
								direction = 1;
							}

						} else if (pValueY == -1) {
							// Down
							if (direction != 2) {
								player.animate(
										new long[] { SPEED, SPEED, SPEED }, 9,
										11, true);
								direction = 2;
							}
						} else if (pValueX == -1) {
							// Left
							if (direction != 3) {
								player.animate(
										new long[] { SPEED, SPEED, SPEED }, 3,
										5, true);
								direction = 3;
							}
						} else if (pValueX == 1) {
							// Right
							if (direction != 4) {
								player.animate(
										new long[] { SPEED, SPEED, SPEED }, 6,
										8, true);
								direction = 4;
							}
						} else {
							if (player.isAnimationRunning()) {
								player.stopAnimation();
								// direction = 0;
							}

						}

						mPlayerBody.setLinearVelocity(pValueX * accel, pValueY
								* accel);
					}
				});
		this.mDigitalOnScreenControl.getControlBase().setBlendFunction(
				GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.mDigitalOnScreenControl.getControlBase().setAlpha(0.5f);
		this.mDigitalOnScreenControl.getControlBase().setScaleCenter(0, 128);
		this.mDigitalOnScreenControl.getControlBase().setScale(1.25f);
		this.mDigitalOnScreenControl.getControlKnob().setScale(1.25f);
		this.mDigitalOnScreenControl.refreshControlKnobPosition();

		scene.setChildScene(this.mDigitalOnScreenControl);

		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() {
			}

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				final float[] playerFootCordinates = player
						.convertLocalToSceneCoordinates(15, 45);

				
				final TMXTile tmxTile = tmxLayer.getTMXTileAt(
						playerFootCordinates[Constants.VERTEX_INDEX_X],
						playerFootCordinates[Constants.VERTEX_INDEX_Y]);

				if (tmxTile != null) {

					fpsTextY.setText("x : " + (int) player.getX() + " | y : "
							+ (int) player.getY());

					for (Upc upc : UPChandle) {
						if (player.collidesWith(upc.getUPC())) {
							nowTalkstr = upc.getTalk(0);

							if (nowTalkstr.equalsIgnoreCase("false")) {
								gettalk = false;
							} else {
								gettalk = true;
							}

						}
					}
				}
			}
		});

		return scene;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	private void readMapObjects(TMXTiledMap map) {
		
		for (final TMXObjectGroup group : this.mTMXTiledMap
				.getTMXObjectGroups()) {

			
			if (group.getTMXObjectGroupProperties().containsTMXProperty("walk",
					"no")) {
				
				for (final TMXObject object : group.getTMXObjects()) {
					final Rectangle rect = new Rectangle(object.getX(),
							object.getY(), object.getWidth(),
							object.getHeight(),
							this.getVertexBufferObjectManager());
					final FixtureDef boxFixtureDef = PhysicsFactory
							.createFixtureDef(0, 0, 1f);
					PhysicsFactory.createBoxBody(this.mPhysicsWorld, rect,
							BodyType.StaticBody, boxFixtureDef);
					rect.setVisible(false);
					this.scene.attachChild(rect);
				}
			}

			if (group.getTMXObjectGroupProperties().containsTMXProperty(
					"player", "start")) {
				// This is our "wall" layer. Create the boxes from it
				for (final TMXObject object : group.getTMXObjects()) {
					centerX = object.getX();
					centerY = object.getY();
					break;
				}
			}

			if (group.getTMXObjectGroupProperties().containsTMXProperty("upc",
					"upc")) {
				UPChandle = new ArrayList<Upc>();
				for (final TMXObject object : group.getTMXObjects()) {
					if (object.getTMXObjectProperties().containsTMXProperty(
							"image", "hero")) {

						Upc hero = new Upc("player.png", object.getX(),
								object.getY(), this, scene, this.mPhysicsWorld,
								SPEED);
						hero.setDirection(2);
						hero.setTalk(object.getTMXObjectProperties()
								.getValuefromName("talk"));
						UPChandle.add(hero);

					}

				}
			}

		}
	}

	private void addBounds(float width, float height) {
		final Rectangle bottom = new Rectangle(0, height - 2, width, 2,
				this.getVertexBufferObjectManager());
		bottom.setVisible(false);
		final Rectangle top = new Rectangle(0, 0, width, 2,
				this.getVertexBufferObjectManager());
		top.setVisible(false);
		final Rectangle left = new Rectangle(0, 0, 2, height,
				this.getVertexBufferObjectManager());
		left.setVisible(false);
		final Rectangle right = new Rectangle(width - 2, 0, 2, height,
				this.getVertexBufferObjectManager());
		right.setVisible(false);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0,
				1f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, bottom,
				BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, top,
				BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left,
				BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right,
				BodyType.StaticBody, wallFixtureDef);

		this.scene.attachChild(bottom);
		this.scene.attachChild(top);
		this.scene.attachChild(left);
		this.scene.attachChild(right);
	}

}
