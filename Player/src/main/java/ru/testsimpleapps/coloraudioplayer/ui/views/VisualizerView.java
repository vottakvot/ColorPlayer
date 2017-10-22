package ru.testsimpleapps.coloraudioplayer.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Random;

import ru.testsimpleapps.coloraudioplayer.App;

public class VisualizerView
        extends View {

    private static final int MAX_RADIUS = 10;
    private static final int FREQUENCY = 50;
    private static final int DEPENDENCE_RECT_COUNT = 32;
    private long previousTime = 0;
    private boolean isPlaying = false;
    private Random random = new Random(System.currentTimeMillis());

    private byte[] bytes;
    private float[] points;
    private Rect rect = new Rect();
    private Paint forePaint = new Paint();

    public VisualizerView(Context context) {
        super(context);
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bytes = null;
        forePaint.setStrokeWidth(3f);
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
    private void drawLineEqualizer(Canvas canvas) {
        if (bytes == null) {
            return;
        }

        if (System.currentTimeMillis() - previousTime > FREQUENCY && isPlaying) {
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

            App.getAppContext().getNumberTheme();
            forePaint.setColor(App.getAppContext().getColorTheme(70, 0, 0, 0));
            canvas.drawLines(points, forePaint);
            previousTime = System.currentTimeMillis();
        }
    }

    /**
     * Draw visualizer with points
     */
    private void drawPointEqualizer(Canvas canvas) {
        if (bytes == null) {
            return;
        }

        if (System.currentTimeMillis() - previousTime > FREQUENCY && isPlaying) {
            rect.set(0, 0, getWidth(), getHeight());
            float middle = (float) rect.height() / 2.0f;
            float x = 0.0f;
            float y = 0.0f;
            float r = 0.0f;
            for (int i = 0; i < bytes.length - 2; i++) {
                if (i % 16 == 0) {
                    x = (float) rect.width() * i / bytes.length;
                    y = (float) rect.height() * (bytes[i] + 128) / 256;
                    r = (float) random.nextInt(rect.height() / 10);

                    if (y < middle) {
                        y += 2 * r;
                    } else {
                        y -= 2 * r;
                    }

                    forePaint.setColor(App.getAppContext().getColorTheme(100, random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                    canvas.drawCircle(x, y, r, forePaint);
                }
            }

            previousTime = System.currentTimeMillis();
        }
    }

    /**
     * Draw visualizer with rectangles
     */
    private void drawRectEqualizer(Canvas canvas) {
        if (bytes == null) {
            return;
        }

        if (System.currentTimeMillis() - previousTime > FREQUENCY && isPlaying) {
            rect.set(0, 0, getWidth(), getHeight());
            final int numberDraw = bytes.length / (DEPENDENCE_RECT_COUNT * 2);
            final float border = 2.0f;
            final float widthRect = (float) rect.width() / (float) DEPENDENCE_RECT_COUNT;
            final float deltaAmplitude = (float) rect.height() / 256.0f;
            final float middle = (float) rect.height() / 2.0f;
            int j = 0;
            float y = 0.0f;
            float average = 0.0f;
            for (int i = 0; i < bytes.length; i++) {
                if ((i + 1) % numberDraw == 0) {
                    y = deltaAmplitude * average * 2.0f / numberDraw;
                    average = 0.0f;
                    forePaint.setColor(App.getAppContext().getColorTheme(50, 0, 0, 0));
                    canvas.drawRect((float) j * widthRect + border, middle - y, (float) j * widthRect + widthRect, middle + y, forePaint);
                    j++;
                } else {
                    average += (float) (128 - Math.abs(bytes[i]));
                }
            }
            previousTime = System.currentTimeMillis();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (App.getAppContext().getNumberVisualizer()) {
//            case App.VISUALIZER_NONE:
//                break;
//            case App.VISUALIZER_LINE:
//                drawLineEqualizer(canvas);
//                break;
//            case App.VISUALIZER_CIRCLE:
//                drawPointEqualizer(canvas);
//                break;
//            case App.VISUALIZER_RECT:
//                drawRectEqualizer(canvas);
//                break;
        }
    }
}
