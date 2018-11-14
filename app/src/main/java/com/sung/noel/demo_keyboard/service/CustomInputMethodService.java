package com.sung.noel.demo_keyboard.service;

import android.app.Service;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Vibrator;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import com.sung.noel.demo_keyboard.R;
import com.sung.noel.demo_keyboard.util.SharedPreferenceUtil;
import com.sung.noel.demo_keyboard.util.TextToSpeechUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;




public class CustomInputMethodService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    //鍵盤
    public static final int KEY_BOARD_CHANGE = 1000;
    //英文
    public static final int KEY_BOARD_ENGLISH = 1001;
    //符號
    public static final int KEY_BOARD_SYMBOL = 1002;
    //符號
    public static final int KEY_BOARD_NUMBERS = 1003;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({KEY_BOARD_CHANGE, KEY_BOARD_SYMBOL, KEY_BOARD_ENGLISH, KEY_BOARD_NUMBERS})
    public @interface KeyboardEvent {
    }

    private int currentBoard = KEY_BOARD_ENGLISH;

    private final int VIBRATE_TIME = 10;
    private Vibrator vibrator;
    private KeyboardView keyboardView;
    private Keyboard englishKeyboard;
    private Keyboard symbolKeyboard;
    private Keyboard numberKeyboard;
    private TextToSpeechUtil textToSpeechUtil;
    private SharedPreferenceUtil sharedPreferenceUtil;

    public CustomInputMethodService() {
        super();
    }

    @Override
    public View onCreateInputView() {
        vibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.view_keyboard, null);
        sharedPreferenceUtil = new SharedPreferenceUtil(this, SharedPreferenceUtil._NAME_USER_DEFAULT);
        textToSpeechUtil = new TextToSpeechUtil(this);
        initKeyboards();

        keyboardView.setKeyboard(englishKeyboard);
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    //-------------

    /***
     * 初始化 鍵盤模組
     */
    private void initKeyboards() {
        englishKeyboard = new Keyboard(this, R.xml.keys_board_english);
        symbolKeyboard = new Keyboard(this, R.xml.keys_board_symbol);
        numberKeyboard = new Keyboard(this,R.xml.keys_board_numbers);
    }
    //-------------

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    //------
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            switch (primaryCode) {
                //文字清除鍵
                case Keyboard.KEYCODE_DELETE:
                    CharSequence selectedText = inputConnection.getSelectedText(0);

                    if (TextUtils.isEmpty(selectedText)) {
                        inputConnection.deleteSurroundingText(1, 0);
                    } else {
                        inputConnection.commitText("", 1);
                    }
                    break;
                //大小寫切換
                case Keyboard.KEYCODE_SHIFT:
                    Keyboard.Key currentKey = englishKeyboard.getKeys().get(englishKeyboard.getShiftKeyIndex());
                    currentKey.icon = getResources().getDrawable(englishKeyboard.isShifted() ? R.drawable.ic_caps_off : R.drawable.ic_caps_on);
                    englishKeyboard.setShifted(!englishKeyboard.isShifted());
                    keyboardView.invalidateAllKeys();
                    break;
                //換行
                case Keyboard.KEYCODE_DONE:
                    inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                    break;
                //切換英文鍵盤
                case KEY_BOARD_ENGLISH:
                    currentBoard = primaryCode;
                    keyboardView.setKeyboard(englishKeyboard);
                    break;
                //切換符號鍵盤
                case KEY_BOARD_SYMBOL:
                    currentBoard = primaryCode;
                    keyboardView.setKeyboard(symbolKeyboard);
                    break;
                //數字
                case KEY_BOARD_NUMBERS:
                    currentBoard = primaryCode;
                    keyboardView.setKeyboard(numberKeyboard);
                    break;
                //更改鍵盤
                case KEY_BOARD_CHANGE:
                    currentBoard++;
                    if (currentBoard > KEY_BOARD_NUMBERS) {
                        currentBoard = KEY_BOARD_ENGLISH;
                    }
                    onKey(currentBoard, keyCodes);
                    break;
                //其他鍵盤
                default:
                    char code = (char) primaryCode;
                    if (Character.isLetter(code) && englishKeyboard.isShifted()) {
                        code = Character.toUpperCase(code);
                    }
                    inputConnection.commitText(String.valueOf(code), 1);
                    break;
            }
            vibrator.vibrate(VIBRATE_TIME);
            if (sharedPreferenceUtil.isSpell()) {
                textToSpeechUtil.speak(((char) primaryCode) + "");
            }
        }
    }

    //-----------

    @Override
    public void onDestroy() {
        super.onDestroy();
        textToSpeechUtil.shutDown();
    }
}
