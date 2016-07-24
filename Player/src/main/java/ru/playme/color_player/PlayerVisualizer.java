package ru.playme.color_player;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Random;

public class PlayerVisualizer
        extends View {
    private static final int MAX_RADIUS = 10;
    private long previousTime = 0;
    private boolean isPlaying = false;
    private Random random = new Random(System.currentTimeMillis());

    private byte[] bytes;
    private float[] points;
    private Rect rect = new Rect();
    private Paint forePaint = new Paint();

    public PlayerVisualizer(Context context) {
        super(context);
        init();
    }

    public PlayerVisualizer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerVisualizer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bytes = null;
        forePaint.setStrokeWidth(1f);
        forePaint.setAntiAlias(true);
        forePaint.setColor(Color.rgb(0, 255, 0));
        setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    public void updateVisualizer(byte[] bytes, boolean isPlaying) {
        this.bytes = bytes;
        this.isPlaying = isPlaying;
        invalidate();
    }


    /**
     * Draw visualizer with lines
     */
    private void drawLineEqualizer(Canvas canvas){
        if (bytes == null) {
            return;
        }

        if(System.currentTimeMillis() - previousTime > 50 && isPlaying) {
            if (points == null || points.length < bytes.length * 4) {
                points = new float[bytes.length * 4];
            }
            rect.set(0, 0, getWidth(), getHeight());
            int middle = rect.height() / 2;
            for (int i = 0; i < bytes.length - 1; i++) {
                points[i * 4] = rect.width() * i / (bytes.length - 1);
                points[i * 4 + 1] = middle + ((byte) (bytes[i] + 128)) * middle / 128;
                points[i * 4 + 2] = rect.width() * (i + 1) / (bytes.length - 1);
                points[i * 4 + 3] = middle + ((byte) (bytes[i + 1] + 128)) * middle / 128;
            }

            PlayerApplication.getPlayerApplication().getNumberTheme();
            forePaint.setColor(PlayerApplication.getPlayerApplication().getColorTheme(70, 0, 0, 0));
            canvas.drawLines(points, forePaint);

            previousTime = System.currentTimeMillis();
        }
    }

    /**
     * Draw visualizer with points
     */
    private void drawPointEqualizer(Canvas canvas){
        if (bytes == null) {
            return;
        }

        if(System.currentTimeMillis() - previousTime > 50 && isPlaying){
            rect.set(0, 0, getWidth(), getHeight());
            int middle = rect.height() / 2;
            for (int i = 0; i < bytes.length - 2; i++) {
                if(i % 16 == 0){
                    int x = rect.width() * i / bytes.length;
                    int y = rect.height() * (bytes[i] + 128) / 255;
                    int r = random.nextInt(rect.height() / 10);

                    if(y < middle){
                        y += 2 * r;
                    } else {
                            y -= 2 * r;
                        }

                    forePaint.setColor(PlayerApplication.getPlayerApplication().getColorTheme(140, random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                    canvas.drawCircle(x, y, r, forePaint);
                }
            }

            previousTime = System.currentTimeMillis();
        }
    }

    /**
     * Draw visualizer with rectangles
     */
    private void drawRectEqualizer(Canvas canvas){
        if (bytes == null) {
            return;
        }

        if(System.currentTimeMillis() - previousTime > 50 && isPlaying) {
            rect.set(0, 0, getWidth(), getHeight());
            int numRect = bytes.length / 32;
            int middle = rect.height() / 2;
            int widthRect = rect.width() / numRect;
            int j = 0;
            int average = 0;
            for (int i = 0; i < bytes.length; i++) {
                if (i % 32 == 0) {
                    int currentByte = average / 32;
                    average = 0;
                    int y = rect.height() * currentByte / 510;
                    forePaint.setColor(PlayerApplication.getPlayerApplication().getColorTheme(50, 0, 0, 0));
                    canvas.drawRect(j * widthRect + 2, middle - y, j * widthRect + widthRect, middle + y, forePaint);
                    j++;
                }

                average += bytes[i] + 128;
            }
            previousTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch(PlayerApplication.getPlayerApplication().getNumberVisualizer()){
            case PlayerApplication.VISUALIZER_NONE:
                break;
            case PlayerApplication.VISUALIZER_LINE:
                drawLineEqualizer(canvas);
                break;
            case PlayerApplication.VISUALIZER_CIRCLE:
                drawPointEqualizer(canvas);
                break;
            case PlayerApplication.VISUALIZER_RECT:
                drawRectEqualizer(canvas);
                break;
        }
    }
}
