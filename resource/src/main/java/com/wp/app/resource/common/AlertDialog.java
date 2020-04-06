package com.wp.app.resource.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.wp.app.resource.R;
import com.wp.app.resource.basic.BasicApp;

public class AlertDialog extends Dialog {

    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;

    /**
     * 否定
     */
    private String negativeText;
    private View.OnClickListener negativeClickListener;
    /**
     * 肯定
     */
    private String positiveText;
    private View.OnClickListener positiveClickListener;
    private View rootView;
    private TextView tvTitle;
    private TextView tvContent;

    public AlertDialog(Context context) {
        super(context, R.style.DialogTransparentTheme);
    }

    /**
     * 设置标题
     *
     * @param title 标题
     * @return 窗口实例
     */
    public AlertDialog setTitleText(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设置标题
     *
     * @param titleResId 标题ResId
     * @return 窗口实例
     */
    public AlertDialog setTitleResId(int titleResId) {
        this.title = getContext().getString(titleResId);
        return this;
    }

    /**
     * 设置内容
     *
     * @param content 内容
     * @return 窗口实例
     */
    public AlertDialog setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * 设置内容
     *
     * @param contentResId 内容ResId
     * @return 窗口实例
     */
    public AlertDialog setContent(int contentResId) {
        this.content = getContext().getString(contentResId);
        return this;
    }

    /**
     * 设置否定监听
     *
     * @param negativeText 否定文本
     * @param listener     否定监听器
     * @return 窗口实例
     */
    public AlertDialog setNegativeClickListener(String negativeText, View.OnClickListener listener) {
        this.negativeText = negativeText;
        this.negativeClickListener = listener;
        return this;
    }

    /**
     * 设置否定监听
     *
     * @param negativeResId 否定文本ResId
     * @param listener      否定监听器
     * @return 窗口实例
     */
    public AlertDialog setNegativeClickListener(int negativeResId, View.OnClickListener listener) {
        this.negativeText = getContext().getString(negativeResId);
        this.negativeClickListener = listener;
        return this;
    }

    /**
     * 设置肯定监听
     *
     * @param positiveText 肯定文本
     * @param listener     肯定监听器
     * @return 窗口实例
     */
    public AlertDialog setPositiveClickListener(String positiveText, View.OnClickListener listener) {
        this.positiveText = positiveText;
        this.positiveClickListener = listener;
        return this;
    }

    /**
     * 设置肯定监听
     *
     * @param positiveResId 肯定文本ResId
     * @param listener      肯定监听器
     * @return 窗口实例
     */
    public AlertDialog setPositiveClickListener(int positiveResId, View.OnClickListener listener) {
        this.positiveText = getContext().getString(positiveResId);
        this.positiveClickListener = listener;
        return this;
    }

    public AlertDialog setCanDismissOnTouchOutside(boolean b) {
        setCanceledOnTouchOutside(b);
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_alert,
                null, false);
        setContentView(rootView);

        this.tvTitle = rootView.findViewById(R.id.tvTitle);
        this.tvContent = rootView.findViewById(R.id.tvContent);
        this.tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
        this.tvContent.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);

        this.tvTitle.setText(title);
        this.tvContent.setText(content);

        observeNegative();
        observePositive();
    }

    /**
     * 否定操作
     */
    private void observeNegative() {
        TextView tvCancel = this.rootView.findViewById(R.id.tvCancel);
        tvCancel.setVisibility(TextUtils.isEmpty(this.negativeText) ? View.GONE : View.VISIBLE);
        tvCancel.setText(this.negativeText);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (negativeClickListener != null) {
                    negativeClickListener.onClick(v);
                }
            }
        });
    }

    /**
     * 肯定操作
     */
    private void observePositive() {
        TextView tvConfirm = this.rootView.findViewById(R.id.tvConfirm);
        tvConfirm.setVisibility(TextUtils.isEmpty(this.positiveText) ? View.GONE : View.VISIBLE);
        tvConfirm.setText(this.positiveText);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (positiveClickListener != null) {
                    positiveClickListener.onClick(v);
                }
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        Window window = getWindow();
        if (window == null) {
            return;
        }

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int) (BasicApp.getScreenWidth() * 0.8);
        window.setAttributes(layoutParams);
    }
}
