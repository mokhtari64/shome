package app.shome.ir.shome.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import app.shome.ir.shome.R;
import app.shome.ir.shome.SHomeActivity;

/**
 * Created by Iman on 1/15/2017.
 */
public class IconSelectionActivity extends SHomeActivity implements View.OnClickListener {
    ImageView expandedImageView;
    String editType;
    View bgVIew, back, done;
    GridView gridView;
    int currentRes = -1;
    int zoneIcon[] = new int[]{R.drawable.z01,
            R.drawable.z02,
            R.drawable.z03,
            R.drawable.z04,
            R.drawable.z05,
            R.drawable.z06,
            R.drawable.z06,
            R.drawable.z07,
            R.drawable.z08,
            R.drawable.z09,
            R.drawable.z10,
            R.drawable.z11,
            R.drawable.z12,
            R.drawable.z13,
            R.drawable.z14,
            R.drawable.z15,
            R.drawable.z16,
            R.drawable.z17,
            R.drawable.z18,
            R.drawable.z19,
            R.drawable.z20,
            R.drawable.z21,
            R.drawable.z22,
            R.drawable.z23,
            R.drawable.z24,
            R.drawable.z25,
            R.drawable.z26,
            R.drawable.z27,
            R.drawable.z28,
            R.drawable.z29,
            R.drawable.z30,
            R.drawable.z31,
            R.drawable.z32,
            R.drawable.z33,
            R.drawable.z34,
            R.drawable.z35,
            R.drawable.z36,
            R.drawable.z37,
            R.drawable.z38,
            R.drawable.z39,
            R.drawable.z40,
            R.drawable.z41,
            R.drawable.z42,
            R.drawable.z43,
            R.drawable.z44,
            R.drawable.z45,
            R.drawable.z46,
            R.drawable.z47,
            R.drawable.z48,
            R.drawable.z49,
            R.drawable.z50,
            R.drawable.z51,
            R.drawable.z52,

    };


    int deviceIcon[] = new int[]{R.drawable.l1,
            R.drawable.l2,
            R.drawable.l3,
            R.drawable.l4,
            R.drawable.l5,
            R.drawable.l6,
            R.drawable.l7,
            R.drawable.l8,
            R.drawable.l9,
            R.drawable.l10,
            R.drawable.l11,
            R.drawable.l12,
            R.drawable.l13,
            R.drawable.l14,
            R.drawable.l15,
            R.drawable.l16,
            R.drawable.l17,
            R.drawable.l18,
            R.drawable.l19,
            R.drawable.l20,
            R.drawable.l21,
            R.drawable.l22,
            R.drawable.l23,
            R.drawable.l24,
            R.drawable.l25,
            R.drawable.l26,
            R.drawable.l27,
            R.drawable.l28,
            R.drawable.l29,
            R.drawable.l30,
            R.drawable.l31,
            R.drawable.l32,
            R.drawable.l33,
            R.drawable.l34,
            R.drawable.l35,
            R.drawable.l36,
            R.drawable.l37,
            R.drawable.l38,
            R.drawable.l39,
            R.drawable.l40,
            R.drawable.l42,
            R.drawable.l43,
            R.drawable.l44,
            R.drawable.l45

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                editType = extras.getString("type");
            }
        }
        if (editType == null)
            finish();
        setContentView(R.layout.activity_icon_select);

        expandedImageView = (ImageView) (this)
                .findViewById(R.id.expanded_image);
        gridView = (GridView) findViewById(R.id.gridView);
        bgVIew = findViewById(R.id.iconbg);
        back = findViewById(R.id.back);
        done = findViewById(R.id.done);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentRes == -1) {
                    Toast.makeText(IconSelectionActivity.this, "Please select One", Toast.LENGTH_SHORT).show();
                } else {
                    Intent a = new Intent();
                    a.putExtra("data", currentRes);
                    setResult(OK_STATUS, a);
                    finish();
                }
            }
        });


        gridView.setAdapter(new GridAdapter());

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v,
//                                    int position, long id) {
//
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        int id = (Integer) v.getTag();
        currentRes = id;
        zoomImageFromThumb(v, id);
    }

    class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (editType.trim().equals("zone")) {
                return zoneIcon.length;
            } else {
                return deviceIcon.length;
            }

        }

        @Override
        public Object getItem(int position) {
            return deviceIcon[position];
        }

        @Override
        public long getItemId(int position) {
            return deviceIcon[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ImageView imageView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                imageView = (ImageView) inflater.inflate(R.layout.icons, null);

                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            } else {
                imageView = (ImageView) convertView;
            }
            int idRes=-1;
            if (editType.trim().equals("zone")) {
                idRes=zoneIcon[position];
            } else {
                idRes=deviceIcon[position];
            }

            imageView.setImageResource(idRes);
            imageView.setTag(idRes);
            imageView.setOnClickListener(IconSelectionActivity.this);

            return imageView;

        }
    }

    private Animator mCurrentAnimator;

    private int mShortAnimationDuration;

    private void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there'allScenarios an animation in progress, save it immediately and
        // proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.

        expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the
        // final bounds are the global visible rectangle of the container view.
        // Also
        // set the container view'allScenarios offset as the origin for the bounds, since
        // that'allScenarios
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the
        // "center crop" technique. This prevents undesirable stretching during
        // the animation.
        // Also calculate the start scaling factor (the end scaling factor is
        // always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds
                .width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);
        bgVIew.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the
        // top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set.play(
                ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y,
                        startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down to the
        // original bounds
        // and show the thumbnail instead of the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set.play(
                        ObjectAnimator.ofFloat(expandedImageView, View.X,
                                startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                                startBounds.top))
                        .with(ObjectAnimator.ofFloat(expandedImageView,
                                View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator.ofFloat(expandedImageView,
                                View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        bgVIew.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        bgVIew.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

}
