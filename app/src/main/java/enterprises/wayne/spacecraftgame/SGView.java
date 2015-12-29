package enterprises.wayne.spacecraftgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Aaron on 12/27/2015.
 */
public class SGView extends SurfaceView
    implements Runnable {

    private Context mContext;

    private int mScreenX;
    private int mScreenY;

    volatile boolean mIsPlaying;
    Thread mGameThread = null;

    private PlayerSpacecraft mPlayer;

    // For drawing
    private Paint mPaint;
    private Canvas mCanvas;
    private SurfaceHolder mHolder; // for locking the canvas

    public SGView(Context context, int screenX, int screenY) {
        super(context);

        mContext = context;

        mScreenX = screenX;
        mScreenY = screenY;

        mHolder = getHolder();
        mPaint = new Paint();

        restartGame();
    }

    private void restartGame() {
        // Initialize spacecrafts
        mPlayer = new PlayerSpacecraft(mContext, Spacecraft.Type.HERO,
                mScreenX, mScreenY);


    }

    @Override
    public void run() {
        while (mIsPlaying) {
            update();
            draw();
        }
    }

    private void update() {
        mPlayer.update();
    }

    private void draw() {
        if (mHolder.getSurface().isValid()) {
            // Lock the area of memory to draw to
            mCanvas = mHolder.lockCanvas();

            // Rub out the last frame
            mCanvas.drawColor(Color.argb(255, 0, 0, 0));

            // Draw trivial text
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setTextSize(50);
            mCanvas.drawText("Hello", mScreenX / 2, mScreenY / 2, mPaint);

            // Draw the player
            mCanvas.drawBitmap(
                    mPlayer.getBitmap(),
                    mPlayer.getX(),
                    mPlayer.getY(),
                    mPaint);

            // Unlock and draw the scene
            mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    public void pause() {
        mIsPlaying = false;
        try {
            // Clean up thread
            mGameThread.join();
        } catch (InterruptedException e) {

        }
    }

    public void resume() {
        mIsPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }
}