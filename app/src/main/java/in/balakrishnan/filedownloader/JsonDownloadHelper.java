package in.balakrishnan.filedownloader;

import android.annotation.SuppressLint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

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

/**
 * Created by BalaKrishnan
 */
public class JsonDownloadHelper {
    Retrofit retrofit;

    public JsonDownloadHelper() {
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://ashik-nfn.s3.amazonaws.com/")
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @SuppressLint("CheckResult")
    public void downloadJson(String url, FutureRunnable runnable) {
        retrofit.create(APIInterface.class)
                .downloadFileByUrlRx(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<ResponseBody, File>() {
                         @Override
                         public File apply(ResponseBody responseBody) throws Exception {
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
                        runnable.setFile(file);
                        runnable.run();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public interface APIInterface {
        // Retrofit 2 GET request for rxjava
        @Streaming
        @GET
        Single<ResponseBody> downloadFileByUrlRx(@Url String fileUrl);

    }
}
