package in.balakrishnan.filedownloader;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.io.File;
import java.util.ArrayList;

import in.balakrishnan.filedownloader.ImageHelper.FileLocalCache;
import in.balakrishnan.filedownloader.ImageHelper.FilenameUtils;
import in.balakrishnan.filedownloader.Storage.LocalData;
import in.balakrishnan.filedownloader.fileDownload.Download;
import in.balakrishnan.filedownloader.fileDownload.FileDownloadHelper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static MediatorLiveData<Download> downloadMediatorLiveData = new MediatorLiveData<>();
    LocalData localData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        localData = new LocalData(this);
        downloadMediatorLiveData.observe(this, new Observer<Download>() {
            @Override
            public void onChanged(Download download) {
                Log.d(TAG, String.format("onChanged: %s %d", FilenameUtils.getName(download.getUrl()), download.getProgress()));
                if (download.getProgress() == 100) {
                    FileLocalCache fileLocalCache = new FileLocalCache(MainActivity.this, FileLocalCache.StorageType.CONTEXT_WRAPPER);
                    File test = fileLocalCache.getFile("test", "", FilenameUtils.getName(download.getUrl()));
                    Log.d(TAG, "onChanged: final " + test.getName());
                }

            }
        });
        sampleImageDownloadCall();
    }

    /**
     * A method to download the multiple imaages check
     */
    private void sampleImageDownloadCall() {
        ArrayList<String> images = new ArrayList<>();
        images.add("https://assets.nfnlabs.design/wallpapers/inch_5_5/1550087015.png");
        images.add("https://assets.nfnlabs.design/wallpapers/inch_5_5/1550086978.png");
        images.add("https://assets.nfnlabs.design/wallpapers/inch_5_5/1550086957.png");
//        images.add("https://assets.nfnlabs.design/wallpapers/inch_5_5/1550086930.png");
//        images.add("https://assets.nfnlabs.design/wallpapers/inch_5_5/1550086888.png");
//        images.add("https://assets.nfnlabs.design/wallpapers/inch_5_5/1550086860.png");
//        images.add("https://assets.nfnlabs.design/wallpapers/inch_5_5/1550086829.png");
//        images.add("https://assets.nfnlabs.design/wallpapers/inch_5_5/1550086038.png");
//        images.add("https://images.pexels.com/photos/2304805/pexels-photo-2304805.jpeg");
//        images.add("https://images.pexels.com/photos/2255984/pexels-photo-2255984.jpeg");
        images.add("https://file-examples.com/wp-content/uploads/2017/04/file_example_MP4_1920_18MG.mp4");

        Log.d(TAG, "onCreate: in job creation");
        new FileDownloadHelper(this, images, "test");

    }

}
