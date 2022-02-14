package com.example.facemesh;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import androidx.appcompat.widget.AppCompatImageView;

import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Size;

import com.google.common.collect.ImmutableSet;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.solutions.facemesh.FaceMeshConnections;
import com.google.mediapipe.solutions.facemesh.FaceMeshResult;

import java.util.List;

/**
 * An ImageView implementation for displaying {@link FaceMeshResult}.
 */
public class FaceMeshResultImageView extends AppCompatImageView {
    private static final String TAG = "FaceMeshResultImageView";

    private static final int LIPS_COLOR = Color.parseColor("#CD2527");
    private Bitmap latest;

    public FaceMeshResultImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FaceMeshResultImageView(Context context) {
        super(context);
        setScaleType(AppCompatImageView.ScaleType.FIT_CENTER);
    }

    /**
     * Sets a {@link FaceMeshResult} to render.
     *
     * @param result a {@link FaceMeshResult} object that contains the solution outputs and the input
     *               {@link Bitmap}.
     */
    public void setFaceMeshResult(FaceMeshResult result) {
        if (result == null) {
            return;
        }
        Bitmap bmInput = result.inputBitmap();
        int width = bmInput.getWidth();
        int height = bmInput.getHeight();
        latest = Bitmap.createBitmap(width, height, bmInput.getConfig());
        Canvas canvas = new Canvas(latest);
        Size imageSize = new Size(width, height);
        canvas.drawBitmap(bmInput, new Matrix(), null);
        int numFaces = result.multiFaceLandmarks().size();
        for (int i = 0; i < numFaces; ++i) {
            drawLipsOnCanvas(
                    canvas,
                    result.multiFaceLandmarks().get(i).getLandmarkList(),
                    imageSize,
                    LIPS_COLOR);
        }
    }

    /**
     * Updates the image view with the latest {@link FaceMeshResult}.
     */
    public void update() {
        postInvalidate();
        if (latest != null) {
            setImageBitmap(latest);
        }
    }

    //ONLY LIPS
    private void drawLipsOnCanvas(
            Canvas canvas,
            List<NormalizedLandmark> faceLandmarkList,
            Size imageSize,
            int color) {
        // Draw connections.
        Path path = new Path();
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        ImmutableSet<FaceMeshConnections.Connection> connections = FaceMeshConnections.FACEMESH_LIPS;

        //bellow bellow
        for(int i=0; i<10; i++){
            NormalizedLandmark start = faceLandmarkList.get(connections.asList().get(i).start());
            NormalizedLandmark end = faceLandmarkList.get(connections.asList().get(i).end());

            if(i==0){
                path.moveTo(start.getX()* imageSize.getWidth(), start.getY()* imageSize.getHeight());
            }
            path.lineTo(start.getX()* imageSize.getWidth(), start.getY()* imageSize.getHeight());
            path.lineTo(end.getX()* imageSize.getWidth(), end.getY()* imageSize.getHeight());
        }

        //bellow up
        for(int i=29; i>19; i--){
            NormalizedLandmark start = faceLandmarkList.get(connections.asList().get(i).start());
            NormalizedLandmark end = faceLandmarkList.get(connections.asList().get(i).end());

            path.lineTo(start.getX()* imageSize.getWidth(), start.getY()* imageSize.getHeight());
            path.lineTo(end.getX()* imageSize.getWidth(), end.getY()* imageSize.getHeight());
        }

        //up up
        for(int i=10; i<20; i++){
            NormalizedLandmark start = faceLandmarkList.get(connections.asList().get(i).start());
            NormalizedLandmark end = faceLandmarkList.get(connections.asList().get(i).end());

            path.lineTo(start.getX()* imageSize.getWidth(), start.getY()* imageSize.getHeight());
            path.lineTo(end.getX()* imageSize.getWidth(), end.getY()* imageSize.getHeight());
        }

        //up bellow
        for(int i=39; i>29; i--){
            NormalizedLandmark start = faceLandmarkList.get(connections.asList().get(i).start());
            NormalizedLandmark end = faceLandmarkList.get(connections.asList().get(i).end());

            path.lineTo(start.getX()* imageSize.getWidth(), start.getY()* imageSize.getHeight());
            path.lineTo(end.getX()* imageSize.getWidth(), end.getY()* imageSize.getHeight());
        }

        path.close();
        canvas.drawPath(path, paint);

    }
}

