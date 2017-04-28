package com.smart.customviews;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.smartprime.R;

public class ExpandableHTMLTextView extends LinearLayout implements OnClickListener {
	SmartTextView mTv;
	private boolean mRelayout = false;
	private boolean mCollapsed = true; // Show short version as default.
	private int mMaxCollapsedLines = 8; // The default number of lines;


//	private OnExpandableTextViewClickListener expandableTextViewClickListener;


	public ExpandableHTMLTextView(Context context) {
		super(context);
		init();
	}

	public ExpandableHTMLTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ExpandableHTMLTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	void init() {
		mMaxCollapsedLines = 3;
	}

	@Override
	public void onClick(View v) {

		mCollapsed = !mCollapsed;
		mTv.setMaxLines(mCollapsed ? mMaxCollapsedLines : Integer.MAX_VALUE);
//		expandableTextViewClickListener.onClick(v);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// If no change, measure and return
		if (!mRelayout || getVisibility() == View.GONE) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;	
		}
		mRelayout = false;
		// Setup with optimistic case
		// i.e. Everything fits. No button needed

		mTv.setMaxLines(Integer.MAX_VALUE);
		// Measure
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// If the text fits in collapsed mode, we are done.
		if (mTv.getLineCount() <= mMaxCollapsedLines) {
			
			return;
		}
		// Doesn't fit in collapsed mode. Collapse text view as needed. Show
		// button.
		if (mCollapsed) {
			mTv.setMaxLines(mMaxCollapsedLines);
			
		}

		// Re-measure with new setup
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private void findViews() {
		mTv = (SmartTextView) findViewById(R.id.txtDescription);
		mTv.setOnClickListener(this);

	}

	public void setText(String text) {
		mRelayout = true;
		if (mTv == null) {
			findViews();
		}
		String trimmedText = text.trim();
		mTv.setText(Html.fromHtml(trimmedText,null, new MyTagHandler()));
		this.setVisibility(trimmedText.length() == 0 ? View.GONE : View.VISIBLE);
	}

	public CharSequence getText() {
		if (mTv == null) {
			return "";
		}
		return mTv.getText();
	}
	/*public void registerExpandableTextViewClickListener(OnExpandableTextViewClickListener expandableTextViewClickListener) {
		this.expandableTextViewClickListener = expandableTextViewClickListener;
	}*/

	public interface OnExpandableTextViewClickListener {

		void onClick(View v);
	}


}
