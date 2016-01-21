package com.mudo.diyviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.Toast;

import com.mudo.mudoviews.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Mudo on 2016/1/21.
 */
public class MudoEditText extends EditText {


    private int integerLength;
    private int decimalsLength;
    private int allLength;
    private boolean is_normal;
    private boolean is_chinese;
    private boolean is_phoneNum;
    private boolean is_idCardNum;

    private final int TYPE_INTERLENGTH = 1;
    private final int TYPE_DECIMALLENGTH = 2;
    private final int TYPE_ALLLENGTH = 3;
    private final int TYPE_NORMAL = 4;
    private final int TYPE_CHINESE = 6;
    private final int TYPE_PHONENUM = 7;
    private final int TYPE_IDCARDNUM = 8;


    public MudoEditText(Context context) {
        super(context);
        initAttrs();
    }

    public MudoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        //整数长度
        integerLength = attrs.getAttributeIntValue("http://schemas.android.com/apk/com.mudo.mudoviews", "integerLength", -1);
        //小数长度
        decimalsLength = attrs.getAttributeIntValue("http://schemas.android.com/apk/com.mudo.mudoviews", "decimalsLength", -1);
        //所有长度
        allLength = attrs.getAttributeIntValue("http://schemas.android.com/apk/com.mudo.mudoviews", "allLength", -1);

        //是不是仅包含 字母、数字、中文
        is_normal = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/com.mudo.mudoviews", "isNormal", false);
        //是不是仅包含 中文
        is_chinese = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/com.mudo.mudoviews", "isChinese", false);
        //是不是 电话号码
        is_phoneNum = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/com.mudo.mudoviews", "isPhoneNum", false);
        //是不是 身份证号码
        is_idCardNum = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/com.mudo.mudoviews", "isIdCardNum", false);

        initAttrs();
    }

    public MudoEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MudoEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs();
    }

    public void initAttrs() {

        if (integerLength != -1) {
            //设置了整数长度,注意前提为设置为numberdecinals
            this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); //设置输入type必须为整数或小数
            // this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);    //设置输入type必须为整数数字

            this.addTextChangedListener(new MudoTextWatcher(TYPE_INTERLENGTH, integerLength));
        }
        if (decimalsLength != -1) {
            //设置了小数长度,注意前提为设置为numberdecinals
            this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            this.addTextChangedListener(new MudoTextWatcher(TYPE_DECIMALLENGTH, decimalsLength));
        }
        if (allLength != -1) {
            //设置了全部输入长度
            this.addTextChangedListener(new MudoTextWatcher(TYPE_ALLLENGTH, allLength));
        }
        if (is_normal) {
            //限制 字母，数字，中文
            this.addTextChangedListener(new MudoTextWatcher(TYPE_NORMAL));
        }
        if (is_chinese) {
            //限制中文

            this.addTextChangedListener(new MudoTextWatcher(TYPE_CHINESE));
        }
        if (is_phoneNum) {
            //限制手机号码
            this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            this.addTextChangedListener(new MudoTextWatcher(TYPE_PHONENUM));
        }
        if (is_idCardNum) {
            //限制输入为身份证号码

            this.addTextChangedListener(new MudoTextWatcher(TYPE_IDCARDNUM));
        }
    }

    class MudoTextWatcher implements TextWatcher {

        private int mType = -1;
        private int mLength = -1;

        private int beforeStrLength;
        private int afterStrLength;
        private int mPosition;
        private int inputLength;

        public MudoTextWatcher(int type, int length) {
            this.mType = type;
            this.mLength = length;
        }

        public MudoTextWatcher(int type) {
            this.mType = type;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            System.out.println("befordTextChanged CharSequence:" + s.toString() + " start:" + start + " count:" + count + " after:" + after);
            //start表示输入框中，焦点所在的位置，即输入开始位置或删除开始位置
            //s.toString().length() 就是输入前的字符串长度
            beforeStrLength = s.toString().length();
            mPosition = start;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            System.out.println("onTextChanged CharSequence:" + s.toString() + " start:" + start + " before:" + before + " count:" + count);
            afterStrLength = s.toString().length();
            inputLength = count;
        }

        @Override
        public void afterTextChanged(Editable s) {

            if (afterStrLength > beforeStrLength) {
                //删除不判断，只有输入的时候进行判断

                switch (mType) {
                    case TYPE_INTERLENGTH:
                        if (getText().toString().contains(".")) {
                            //包含小数点的时候，浮点型数字
                            int pointPosition = getText().toString().indexOf(".");
                            if (pointPosition > mLength) {
                                //超过长度,删除最后输入的那个数字
                                Toast.makeText(getContext(), "整数位不能超过" + mLength + "位", Toast.LENGTH_SHORT).show();
                                s.delete(mPosition, mPosition + inputLength);
                            }

                        } else {
                            //不包含小数点，则全是数字
                            if (getText().toString().length() > mLength) {
                                //超过长度，删除最后输入的那个数字
                                Toast.makeText(getContext(), "整数位不能超过" + mLength + "位", Toast.LENGTH_SHORT).show();
                                s.delete(mPosition, mPosition + inputLength);
                            }

                        }

                        break;
                    case TYPE_DECIMALLENGTH:
                        if (getText().toString().contains(".")) {
                            //包含小数点的时候，浮点型数字
                            int pointPosition = getText().toString().indexOf(".");
                            if (s.toString().length() - pointPosition > mLength + 1) {
                                Toast.makeText(getContext(), "小数位不能超过" + mLength + "位", Toast.LENGTH_SHORT).show();
                                //超过长度,删除最后输入的那个数字
                                s.delete(mPosition, mPosition + inputLength);
                            }
                        }
                        break;
                    case TYPE_ALLLENGTH:

                        if (s.toString().length() > mLength) {
                            Toast.makeText(getContext(), "位数不能超过" + mLength + "位", Toast.LENGTH_SHORT).show();
                            //超过长度,删除最后输入的那个数字
                            s.delete(mPosition, mPosition + inputLength);
                        }

                        break;
                    case TYPE_NORMAL:

                        String NormalReg = "^[\\u4e00-\\u9fa5_a-zA-Z0-9]+$";
                        if (!s.toString().matches(NormalReg)) {
                            Toast.makeText(getContext(), "仅支持输入中文、数字、字母以及下划线", Toast.LENGTH_SHORT).show();
                            s.delete(mPosition, mPosition + inputLength);
                        }
                        break;
                    case TYPE_CHINESE:

                        String ChineseReg = "^[\\u4e00-\\u9fa5]+$";
                        if (!s.toString().matches(ChineseReg)) {
                            Toast.makeText(getContext(), "仅支持输入中文", Toast.LENGTH_SHORT).show();
                            s.delete(mPosition, mPosition + inputLength);
                        }
                        break;
                    case TYPE_PHONENUM:

                        String PhoneNumReg = "^1[3|4|5|7|8]\\d{9}$";
                        if (s.toString().length() > 11) {
                            s.delete(mPosition, mPosition + inputLength);
                        } else if (s.toString().length() == 11) {
                            if (!s.toString().matches(PhoneNumReg)) {
                                Toast.makeText(getContext(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case TYPE_IDCARDNUM:
                        String IdCardNumReg = "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$";
                        if (s.toString().length() == 18) {
                            if (!s.toString().matches(IdCardNumReg)) {
                                Toast.makeText(getContext(), "请输入正确的身份证号码", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;

                    default:
                        //其他未知类型

                        break;
                }
            }


        }
    }

}
