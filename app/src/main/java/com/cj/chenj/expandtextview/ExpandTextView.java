package com.cj.chenj.expandtextview;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ExpandTextView extends RelativeLayout {

	public static final int WORD_LENGTH = 26;//设置一个单词最长不能超过26个字符

	private String mString = "";

	private TextView mTextView;

	private FlowLayout mFlowLayout;

	private float mCharSize = 14;//文本字体大小

	private int mZoomRows = 2;//完全显示的是mZoomRows行，一共显示的是mZoomRows+1行
	private int mZoomChar = 9;//最后一行字符显示的个数

	private Context mContext;

	private float mDescSize = 14;//文本字体大小
	private int mDescColor = -1;//文本字体颜色

	private String mExpandTextOpen = null;
	private String mExpandTextClose = null;
	private int mExpandTextColor = -1;
	private float mExpandTextSize = 14;//展开/收缩文本大小

	private int mOpenResId;//展开右边的图片资源id
	private int mCloseResId;//收缩右边的图片资源id

	/*
	 *
	 *  <attr name="desc" format="string" />
        <attr name="descSize" format="float" />
        <attr name="descColor" format="color" />
        <attr name="expandText" format="string" />
        <attr name="expandTextColor" format="color" />
        <attr name="expandTextSize" format="float" />
	 */

	public ExpandTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.ExpandTextView);
		mZoomRows = a.getInt(R.styleable.ExpandTextView_descZoomRows, 2);
		mString = a.getString(R.styleable.ExpandTextView_descText);
		if(TextUtils.isEmpty(mString)){
			mString = "";
		}
		mDescSize = a.getFloat(R.styleable.ExpandTextView_descSize, 14);
		mCharSize = mDescSize;
		mDescColor = a.getColor(R.styleable.ExpandTextView_descColor, 0);
		mExpandTextOpen = a.getString(R.styleable.ExpandTextView_expandTextOpen);
		mExpandTextClose = a.getString(R.styleable.ExpandTextView_expandTextClose);
		mExpandTextColor = a.getColor(R.styleable.ExpandTextView_expandTextColor, 0);
		mExpandTextSize = a.getFloat(R.styleable.ExpandTextView_expandTextSize, 14);
		mOpenResId = a.getResourceId(R.styleable.ExpandTextView_expandTextOpenDrawable, 0);
		mCloseResId = a.getResourceId(R.styleable.ExpandTextView_expandTextCloseDrawable, 0);
		init(context);
	}

	public ExpandTextView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public ExpandTextView(Context context) {
		this(context,null);
	}

	/**
	 * 初始化
	 * @param context
	 */
	private void init(Context context){
		//ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_expand, null);
		//setContentView(viewGroup);
		mFlowLayout = new FlowLayout(context);
		this.addView(mFlowLayout);
		//初始化TextView，用于计算每个字符占的宽度
		mTextView = getTextView(mCharSize);//(TextView) findViewById(R.id.details_desc);//new TextView(this);//getTextView(18);

		//expandText(false);
		//初始化展开文本
		mTextViewExpand = new TextView(mContext);
		mTextViewExpand.setTextSize(mExpandTextSize);
		mTextViewExpand.setTextColor(mExpandTextColor);
		mTextViewExpand.setGravity(Gravity.CENTER_VERTICAL);
	}

	private boolean mIsOnce = true;

	private int mWidthSize;

	private TextView mTextViewExpand;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(mIsOnce){
			//计算一行最大显示的宽度
			mWidthSize = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
			//默认为收缩状态
			expandText(false);
			//onMeasure可能会执行多次
			mIsOnce = false;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 判断时候是英文字母
	 * @param ch
	 * @return
	 */
	private boolean isA_z(char ch){
		if('A' <= ch && ch <= 'Z' || 'a' <= ch && ch <= 'z'){
			return true;
		}
		return false;
	}

	/**
	 * 是否展开文本
	 * @param isExpand
	 */
	private void expandText(boolean isExpand){
		//DisplayMetrics metric = new DisplayMetrics();
		//getWindowManager().getDefaultDisplay().getMetrics(metric);
		//int widthScreen = metric.widthPixels;     // 屏幕宽度（像素）
		int widthScreen = mWidthSize;
		float width = 0;//计算一行的宽度
		int index = 0;//记住每行开头的下标索引
		int rows = 0;//计算行数
		int isNullCharIndex = 0;//英文状态下的空格，用于计算英文单词

		List<String> strings = new ArrayList<String>();
		for(int i=0;i<mString.length();i++){
			float charWidth = getCharWidth(mTextView,(char)mString.charAt(i));

			if(width + charWidth <= widthScreen){
				width += charWidth;
				if((char)mString.charAt(i) == ' '){//记住一行中最后一次出现的空格
					isNullCharIndex = i;
				}
				//防止英文字符之间不是连续的
				if(!isA_z((char)mString.charAt(i))){
					isNullCharIndex = i;
				}
				if(isNullCharIndex + WORD_LENGTH < i ){//如果连续15个字符还是没有空格就直接截断
					isNullCharIndex = i;
				}
			}else{
				if(mString.length()-1 >= i + 1 && isA_z((char)mString.charAt(i))){
					i = isNullCharIndex ;
					if(mString.length() != i){
						strings.add(mString.substring(index, i + 1));
						index = i + 1;
						width = 0;
						//width += charWidth;
						rows ++;
					}
					continue;
				}

				if(mString.length() != i){
					strings.add(mString.substring(index, i));
					index = i;
					width = 0;
					width += charWidth;
					rows ++;
				}
			}

			//第一种情况：在收缩文本行数以内 （展开和收缩都是一样的，所以就没有收缩和展开）
			if(rows <= mZoomRows && !isExpand && mString.length()-1 == i){
				strings.add(mString.substring(index, i+1));//加最后一行
				for(int j=0;j<strings.size();j++){
					TextView textView = new TextView(mContext);
					textView.setTextSize(mCharSize);
					textView.setTextColor(mDescColor);
					textView.setText(strings.get(j));
					//textView.setBackgroundColor(Color.RED);
					mFlowLayout.addView(textView);
				}
				break;
			}
			//第二种情况：展开时，已经到最后一个字符
			if(isExpand && mString.length()-1 == i){
				strings.add(mString.substring(index, i+1));//加最后一行
				for(int j=0;j<strings.size();j++){
					TextView textView = new TextView(mContext);
					textView.setTextSize(mCharSize);
					textView.setText(strings.get(j));
					textView.setTextColor(mDescColor);
					//textView.setBackgroundColor(Color.GREEN);
					mFlowLayout.addView(textView);
				}

				mTextViewExpand.setText("  "+mExpandTextClose);
				setExpandTextDrawable(mCloseResId);
				mTextViewExpand.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//Toast.makeText(mContext, "收缩", 0).show();
						mFlowLayout.removeAllViews();
						expandText(false);
					}
				});
				mFlowLayout.addView(mTextViewExpand);
				break;
			}
			//第三种情况：收缩时，但是文本已经超出了收缩的个数，所以只保留收缩的文本字数
			if(rows == mZoomRows && index + mZoomChar == i && !isExpand){
				strings.add(mString.substring(index, i+1));//加最后一行
				strings.add("...");
				for(int j=0;j<strings.size();j++){
					TextView textView = new TextView(mContext);
					textView.setTextSize(mCharSize);
					textView.setText(strings.get(j));
					textView.setTextColor(mDescColor);
					//textView.setBackgroundColor(Color.BLUE);
					mFlowLayout.addView(textView);
				}
				mTextViewExpand.setText("  "+mExpandTextOpen);
				setExpandTextDrawable(mOpenResId);
				mTextViewExpand.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//Toast.makeText(mContext, "展开", 0).show();
						mFlowLayout.removeAllViews();
						expandText(true);
					}
				});
				mFlowLayout.addView(mTextViewExpand);
				break;
			}
		}

	}

	/**
	 *  计算每一个字符的宽度
	 * @param textView 初始化工作的文本控件，载体
	 * @param c 字符
	 * @return
	 */
	private float getCharWidth(TextView textView, char c) {
		textView.setText(String.valueOf(c));
		textView.measure(0, 0);
		return textView.getMeasuredWidth();
	}

	/**
	 * 临时文本控件，用于计算每个字符所占的空间大小，主要是宽度
	 * @param size
	 * @return
	 */
	private TextView getTextView(float size){
		TextView textView = new TextView(mContext);
		textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		textView.setTextSize(size);
		return textView;
	}
	/**
	 * 切换图片
	 * @param resId
	 */
	private void setExpandTextDrawable(int resId){
		Drawable drawable = mContext.getResources().getDrawable(resId);
		drawable.setBounds(0,0,drawable.getMinimumWidth() ,drawable.getMinimumHeight() );
		mTextViewExpand.setCompoundDrawables(null, null, drawable, null);
		mTextViewExpand.setCompoundDrawablePadding(dp2px(2)) ;
	}

	/**
	 * dp转px
	 * @param dp
	 * @return
	 */
	private int dp2px(int dp){
		return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,getContext().getResources().getDisplayMetrics());
	}

	/**
	 * 请求重新布局绘制
	 */
	private void requestLay(){
		mIsOnce = true;
		mFlowLayout.removeAllViews();
		requestLayout();
		invalidate();
	}

	/**
	 * 设置显示的文本
	 * @param text
	 */
	public void setText(String text){
		mString = text;
		requestLay();
	}
	/**
	 * 设置显示文本的颜色
	 * @param color
	 */
	public void setTextColor(int color){
		mDescColor = color;
		requestLay();
	}
	/**
	 * 设置显示文本的大小
	 * @param size
	 */
	public void setTextSize(float size){
		mCharSize = mDescSize = size;
		requestLay();
	}
	/**
	 * 设置显示的展开缩放文本
	 */
	public void setExpandText(String open,String close){
		mExpandTextOpen = open;
		mExpandTextClose = close;
		requestLay();
	}
	/**
	 * 设置显示的展开缩放文本的指示图片
	 */
	public void setExpandTextDrawable(int openResId,int closeResId){
		mOpenResId = openResId;
		mCloseResId = closeResId;
		requestLay();
	}
	/**
	 * 设置显示展开缩放文本的颜色
	 * @param color
	 */
	public void setExpandTextColor(int color){
		mExpandTextColor = color;
		requestLay();
	}
	/**
	 * 设置显示展开缩放文本的大小
	 * @param size
	 */
	public void setExpandTextSize(float size){
		mExpandTextSize = size;
		requestLay();
	}

	/**
	 * 设置收缩时显示的行数
	 * @param rows
	 */
	public void setDescZoomRows(int rows){
		mZoomRows = rows;
		requestLay();
	}
}
