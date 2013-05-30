package com.example.testgame;

import java.util.ArrayList;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Upc {
	public BitmapTextureAtlas mBitmapTextureAtlas;
	public TiledTextureRegion mUpcTextureRegion;
	public AnimatedSprite upc;
	public long animspeed = 0;
	public ArrayList<String> upcTalk;
	
	public Upc(String file,float x,float y,BaseGameActivity bgame,Scene scene,PhysicsWorld mPhysicsWorld,long speed){
		animspeed = speed;
		upcTalk = new ArrayList<String>();
		
		mBitmapTextureAtlas = new BitmapTextureAtlas( bgame.getTextureManager(), 96, 192, TextureOptions.DEFAULT);
		mUpcTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(mBitmapTextureAtlas, bgame.getApplicationContext(), file, 0, 0, 3, 4);
		this.mBitmapTextureAtlas.load();
		
		upc = new AnimatedSprite(x, y, mUpcTextureRegion, bgame.getVertexBufferObjectManager());
		
		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(0,0, 1f);

		final Rectangle pbox = new Rectangle(upc.getX(), upc.getY(),
				upc.getWidth(), upc.getHeight(),
				bgame.getVertexBufferObjectManager());
		pbox.setVisible(false);

		final Body mUpcBody = PhysicsFactory.createBoxBody(mPhysicsWorld, pbox,
				BodyType.KinematicBody, playerFixtureDef);
		// mPlayerBody.
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(upc, mUpcBody, true, false));
		scene.attachChild(upc);
	}
	
	public void setDirection(int direction) {
		
		switch(direction){
			case 1 :
				upc.animate(new long[] { animspeed, animspeed, animspeed }, 0, 2, true);
				break;
			case 2 :
				upc.animate(new long[] { animspeed, animspeed, animspeed }, 9, 11, true);
				break;
			case 3 :
				upc.animate(new long[] { animspeed, animspeed, animspeed }, 3, 5, true);
				break;	
			case 4 :
				upc.animate(new long[] { animspeed, animspeed, animspeed }, 6, 8, true);
				break;
			case 0 :
			default : 
				upc.stopAnimation();
				
		}
	}
	
	public AnimatedSprite getUPC(){
		return this.upc;
	}
	
	public void setTalk(String str){
		upcTalk.add(str);
	}
	
	public String getTalk(int index){
		return upcTalk.get(index).toString();
	}
}
