package org.suirui.huijian.tv.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import org.suirui.huijian.tv.R;

public class SettingsCategory extends LinearLayout {
 private boolean mbShowTopDivider = true;
 private boolean mbShowBottomDivider = true;
 private boolean mbShowCenterDivider = true;
 private Drawable mDrawableTopDivider = null;
 private Drawable mDrawableCenterDivider = null;
 private Drawable mDrawableBottomDivider = null;
 private Drawable mBackground = null;
 private int mBackgroundColor = 0;
 private boolean mHasBackground = false;
 private int mTopDividerHeight = 1;
 private int mCenterDividerHeight = 1;
 private int mBottomDividerHeight = 1;
 private int mMinItemHeight = 0;
 private int mResDrawableSettingsItemSelector;

 public SettingsCategory(Context context) {
     super(context);
     this.mResDrawableSettingsItemSelector = R.drawable.setting_option_item_no_line;//.setting_option_item_no_line;
     this.initView(context, (AttributeSet)null, 0);
 }

 public SettingsCategory(Context context, AttributeSet attrs) {
     super(context, attrs);
     this.mResDrawableSettingsItemSelector = R.drawable.setting_option_item_no_line;
     this.initView(context, attrs, 0);
 }

 @SuppressLint({"NewApi"})
 public SettingsCategory(Context context, AttributeSet attrs, int defStyle) {
     super(context, attrs, defStyle);
     this.mResDrawableSettingsItemSelector = R.drawable.setting_option_item_no_line;
     this.initView(context, attrs, defStyle);
 }

 private void initView(Context context, AttributeSet attrs, int defStyle) {
     if(context != null) {
         int dividerHeight = -1;
         Theme theme = context.getTheme();
         TypedArray defAttrs = theme.obtainStyledAttributes(attrs, R.styleable.SettingsCategory, R.attr.settingsCategoryAppearance, 0);
         if(defAttrs != null) {
             this.mbShowTopDivider = defAttrs.getBoolean(R.styleable.SettingsCategory_showTopDivider, this.mbShowTopDivider);
             this.mbShowBottomDivider = defAttrs.getBoolean(R.styleable.SettingsCategory_showBottomDivider, this.mbShowBottomDivider);
             this.mbShowCenterDivider = defAttrs.getBoolean(R.styleable.SettingsCategory_showCenterDivider, this.mbShowCenterDivider);
             this.mResDrawableSettingsItemSelector = defAttrs.getResourceId(R.styleable.SettingsCategory_settingsItemSelector, this.mResDrawableSettingsItemSelector);
             this.mDrawableTopDivider = defAttrs.getDrawable(R.styleable.SettingsCategory_topDivider);
             this.mDrawableCenterDivider = defAttrs.getDrawable(R.styleable.SettingsCategory_centerDivider);
             this.mDrawableBottomDivider = defAttrs.getDrawable(R.styleable.SettingsCategory_bottomDivider);
             this.mMinItemHeight = defAttrs.getDimensionPixelSize(R.styleable.SettingsCategory_seetingsItemMinHeight, this.mMinItemHeight);
             if(defAttrs.hasValue(R.styleable.SettingsCategory_settingsCategoryBackground)) {
                 this.mHasBackground = true;
                 this.mBackground = defAttrs.getDrawable(R.styleable.SettingsCategory_settingsCategoryBackground);
                 this.mBackgroundColor = defAttrs.getColor(R.styleable.SettingsCategory_settingsCategoryBackground, this.mBackgroundColor);
             }

             dividerHeight = defAttrs.getDimensionPixelSize(R.styleable.SettingsCategory_dividerHeight, dividerHeight);
             defAttrs.recycle();
             defAttrs = null;
         }

         TypedArray a;
         Drawable drawable;
         if(defStyle > 0) {
             a = context.obtainStyledAttributes(attrs, R.styleable.SettingsCategory, defStyle, 0);
             if(a != null) {
                 this.mbShowTopDivider = a.getBoolean(R.styleable.SettingsCategory_showTopDivider, this.mbShowTopDivider);
                 this.mbShowBottomDivider = a.getBoolean(R.styleable.SettingsCategory_showBottomDivider, this.mbShowBottomDivider);
                 this.mbShowCenterDivider = a.getBoolean(R.styleable.SettingsCategory_showCenterDivider, this.mbShowCenterDivider);
                 this.mResDrawableSettingsItemSelector = a.getResourceId(R.styleable.SettingsCategory_settingsItemSelector, this.mResDrawableSettingsItemSelector);
                 this.mMinItemHeight = a.getDimensionPixelSize(R.styleable.SettingsCategory_seetingsItemMinHeight, this.mMinItemHeight);
                 if(a.hasValue(R.styleable.SettingsCategory_settingsCategoryBackground)) {
                     this.mHasBackground = true;
                     this.mBackground = a.getDrawable(R.styleable.SettingsCategory_settingsCategoryBackground);
                     this.mBackgroundColor = a.getColor(R.styleable.SettingsCategory_settingsCategoryBackground, this.mBackgroundColor);
                 }

                 dividerHeight = a.getDimensionPixelSize(R.styleable.SettingsCategory_dividerHeight, dividerHeight);
                 drawable = a.getDrawable(R.styleable.SettingsCategory_topDivider);
                 if(drawable != null) {
                     this.mDrawableTopDivider = drawable;
                 }

                 drawable = a.getDrawable(R.styleable.SettingsCategory_centerDivider);
                 if(drawable != null) {
                     this.mDrawableCenterDivider = drawable;
                 }

                 drawable = a.getDrawable(R.styleable.SettingsCategory_bottomDivider);
                 if(drawable != null) {
                     this.mDrawableBottomDivider = drawable;
                 }

                 a.recycle();
             }
         }

         if(attrs != null) {
             a = context.obtainStyledAttributes(attrs, R.styleable.SettingsCategory);
             this.mbShowTopDivider = a.getBoolean(R.styleable.SettingsCategory_showTopDivider, this.mbShowTopDivider);
             this.mbShowBottomDivider = a.getBoolean(R.styleable.SettingsCategory_showBottomDivider, this.mbShowBottomDivider);
             this.mbShowCenterDivider = a.getBoolean(R.styleable.SettingsCategory_showCenterDivider, this.mbShowCenterDivider);
             this.mResDrawableSettingsItemSelector = a.getResourceId(R.styleable.SettingsCategory_settingsItemSelector, this.mResDrawableSettingsItemSelector);
             this.mMinItemHeight = a.getDimensionPixelSize(R.styleable.SettingsCategory_seetingsItemMinHeight, this.mMinItemHeight);
             if(a.hasValue(R.styleable.SettingsCategory_settingsCategoryBackground)) {
                 this.mHasBackground = true;
                 this.mBackground = a.getDrawable(R.styleable.SettingsCategory_settingsCategoryBackground);
                 this.mBackgroundColor = a.getColor(R.styleable.SettingsCategory_settingsCategoryBackground, this.mBackgroundColor);
             }

             dividerHeight = a.getDimensionPixelSize(R.styleable.SettingsCategory_dividerHeight, dividerHeight);
             drawable = a.getDrawable(R.styleable.SettingsCategory_topDivider);
             if(drawable != null) {
                 this.mDrawableTopDivider = drawable;
             }

             drawable = a.getDrawable(R.styleable.SettingsCategory_centerDivider);
             if(drawable != null) {
                 this.mDrawableCenterDivider = drawable;
             }

             drawable = a.getDrawable(R.styleable.SettingsCategory_bottomDivider);
             if(drawable != null) {
                 this.mDrawableBottomDivider = drawable;
             }

             a.recycle();
         }

         if(this.mDrawableTopDivider == null) {
             this.mDrawableTopDivider = this.getResources().getDrawable(R.drawable.m_settings_top_divider);
         }

         if(this.mDrawableCenterDivider == null) {
             this.mDrawableCenterDivider = this.getResources().getDrawable(R.drawable.m_settings_center_divider);
         }

         if(this.mDrawableBottomDivider == null) {
             this.mDrawableBottomDivider = this.getResources().getDrawable(R.drawable.m_settings_bottom_divider);
         }

         if(dividerHeight == 0) {
             this.mbShowTopDivider = false;
             this.mbShowCenterDivider = false;
             this.mbShowBottomDivider = false;
         } else if(dividerHeight > 0) {
             this.mTopDividerHeight = dividerHeight;
             this.mCenterDividerHeight = dividerHeight;
             this.mBottomDividerHeight = dividerHeight;
         } else {
             if(this.mDrawableTopDivider != null) {
                 this.mTopDividerHeight = this.mDrawableTopDivider.getIntrinsicHeight();
             }

             if(this.mDrawableCenterDivider != null) {
                 this.mCenterDividerHeight = this.mDrawableCenterDivider.getIntrinsicHeight();
             }

             if(this.mDrawableBottomDivider != null) {
                 this.mBottomDividerHeight = this.mDrawableBottomDivider.getIntrinsicHeight();
             }
         }

         if(this.isInEditMode()) {
             if(this.mTopDividerHeight < 2 && this.mDrawableTopDivider != null) {
                 this.mTopDividerHeight = 2;
             }

             if(this.mCenterDividerHeight < 2 && this.mDrawableCenterDivider != null) {
                 this.mCenterDividerHeight = 2;
             }

             if(this.mBottomDividerHeight < 2 && this.mDrawableBottomDivider != null) {
                 this.mBottomDividerHeight = 2;
             }
         }

         if(this.mHasBackground) {
             if(this.mBackground != null) {
                 this.setBackgroundDrawable(this.mBackground);
             } else {
                 this.setBackgroundColor(this.mBackgroundColor);
             }

             this.setPadding(0, 0, 0, 0);
         }

         this.setWillNotDraw(false);
     }
 }

 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
     int itemCount = 0;
     int count = this.getChildCount();
     View lastChild = null;

     int paddingLeft;
     int paddingRight;
     int paddingBottom;
     for(paddingLeft = 0; paddingLeft < count; ++paddingLeft) {
         View paddingTop = this.getChildAt(paddingLeft);
         if(paddingTop.getVisibility() == View.VISIBLE) {
             if(lastChild != null) {
                 paddingRight = lastChild.getPaddingLeft();
                 paddingBottom = lastChild.getPaddingTop();
                 int params = lastChild.getPaddingRight();
                 int paddingBottom1 = lastChild.getPaddingBottom();
                 lastChild.setBackgroundResource(this.mResDrawableSettingsItemSelector);
                 lastChild.setPadding(paddingRight, paddingBottom, params, paddingBottom1);
                 LayoutParams params1 = (LayoutParams)lastChild.getLayoutParams();
                 if(itemCount == 1 && this.mbShowTopDivider && this.mDrawableTopDivider != null) {
                     params1.topMargin = this.mTopDividerHeight;
                 } else if(itemCount > 1 && this.mbShowCenterDivider && this.mDrawableCenterDivider != null) {
                     params1.topMargin = this.mCenterDividerHeight;
                 } else {
                     params1.topMargin = 0;
                 }

                 lastChild.setLayoutParams(params1);
                 lastChild.setMinimumHeight(this.mMinItemHeight);
             }

             ++itemCount;
             lastChild = paddingTop;
         }
     }

     if(lastChild != null) {
         paddingLeft = lastChild.getPaddingLeft();
         int var13 = lastChild.getPaddingTop();
         paddingRight = lastChild.getPaddingRight();
         paddingBottom = lastChild.getPaddingBottom();
         lastChild.setBackgroundResource(this.mResDrawableSettingsItemSelector);
         lastChild.setPadding(paddingLeft, var13, paddingRight, paddingBottom);
         LayoutParams var14 = (LayoutParams)lastChild.getLayoutParams();
         if(itemCount == 1 && this.mbShowTopDivider && this.mDrawableTopDivider != null) {
             var14.topMargin = this.mTopDividerHeight;
         } else if(itemCount > 1 && this.mbShowCenterDivider && this.mDrawableCenterDivider != null) {
             var14.topMargin = this.mCenterDividerHeight;
         } else {
             var14.topMargin = 0;
         }

         if(this.mbShowBottomDivider && this.mDrawableBottomDivider != null) {
             var14.bottomMargin = this.mBottomDividerHeight;
         } else {
             var14.bottomMargin = 0;
         }

         lastChild.setLayoutParams(var14);
         lastChild.setMinimumHeight(this.mMinItemHeight);
     }

     super.onMeasure(widthMeasureSpec, heightMeasureSpec);
 }

 protected void onDraw(Canvas canvas) {
     super.onDraw(canvas);
     int itemCount = 0;
     int count = this.getChildCount();
     View lastChild = null;

     int b;
     int t;
     for(int params = 0; params < count; ++params) {
         View l = this.getChildAt(params);
         if(l.getVisibility() == View.VISIBLE) {
             if(lastChild != null) {
                 LayoutParams r = (LayoutParams)lastChild.getLayoutParams();
                 b = lastChild.getLeft();
                 t = lastChild.getRight();
                 int b1 = lastChild.getTop();
                 int t1 = b1 - r.topMargin;
                 if(itemCount == 1 && this.mbShowTopDivider) {
                     this.drawTopDivider(canvas, b, t1, t, b1);
                 } else if(itemCount > 1 && this.mbShowCenterDivider) {
                     this.drawCenterDivider(canvas, b, t1, t, b1);
                 }
             }

             ++itemCount;
             lastChild = l;
         }
     }

     if(lastChild != null) {
         LayoutParams var12 = (LayoutParams)lastChild.getLayoutParams();
         int var13 = lastChild.getLeft();
         int var14 = lastChild.getRight();
         b = lastChild.getTop();
         t = b - var12.topMargin;
         if(itemCount == 1 && this.mbShowTopDivider) {
             this.drawTopDivider(canvas, var13, t, var14, b);
         } else if(itemCount > 1 && this.mbShowCenterDivider) {
             this.drawCenterDivider(canvas, var13, t, var14, b);
         }

         if(this.mbShowBottomDivider) {
             t = lastChild.getBottom();
             b = t + var12.bottomMargin;
             this.drawBottomDivider(canvas, var13, t, var14, b);
         }
     }

 }

 private void drawTopDivider(Canvas canvas, int left, int top, int right, int bottom) {
     if(this.mDrawableTopDivider != null) {
         this.mDrawableTopDivider.setBounds(left, top, right, bottom);
         this.mDrawableTopDivider.draw(canvas);
     }

 }

 private void drawCenterDivider(Canvas canvas, int left, int top, int right, int bottom) {
     if(this.mDrawableCenterDivider != null) {
         this.mDrawableCenterDivider.setBounds(left, top, right, bottom);
         this.mDrawableCenterDivider.draw(canvas);
     }

 }

 private void drawBottomDivider(Canvas canvas, int left, int top, int right, int bottom) {
     if(this.mDrawableBottomDivider != null) {
         this.mDrawableBottomDivider.setBounds(left, top, right, bottom);
         this.mDrawableBottomDivider.draw(canvas);
     }

 }
}
