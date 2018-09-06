package com.sopan.quotes.utils;

import java.lang.ref.WeakReference;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

public class EABitmapManager extends AsyncTask<Integer, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    private int res = 0;
    private int data = 0;
    int targetWidth = 50;
    int targetHeight = 50;
    Resources resource = null;
    LruCache<String, Bitmap> cache = null;
    private Context context = null;

    public EABitmapManager(ImageView imageView, Resources r, LruCache<String , Bitmap> cache) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        this.cache = cache;
        imageViewReference = new WeakReference<ImageView>(imageView);
        targetWidth = imageView.getWidth()>0?imageView.getWidth():50;
        targetHeight =imageView.getHeight()>0?imageView.getHeight():50;
        targetWidth = targetHeight = 300;
        resource = r;
    }

    public void setContext(Context c){
        context = c;
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
        // TODO Auto-generated method stub
        res = params[0];
        data = res;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 4;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(resource, res, options);
        if(bitmap == null){
            return null;
        }
        targetWidth = bitmap.getWidth()>0?bitmap.getWidth():300;
        targetHeight = bitmap.getHeight()>0?bitmap.getHeight():300;
        return bitmap;
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {

        if(scaleBitmapImage == null){
            return null;
        }
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            final EABitmapManager bitmapWorkerTask =
                    getBitmapManager(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                bitmap = getRoundedShape(bitmap);
                imageView.setImageBitmap(bitmap);
            }
        }
    }


    private static EABitmapManager getBitmapManager(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final EABitmapManager bitmapWorkerTask = getBitmapManager(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == 0 || bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }


    public static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<EABitmapManager> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             EABitmapManager bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<EABitmapManager>(bitmapWorkerTask);
        }

        public EABitmapManager getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }





}
