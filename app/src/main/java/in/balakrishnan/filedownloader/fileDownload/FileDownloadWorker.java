package in.balakrishnan.filedownloader.fileDownload;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import in.balakrishnan.filedownloader.ImageHelper.FilenameUtils;
import in.balakrishnan.filedownloader.MainActivity;
import in.balakrishnan.filedownloader.Storage.LocalData;

/**
 * Created by BalaKrishnan
 */
public class FileDownloadWorker extends Worker {
    static final String KEY_WORkER_TOTAL = "key_worker_total";
    static final String KEY_WORKER_CURRENT = "key_worker_current";
    static final String KEY_WORKER_FILE_URL = "key_worker_input_url";
    static final String KEY_WORKER_BUCKET = "key_worker_id";
    private static final String TAG = "FileDownloadWorker";
    private WorkerParameters workerParameters;
    private LocalData localData;
    private int currentProcessingImage;
    private int totalCount;


    public FileDownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        workerParameters = workerParams;
        localData = new LocalData(context);
    }


    @NonNull
    @Override
    public Result doWork() {
        try {
            // These are used for uploading the image
            String url = workerParameters.getInputData().getString(KEY_WORKER_FILE_URL);
            String bucket = workerParameters.getInputData().getString(KEY_WORKER_BUCKET);
            int total = workerParameters.getInputData().getInt(KEY_WORkER_TOTAL, 0);
            int current = workerParameters.getInputData().getInt(KEY_WORKER_CURRENT, 0);
            currentProcessingImage = workerParameters.getInputData().getInt(KEY_WORKER_CURRENT, 0);
            totalCount = workerParameters.getInputData().getInt(KEY_WORkER_TOTAL, 0);
            FileDownloadNetworkHelper.DownloadProgressListener listener = new FileDownloadNetworkHelper.DownloadProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    Download download = new Download();
                    download.setTotalFileSize(contentLength);
                    download.setCurrentFileSize(bytesRead);
                    int progress = (int) ((bytesRead * 100) / contentLength);
                    download.setProgress(progress);
                    download.setUrl(url);
                    Log.d(TAG, "update: " + progress);
                    if (MainActivity.downloadMediatorLiveData != null) {
                        MainActivity.downloadMediatorLiveData.postValue(download);
                    } else {
                        //Post Notification
                    }
                }
            };

            Result result = new FileDownloadNetworkHelper(getApplicationContext(), "https://file-examples.com/", listener).downloadFile(url, guessFileName(url), bucket);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
    }

    String guessFileName(String imagePath) {
        return FilenameUtils.getName(imagePath);
    }
}
