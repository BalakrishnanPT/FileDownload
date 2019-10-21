package in.balakrishnan.filedownloader.fileDownload;

import android.content.Context;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;

/**
 * Created by BalaKrishnan
 */
public class FileDownloadHelper {
    private static final String TAG = "FileDownloadHelper";
    Context context;

    public FileDownloadHelper(Context context, ArrayList<String> strings, String monument) {
        this.context = context;
        Log.d(TAG, "FileDownloadHelper: ");
        Constraints.Builder constraintsBuilder = new Constraints.Builder();
        constraintsBuilder.setRequiredNetworkType(NetworkType.CONNECTED);
        String time = "" + System.currentTimeMillis();
        WorkManager instance = WorkManager.getInstance(context);
        for (int i = 0; i < strings.size(); i++) {
            OneTimeWorkRequest.Builder builder = new OneTimeWorkRequest.Builder(FileDownloadWorker.class)
                    .setConstraints(constraintsBuilder.build())
                    .setInputData(getModel(strings.get(i), monument, strings.size(), i));
            instance.beginUniqueWork(time + monument, ExistingWorkPolicy.APPEND, builder.build()).enqueue();

        }
    }

    public Data getModel(String url, String name, int total, int current) {
        return new Data.Builder()
                .putString(FileDownloadWorker.KEY_WORKER_FILE_URL, url)
                .putString(FileDownloadWorker.KEY_WORKER_BUCKET, name)
                .putInt(FileDownloadWorker.KEY_WORkER_TOTAL, total)
                .putInt(FileDownloadWorker.KEY_WORKER_CURRENT, current)
                .build();
    }
}
