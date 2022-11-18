package com.spartan.dc.delayed;

import com.spartan.dc.delayed.entity.QueryPayResultEntity;

import java.util.concurrent.DelayQueue;

public class DelayQueueHelper<T> {

    private volatile static DelayQueueHelper delayQueueHelper = null;

    private DelayQueue<DelayedTask<T>> queue = new DelayQueue<>();

    private DelayQueueHelper() {
    }

    public static DelayQueueHelper getInstance() {
        if(delayQueueHelper == null) {
            synchronized(DelayQueueHelper.class) {
                delayQueueHelper = new DelayQueueHelper();
            }
        }
        return delayQueueHelper;
    }

    public void addTask(DelayedTask<T> task) {
        queue.put(task);
    }

    /*public void removeTask(DelayedTask task) {
        if(task == null){
            return;
        }
        for(Iterator<DelayedTask> iterator = queue.iterator(); iterator.hasNext();) {
            if(task instanceof QueryPayResultEntity) {
                QueryPayResultEntity clue = (QueryPayResultEntity) task;
                QueryPayResultEntity queueObj = (QueryPayResultEntity) iterator.next();
                if(clue.getId().equals(queueObj.getId())){
                    queue.remove(queueObj);
                }
            }
        }
    }*/

    public DelayQueue<DelayedTask<T>> getQueue() {
        return queue;
    }

}
