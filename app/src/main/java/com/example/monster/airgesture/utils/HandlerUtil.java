package com.example.monster.airgesture.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * handler工具类
 * Created by WelkinShadow on 2017/11/19.
 */

public class HandlerUtil {

    public static class HandlerHolder extends Handler {
        WeakReference<OnReceiveMessageListener> mListenerWeakReference;

        /**
         * 使用HandlerHolder来创建Handler
         * 不要使用匿名类来实现接口以避免内存泄漏
         *
         * @param listener 收到消息回调接口
         */
        public HandlerHolder(OnReceiveMessageListener listener) {
            mListenerWeakReference = new WeakReference<>(listener);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mListenerWeakReference != null && mListenerWeakReference.get() != null) {
                mListenerWeakReference.get().handlerMessage(msg);
            }
        }
    }

    /**
     * 收到消息回调接口
     * 在activity或者activity的内部持有类实现该接口
     */
    public interface OnReceiveMessageListener {
        void handlerMessage(Message msg);
    }
}
