package in.balakrishnan.filedownloader;

public abstract class FutureRunnable<T> implements Runnable {

    T result;

    public void setFile(T result) {
        this.result = result;
    }

    @Override
    public void run() {
        if (result != null) {
            task(result);
        }
    }

    public abstract void task(T result);
}