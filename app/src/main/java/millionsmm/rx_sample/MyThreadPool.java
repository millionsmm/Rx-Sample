package millionsmm.rx_sample;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Wilber
 * on 7/10/2017.
 */

public class MyThreadPool<Work extends Runnable> implements Executor<Work> {

    private final HashSet<Worker> workers = new HashSet<>();
    private final List<Thread> threadList = new ArrayList<>();
    private volatile boolean RUNNING = true;
    private BlockingQueue<Work> queue = null;
    private int poolSize;//工作线程数
    private int coreSize;//核心线程数
    private boolean shutDown = false;

    public MyThreadPool(int poolSize) {
        this.poolSize = poolSize;
        queue = new LinkedBlockingQueue<>(poolSize);
    }

    @Override
    public void execute(Work work) {
        if (work == null) throw new NullPointerException("work is null");
        if (poolSize > coreSize) {
            addWorks(work);
        } else {
            try {
                queue.put(work);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void shutdown() {
        RUNNING = false;
        if (!workers.isEmpty()) {
            for (Worker worker : workers) {
                worker.interruptAllThread();
            }
        }
        shutDown = true;
        Thread.currentThread().interrupt();
    }

    @Override
    public void addWorks(Work work) {
        coreSize++;
        Worker worker = new Worker(work);
        workers.add(worker);
        Thread t = new Thread(worker);
        threadList.add(t);
        t.start();
    }

    @Override
    public void removeWorks(int num) {

    }

    @Override
    public int getWorkSize() {
        return workers.size();
    }

    private class Worker implements Runnable {
        Worker(Work work) {
            queue.offer(work);
        }

        @Override
        public void run() {
            while (RUNNING) {
                if (shutDown) {
                    Thread.interrupted();
                }
                Work task;
                try {
                    task = getTask();
                    task.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public Work getTask() throws InterruptedException {
            return queue.take();
        }

        public void interruptAllThread() {
            for (Thread thread : threadList) {
                thread.interrupt();
            }
        }
    }
}
