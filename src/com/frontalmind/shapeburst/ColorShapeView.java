/**
 * 
 */
package com.frontalmind.shapeburst;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

import com.frontalmind.ShapeFactory;
import com.frontalmind.StrokeAndFillDrawable;

/**
 * @author bob
 * 
 */
public class ColorShapeView extends View {

	private Random random;
	private int  animationRate, moveRate, viewWidth, viewHeight, blockSize;
	private Timer createTimer;
	private Timer moveTimer;
	private boolean toggleAnimate = true;
	String shape = "star";
	Paint paint = new Paint();
	RectF rect = new RectF();
	int cornerRadius = 3;
	List<StrokeAndFillDrawable> drawables;
	
	private String shapeStr = "star";
	private String colorRange = "All";
	private String borderColorRange = "All";
	private int fillAlpha = 255;
	private int strokeAlpha = 255;
	private int strokeWidth = 2;
	private int threshold = 0;
	private int decay = 4;

	public ColorShapeView(Context context) {
		super(context);
		blockSize = 50;
		animationRate = 500;
		moveRate = 100;
		decay = 8;
		threshold = 0;
		cornerRadius = blockSize / 10;

		paint.setStyle(Style.FILL_AND_STROKE);

		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					toggleAnimate = !toggleAnimate;
					enableAnimation(toggleAnimate);
				}
				return true;
			}
		});

		random = new Random();
		drawables = new LinkedList<StrokeAndFillDrawable>();
	}

	public void enableAnimation(boolean enable) {
		if (createTimer != null) {
			createTimer.cancel();
			createTimer.purge();
		}
		if (enable) {
			createTimer = new Timer();
			createTimer.scheduleAtFixedRate(new CreateTask(), 0, animationRate);
		}
		
		if (moveTimer != null) {
			moveTimer.cancel();
			moveTimer.purge();
		}
		if (enable) {
			moveTimer  = new Timer();
			moveTimer.scheduleAtFixedRate(new MoveTask(), 0, moveRate);
		}

	}


	class MoveTask extends TimerTask {
		private Handler updateUI = new Handler() {
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				if (ColorShapeView.this.viewWidth == 0)
					return;
				
				for (int i = 0; i < drawables.size(); ++i) {
					drawables.get(i).animate();
				}

				ColorShapeView.this.invalidate();


			}
		};
	

		@Override
		public void run() {
			try {
				updateUI.sendEmptyMessage(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class CreateTask extends TimerTask {
		private Handler updateUI = new Handler() {
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				if (ColorShapeView.this.viewWidth == 0)
					return;
				
				int left = random.nextInt(ColorShapeView.this.viewWidth);
				int top = random.nextInt(ColorShapeView.this.viewHeight);
				int width = random.nextInt(ColorShapeView.this.viewWidth / 4);
				int height = random.nextInt(ColorShapeView.this.viewHeight / 4);

				int right = left + width;
				int bottom = top + height;
				if (right > ColorShapeView.this.viewWidth) {
					right = ColorShapeView.this.viewWidth;
				}
				if (bottom > ColorShapeView.this.viewHeight) {
					bottom = ColorShapeView.this.viewHeight;
				}
				
				StrokeAndFillDrawable shape = ShapeFactory.generateDrawable(ColorShapeView.this.shapeStr,
						ColorShapeView.this.colorRange,
						ColorShapeView.this.borderColorRange,
						ColorShapeView.this.fillAlpha,
						ColorShapeView.this.strokeAlpha,
						ColorShapeView.this.strokeWidth,
						ColorShapeView.this.threshold,
						ColorShapeView.this.decay);
				shape.setBounds(left, top, right, bottom);
				drawables.add(shape);

				if (drawables.size() > 100) {
					drawables.remove(0);
				}

				ColorShapeView.this.invalidate();
			}
		};

		@Override
		public void run() {
			try {
				updateUI.sendEmptyMessage(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		this.viewHeight = h;
		this.viewWidth = w;
		enableAnimation(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (int i = 0; i < drawables.size(); ++i) {
			//paint.setColor(colors.get(i));
			drawables.get(i).draw(canvas);
			// canvas.drawRect(rects.get(i), paint);
			//canvas.drawCircle(rects.get(i).left, rects.get(i).top, rects.get(i)
			//		.width() / 2, paint);
		}
	}

	public void onResume() {
		// enableAnimation(true);
	}

	public void onPause() {
		enableAnimation(false);
	}

	public void setRate(int animationRate) {
		this.animationRate = animationRate;
		enableAnimation(true);

	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
		this.cornerRadius = blockSize / 10;
	}

	public void setColorRange(String colorRange) {
		this.colorRange = colorRange;
	}

	public void setDecayStep(int decayStep) {
		this.decay = decayStep;

	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}


	public void setShape(String shape) {
		this.shape = shape;
	}
}
