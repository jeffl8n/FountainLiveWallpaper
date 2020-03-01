package com.builtclean.android.livewallpapers.fountain;

import com.builtclean.android.livewallpapers.fountain.R;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class FountainLiveWallpaper extends WallpaperService {

	private final Handler mHandler = new Handler();

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		return new FountainEngine();
	}

	class FountainEngine extends Engine {

		public int offsetX = -75;
		public int offsetY = 0;
		public int height;
		public int width;
		public int visibleWidth;

		public int nextImage;
		public int currentImage = 1;
		public int totalImages = 132;

		private final Runnable mDrawFountain = new Runnable() {
			public void run() {
				drawFrame();
			}
		};
		private boolean mVisible;

		private MediaPlayer fountainPlayer;

		FountainEngine() {
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);

			setTouchEventsEnabled(true);

			fountainPlayer = MediaPlayer.create(getApplicationContext(),
					R.raw.fountain);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			mHandler.removeCallbacks(mDrawFountain);

			fountainPlayer.release();
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			if (visible) {
				drawFrame();
			} else {
				mHandler.removeCallbacks(mDrawFountain);
			}
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			mHandler.removeCallbacks(mDrawFountain);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {

			this.height = height;
			this.width = width;
			this.visibleWidth = width;

			drawFrame();

			super.onSurfaceChanged(holder, format, width, height);
		}

		@Override
		public Bundle onCommand(String action, int x, int y, int z,
				Bundle extras, boolean resultRequested) {

			Bundle bundle = super.onCommand(action, x, y, z, extras,
					resultRequested);

			if (action.equals("android.wallpaper.tap")) {
				playFountainSound();
			}

			return bundle;
		}

		void drawFrame() {
			final SurfaceHolder holder = getSurfaceHolder();

			Canvas c = null;
			try {
				c = holder.lockCanvas();
				if (c != null) {
					drawFountain(c);
				}
			} finally {
				if (c != null)
					holder.unlockCanvasAndPost(c);
			}

			mHandler.removeCallbacks(mDrawFountain);
			if (mVisible) {
				mHandler.postDelayed(mDrawFountain, 1000 / 30);
			}
		}

		void drawFountain(Canvas c) {

			Resources res = getResources();
			String nextImageStr = "";

			if (++currentImage > totalImages) {
				currentImage = 1;
			}
			nextImageStr = Integer.toString(currentImage);

			while (nextImageStr.length() < 3) {
				nextImageStr = "0" + nextImageStr;
			}
			
			nextImage = res.getIdentifier("image_" + nextImageStr, "drawable",
					"com.builtclean.android.livewallpapers.fountain");

			c.drawBitmap(BitmapFactory.decodeResource(res, nextImage),
					this.offsetX, this.offsetY, null);
		}

		void playFountainSound() {
			fountainPlayer.seekTo(0);
			fountainPlayer.start();
		}
	}
}