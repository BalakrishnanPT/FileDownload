package in.balakrishnan.filedownloader;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MediatorLiveData;

import java.io.File;

import in.balakrishnan.filedownloader.Storage.LocalData;
import in.balakrishnan.filedownloader.fileDownload.Download;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static MediatorLiveData<Download> downloadMediatorLiveData = new MediatorLiveData<>();
    LocalData localData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sampleImageDownloadCall();
    }

    //Initial call
    private void sampleImageDownloadCall() {
        String url = "https://ashik-nfn.s3.amazonaws.com/01b8okhyr3ewms-config.json";
        JsonDownloadHelper jsonDownloadHelper = new JsonDownloadHelper();
        jsonDownloadHelper.downloadJson(url, new FileRunnable());
    }

    class FileRunnable extends FutureRunnable<File> {
        //Write what you want to do with file
        @Override
        public void task(File result) {
            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
            result.delete();
        }
    }

}
