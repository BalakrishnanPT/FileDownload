package in.balakrishnan.filedownloader;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import in.balakrishnan.filedownloader.ImageHelper.FileLocalCache;
import in.balakrishnan.filedownloader.ImageHelper.FilenameUtils;
import in.balakrishnan.filedownloader.Storage.LocalData;
import in.balakrishnan.filedownloader.fileDownload.Download;
import in.balakrishnan.filedownloader.fileDownload.FileDownloadNetworkHelper;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

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
    @SuppressLint("CheckResult")
    private void sampleImageDownloadCall() {

        String url = "https://ashik-nfn.s3.amazonaws.com/01b8okhyr3ewms-config.json";

        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ashik-nfn.s3.amazonaws.com/")
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        retrofit.create(Interface.class)
                .downloadFileByUrlRx(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<ResponseBody, File>() {
                         @Override
                         public File apply(ResponseBody responseBody) throws Exception {
                             Log.d(TAG, "onSuccess: ");
                             try {
                                 File file = File.createTempFile("File", ".json");
                                 DataInputStream stream = new DataInputStream(responseBody.byteStream());
                                 byte[] buffer = new byte[(int) responseBody.contentLength()];
                                 stream.readFully(buffer);
                                 stream.close();
                                 DataOutputStream fos = null;
                                 fos = new DataOutputStream(new FileOutputStream(file));
                                 fos.write(buffer);
                                 fos.flush();
                                 fos.close();
                                 return file;
                             } catch (Exception e) {
                                 e.printStackTrace();
                             }
                             return null;
                         }
                     }
                ).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.d(TAG, "onSuccess: " + file.getPath());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public interface Interface {
        // Retrofit 2 GET request for rxjava
        @Streaming
        @GET
        Single<ResponseBody> downloadFileByUrlRx(@Url String fileUrl);

    }

}
