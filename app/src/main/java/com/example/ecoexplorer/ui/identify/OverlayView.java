package com.example.ecoexplorer.ui.identify;

import static android.opengl.ETC1.getHeight;
import static android.opengl.ETC1.getWidth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import org.tensorflow.lite.task.vision.detector.Detection;

import java.util.ArrayList;
import java.util.List;

public class OverlayView extends View {

    private final Paint boxPaint;
    private final Paint textPaint;
    private List<Detection> detections = new ArrayList<>();
    private int imageWidth = 1;
    private int imageHeight = 1;

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        boxPaint = new Paint();
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(5f);

        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(50f);
    }

    public void setDetections(List<Detection> detections, int imageWidth, int imageHeight) {
        this.detections = detections;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float scaleX = (float) getWidth() / imageWidth;
        float scaleY = (float) getHeight() / imageHeight;

        for (Detection detection : detections) {
            RectF box = detection.getBoundingBox();

            // Scale from image space to view space
            float left = box.left * scaleX;
            float top = box.top * scaleY;
            float right = box.right * scaleX;
            float bottom = box.bottom * scaleY;

            canvas.drawRect(left, top, right, bottom, boxPaint);

            if (!detection.getCategories().isEmpty()) {
                String label = detection.getCategories().get(0).getLabel();
                float score = detection.getCategories().get(0).getScore();
                canvas.drawText(label + " (" + String.format("%.2f", score) + ")", left, top - 10, textPaint);
            }
        }
    }
}
