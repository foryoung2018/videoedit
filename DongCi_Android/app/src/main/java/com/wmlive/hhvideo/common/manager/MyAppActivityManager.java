package com.wmlive.hhvideo.common.manager;

import android.app.Activity;

import java.util.Stack;

/**
 * activity 管理器
 * Created by kangzhen on 2017/6/3.
 */

public class MyAppActivityManager {
    /**
     * 接收activity的Stack
     */
    private Stack<Activity> activityStack = null;

    private MyAppActivityManager() {
    }

    private static class Holder {
        final static MyAppActivityManager HOLDER = new MyAppActivityManager();
    }

    public static MyAppActivityManager getInstance() {
        return Holder.HOLDER;
    }

    public void clear() {
        if (activityStack != null) {
            activityStack.clear();
        }
    }

    /**
     * 将activity移出栈
     *
     * @param activity
     */
    public void popActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 结束指定activity
     *
     * @param activity
     */
    public void endActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            if (!activity.isDestroyed() && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 获得当前的activity(即最上层)
     *
     * @return
     */
    public Activity currentActivity() {
        Activity activity = null;
        if (!activityStack.empty())
            activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 将activity推入栈内
     *
     * @param activity
     */
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 弹出除cls外的所有activity
     *
     * @param cls
     */
    public void popAllActivityExceptOne(Class<? extends Activity> cls) {
        Activity push = null;
        while (!activityStack.empty()) {
            Activity activity = activityStack.pop();
            if (activity.getClass().equals(cls)) {
                push = activity;
            }
        }
        activityStack.push(push);
    }

    /**
     * 结束除cls之外的所有activity,执行结果都会清空Stack
     *
     * @param cls
     */
    public void finishAllActivityExceptOne(Class<? extends Activity> cls) {
        Activity push = null;
        while (!activityStack.empty()) {
            Activity activity = activityStack.pop();
            if(activity==null)
                continue;
            if (activity.getClass().equals(cls)) {
                push = activity;
            } else {
                activity.finish();
            }
        }
        activityStack.push(push);
    }

    /**
     * 结束除cls之外的所有activity,执行结果都会清空Stack
     *
     * @param cls
     */
    public void finishAllActivityBefore(Class<? extends Activity> cls) {
        while (!activityStack.empty()) {
            Activity activity = currentActivity();
            if (!activity.getClass().equals(cls)) {
                endActivity(activity);
            } else {
                break;
            }
        }
    }

    /**
     * 结束所有activity
     */
    public void finishAllActivity() {
        while (!activityStack.empty()) {
            Activity activity = currentActivity();
            endActivity(activity);
        }
    }

    /**
     * 队列是否为空
     *
     * @return
     */
    public boolean isEmptyActivits() {
        if (activityStack != null) {
            return activityStack.empty();
        } else {
            return true;
        }
    }
}
