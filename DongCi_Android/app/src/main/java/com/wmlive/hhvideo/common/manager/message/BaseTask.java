package com.wmlive.hhvideo.common.manager.message;

import android.support.annotation.NonNull;

/**
 * Created by lsq on 2/2/2018.3:32 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public abstract class BaseTask implements Runnable, Comparable<BaseTask> {
    public int priority = 5;

    public BaseTask() {
    }

    public BaseTask(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(@NonNull BaseTask o) {
        if (this == o) {
            return 0;
        }
        if (this.priority > o.priority) {
            return -1;
        } else if (this.priority < o.priority) {
            return 1;
        } else {
            return 0;
        }
    }
}
