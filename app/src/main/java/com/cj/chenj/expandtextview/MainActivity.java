package com.cj.chenj.expandtextview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private String text = "好了，静态的气泡排版到这里就好了，下面的问题是，展开时如何使气泡从中心点，以弧形的路径展开，并且气泡的大小也是由小到大变化。这里就用到的动画类ValueAnimator和ScaleAnimation,详解请参考：http://blog.csdn.net/cj_286/article/details/53020725.向外展开的效果我们可以使用view.layout()不断的重新布局气泡view，让其产生一个平移的效果，下面的一个问题就是如何计算平移轨道上面的left,  top, right,bottom,然后重新请求布局就可以了，那么下面就解决如何计算这个轨迹，分析";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup inflate = (ViewGroup) View.inflate(this, R.layout.activity_main, null);
        setContentView(inflate);


        final ExpandTextView detailsDesc = (ExpandTextView) findViewById(R.id.details_desc);
        TextView tv = (TextView) findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsDesc.setText(text);
            }
        });



        //initText();

//        RelativeLayout activityMain = (RelativeLayout) findViewById(R.id.activity_main);
//        FlowLayout flowLayout = new FlowLayout(this);
//        inflate.addView(flowLayout);
//        for(int i=0;i<40;i++){
//            TextView textView = new TextView(this);
//            textView.setText("发哈嘎嘎"+i * i);
//            textView.setBackgroundColor(Color.CYAN);
//            textView.setTextSize(18);
//            textView.setGravity(Gravity.CENTER);
//            textView.setPadding(6,6,6,6);
//            flowLayout.addView(textView);
//        }

    }

    private void initText() {
        TextView tv = (TextView) findViewById(R.id.tv);

        String text = String.format("￥%1$s  市场价:￥%2$s",18.6,22);
        int z = text.lastIndexOf("市");
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.setSpan(new AbsoluteSizeSpan(dp2px(this,12)),0,1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);//字号
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")),0,1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);//颜色
        style.setSpan(new AbsoluteSizeSpan(dp2px(this,16)),1,z, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);//字号
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")),1,z, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);//颜色
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#afafaf")),z,text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);//颜色
        style.setSpan(new AbsoluteSizeSpan(dp2px(this,14)),z,text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);//字号
        tv.setText(style);
    }

    private int dp2px(Context context,int dp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.getResources().getDisplayMetrics());
    }
}
