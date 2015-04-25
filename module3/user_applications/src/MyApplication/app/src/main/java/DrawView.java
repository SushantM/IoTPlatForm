import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.Display;
import android.view.View;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import java.math.MathContext;
public class DrawView extends View {

    Paint paint;
    double sX,sY,r,vR;
    double dX,dY;
    double vX,vY;
    public DrawView(Context context) {
        super(context);
        r=15;
        vR=10;
        sX=0;sY=0;
        vX=sX;vY=sY;
        paint=new Paint();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.BLUE);
        canvas.drawCircle((float)sX,(float)sY,(float)r,paint);

        paint.setColor(Color.GREEN);
        canvas.drawCircle((float)dX,(float)dY,(float)r,paint);


        double n=Math.sqrt((dX-vX)*(dX-vX)+(dY-vY)*(dY-vY));
        vX=(dX-vX)/n;
        vY=(dY-vY)/n;

        paint.setColor(Color.RED);
        canvas.drawCircle((float)vX,(float)vY,(float)vR,paint);



        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        }, 1000/25);
    }
    Handler mHandler = new Handler();

}