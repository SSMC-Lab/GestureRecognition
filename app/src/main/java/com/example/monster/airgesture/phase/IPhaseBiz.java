package com.example.monster.airgesture.phase;

/**
 * 手势解析模块接口
 */

public interface IPhaseBiz {
    /**
     * 回传手势数据
     */
    interface PhaseListener{
        void receiveActionType(float type);
    }
    void startRecognition(PhaseListener listener);
    void stopRecognition();
}