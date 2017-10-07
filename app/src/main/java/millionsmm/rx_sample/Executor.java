package millionsmm.rx_sample;

/**
 * Created by Wilber
 * on 7/10/2017.
 */

public interface Executor<Work extends Runnable> {
    void execute(Work work);

    void shutdown();

    void addWorks(Work work);

    void removeWorks(int num);

    int getWorkSize();
}
