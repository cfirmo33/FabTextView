package beepbeep.fabtextview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FabTextView extends RelativeLayout {

    private enum State {
        EXPAND(0), SHRINK(1);
        int num;

        State(int num) {
            this.num = num;
        }

        public int getNum() {
            return num;
        }

        public static State fromId(int id) {
            for (State type : State.values()) {
                if (type.getNum() == id) {
                    return type;
                }
            }
            return null;
        }
    }

    private State state = State.EXPAND; // TODO: make this configurable
    private View startView;
    private ImageView endView;
    private TextView shrinkableTextView;
    private float distanceX = 0;

    private String iconText;
    private int iconTextSize = 0, diameter;
    private Drawable iconDrawable;
    private int backgroundColor;

    private Drawable roundDrawable;

    public FabTextView(Context context) {
        super(context);
        setup();
    }

    public FabTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        obtainAttr(context, attrs);
        setup();
    }

    public FabTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainAttr(context, attrs);
        setup();
    }

    private void obtainAttr(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FabTextView);
        try {
            iconText = ta.getString(R.styleable.FabTextView_fab_text);
            iconTextSize = ta.getDimensionPixelOffset(R.styleable.FabTextView_fab_textSize, 0);
            iconDrawable = ta.getDrawable(R.styleable.FabTextView_fab_iconSrc);
            backgroundColor = ta.getColor(R.styleable.FabTextView_fab_backgroundColor, Color.BLUE);
            diameter = ta.getDimensionPixelOffset(R.styleable.FabTextView_fab_diameter, 0);
//            state = State.fromId(ta.getInt(R.styleable.FabTextView_fab_state, 0));
        } finally {
            ta.recycle();
        }
    }

    private void setup() {
        inflate(getContext(), R.layout.fab_text_view, this);

        shrinkableTextView = (TextView) findViewById(R.id.shrinkable);
        startView = findViewById(R.id.start_view);
        endView = (ImageView) findViewById(R.id.end_view);
        if (iconText != null && !iconText.isEmpty()) {
            shrinkableTextView.setText(iconText);
        }
        if (iconTextSize > 0) {
            shrinkableTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, iconTextSize);
        }
        setIconDrawable(iconDrawable);

        shrinkableTextView.setBackgroundColor(backgroundColor);
        roundDrawable = getResources().getDrawable(R.drawable.round_bg);
        roundDrawable.setColorFilter(backgroundColor, PorterDuff.Mode.SRC_ATOP);
        startView.setBackground(roundDrawable);
        endView.setBackground(roundDrawable);

        // size
        int radius = diameter / 2;

        startView.getLayoutParams().height = diameter;
        startView.getLayoutParams().width = diameter;
        shrinkableTextView.getLayoutParams().height = diameter;
        shrinkableTextView.setPadding(radius, 0, diameter, 0);//TODO: RTL
        RelativeLayout.LayoutParams shrinkableTextViewLayoutParams = (RelativeLayout.LayoutParams) shrinkableTextView.getLayoutParams();
        shrinkableTextViewLayoutParams.setMargins(-radius, 0, 0, 0);
        shrinkableTextView.setLayoutParams(shrinkableTextViewLayoutParams);

        RelativeLayout.LayoutParams endViewLayoutParams = (RelativeLayout.LayoutParams) endView.getLayoutParams();
        endViewLayoutParams.setMargins(-radius, 0, 0, 0);
        endView.setLayoutParams(endViewLayoutParams);
        endView.getLayoutParams().height = diameter;
        endView.getLayoutParams().width = diameter;
    }

    public void expand() {
        if (state == State.SHRINK) {
            state = State.EXPAND;
            ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(shrinkableTextView, "scaleX", 1f);
            ObjectAnimator moveX = ObjectAnimator.ofFloat(startView, "translationX", 0);
            moveX.start();
            scaleUpX.start();
        }
    }

    public void shrink() {
        if (state == State.EXPAND) {
            state = State.SHRINK;
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(shrinkableTextView, "scaleX", 0f);
            shrinkableTextView.setPivotX(shrinkableTextView.getWidth());
            if (distanceX == 0) {
                distanceX = shrinkableTextView.getWidth();
            }
            ObjectAnimator moveX = ObjectAnimator.ofFloat(startView, "translationX", distanceX);
            moveX.start();
            scaleDownX.start();
        }
    }

    public void toggle() {
        if (state == State.EXPAND) {
            shrink();
        } else {
            expand();
        }
    }

    public void setIconDrawable(Drawable drawable) {
        if (drawable != null) {
            endView.setImageDrawable(drawable);
        }
    }

}