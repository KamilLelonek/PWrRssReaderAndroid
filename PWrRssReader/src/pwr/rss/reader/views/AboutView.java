package pwr.rss.reader.views;

import org.apache.http.protocol.HTTP;

import pwr.rss.reader.R;
import pwr.rss.reader.animations.AnimationEndListener;
import pwr.rss.reader.animations.CollapseAnimation;
import pwr.rss.reader.animations.ExpandAnimation;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AboutView extends LinearLayout implements OnClickListener {
	private static final boolean isOverICS = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	private ImageView expandCollapse;
	private ImageView imageViewMail;
	private LinearLayout aboutHeader;
	private LinearLayout aboutDescription;
	
	private boolean expanded = false;
	private boolean expanding = false;
	
	private final AnimationListener animationListener = new AnimationEndListener() {
		@Override
		public void onAnimationEnd(Animation animation) {
			expanding = false;
		}
	};
	
	public AboutView(final Context context, AttributeSet attrs) {
		super(context, attrs);
		setView(context);
		
		imageViewMail = (ImageView) findViewById(R.id.imageViewMail);
		imageViewMail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendEmail(context);
			}
		});
		expandCollapse = (ImageView) findViewById(R.id.expandCollapse);
		aboutDescription = (LinearLayout) findViewById(R.id.aboutDescription);
		aboutHeader = (LinearLayout) findViewById(R.id.aboutHeader);
		aboutHeader.setOnClickListener(this);
		
		if (!isOverICS) {
			aboutDescription.setVisibility(View.VISIBLE);
		}
	}
	
	private void setView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.about_view, this, true);
	}
	
	private void sendEmail(final Context context) {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "kamil.lelonek@student.pwr.wroc.pl" });
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[Android] PWrRssReader");
		context.startActivity(emailIntent);
	}
	
	private void animateExpand() {
		Animation animation = new ExpandAnimation(aboutDescription);
		aboutDescription.setVisibility(View.VISIBLE);
		animate(animation, true);
	}
	
	private void animateCollapse() {
		Animation animation = new CollapseAnimation(aboutDescription);
		animate(animation, false);
	}
	
	public void animate(Animation animation, boolean isExpanded) {
		animation.setAnimationListener(animationListener);
		aboutDescription.startAnimation(animation);
		expanded = isExpanded;
	}
	
	@Override
	public void onClick(View v) {
		if (!expanding && isOverICS) {
			int imageResource = 0;
			
			if (!expanded) {
				imageResource = R.drawable.ic_collapse;
				animateExpand();
				
			}
			else if (expanded) {
				imageResource = R.drawable.ic_expand;
				animateCollapse();
			}
			
			expandCollapse.setImageResource(imageResource);
			expanding = true;
		}
	}
}