package com.wmlive.hhvideo.heihei.message;

import com.wmlive.hhvideo.common.manager.TaskManager;
import com.wmlive.hhvideo.common.manager.message.BaseTask;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.utils.KLog;

import java.util.concurrent.BlockingQueue;

/**
 * Created by lsq on 2/2/2018.2:37 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class MessageOperator {
    private BlockingQueue<MessageDetail> blockingQueue;
    private volatile boolean running;

    public MessageOperator(BlockingQueue<MessageDetail> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    public void addMessage(MessageDetail task) {
        blockingQueue.add(task);
    }

    public void start() {
        if (!running) {
            running = true;
            TaskManager.get().executeTask(new MessageRunnable());
        }
    }

    public void stop() {
        running = false;
    }

    public void shutdown() {
        running = false;
        if (blockingQueue != null) {
            blockingQueue.clear();
        }
    }

    public MessageDetail pollMessage() {
        if (blockingQueue != null) {
            return blockingQueue.poll();
        }
        return null;
    }

    private class MessageRunnable extends BaseTask {

        @Override
        public void run() {
            KLog.i("消息处理已启动");
            try {
                while (running && !Thread.currentThread().isInterrupted()) {
                    Thread.sleep(2000);
                    MessageDetail task = blockingQueue.take();
                    KLog.i("execute Task: " + task.toString() + " thread name is :" + Thread.currentThread().getName() + " blockingQueue size is " + blockingQueue.size());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                KLog.i("execute InterruptedException: ");
            }
            KLog.i("消息处理已结束");
        }
    }
}
