/*
package cgtz.com.cgwallet.utils.llutils;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import cgtz.com.cgwallet.R;

*/
/**
 * Created by chen on 2015-11-16.
 *//*

public class KeyboardUtil {
    private KeyboardView keyboardView;
    private Keyboard mKeybord;
    private EditText ed;
    public KeyboardUtil(Activity act, Context ctx, EditText edit) {
        this.ed = edit;
        mKeybord = new Keyboard(ctx, R.xml.sysmbols);
        keyboardView = (KeyboardView) act.findViewById(R.id.keyboard_view);
        keyboardView.setKeyboard(mKeybord);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(true);
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setOnKeyboardActionListener(listener);
    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {//对按键的监听，通过对隐藏的一个EditText的密码输入个数判断，去显示可见的EditText的显示状态
            Editable myPassWordText = ed.getText();
            int start = ed.getSelectionStart();//此处start一直会被重复的赋值，保证是当前最新的可输入初始位置
            if (primaryCode == Keyboard.KEYCODE_DELETE) {
                if (myPassWordText != null && myPassWordText.length() > 0) {
                    if (start > 0) {
                        myPassWordText.delete(start - 1, start);
                    }
                }
            }else if (primaryCode == 4896) {
                myPassWordText.clear();
            } else {
                myPassWordText.insert(start, Character.toString((char) primaryCode));
            }
        }
    };

    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }
}
*/
