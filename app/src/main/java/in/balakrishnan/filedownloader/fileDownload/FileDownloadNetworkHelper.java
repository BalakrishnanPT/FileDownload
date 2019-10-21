package in.balakrishnan.filedownloader.fileDownload;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import in.balakrishnan.filedownloader.ImageHelper.FileLocalCache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by BalaKrishnan
 */
public class FileDownloadNetworkHelper {
    private static final String TAG = "DownloadAPI";
    private static final int DEFAULT_TIMEOUT = 15;
    public Retrofit retrofit;
    Context context;


    public FileDownloadNetworkHelper(Context context, String baseUrl, DownloadProgressListener listener) {
        this.context = context;
        DownloadProgressInterceptor interceptor = new DownloadProgressInterceptor(listener);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public ListenableWorker.Result downloadFile(@NonNull String url,String fileName,String bucketName) {
        Log.d(TAG, "downloadFile: " + url);
        ResponseBody responseBody = retrofit.create(VideoDownloadInterface.class)
                .downloadFileByUrlRx(url)
                .blockingLast();
        FileLocalCache fileLocalCache = new FileLocalCache(context, FileLocalCache.StorageType.CONTEXT_WRAPPER);
        File file = fileLocalCache.createFile(bucketName, "", fileName);
        try {
            DataInputStream stream = new DataInputStream(responseBody.byteStream());
            byte[] buffer = new byte[(int) responseBody.contentLength()];
            stream.readFully(buffer);
            stream.close();
            DataOutputStream fos = null;
            fos = new DataOutputStream(new FileOutputStream(file));
            fos.write(buffer);
            fos.flush();
            fos.close();
            return ListenableWorker.Result.success();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ListenableWorker.Result.retry();
        } catch (IOException e) {
            e.printStackTrace();
            return ListenableWorker.Result.retry();
        }
    }

    public interface DownloadProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }

    public class DownloadProgressInterceptor implements Interceptor {

        private DownloadProgressListener listener;

        public DownloadProgressInterceptor(DownloadProgressListener listener) {
            Log.d(TAG, "DownloadProgressInterceptor: ");
            this.listener = listener;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());

            return originalResponse.newBuilder()
                    .body(new DownloadProgressResponseBody(originalResponse.body(), listener))
                    .build();
        }
    }

    public class DownloadProgressResponseBody extends ResponseBody {

        private ResponseBody responseBody;
        private DownloadProgressListener progressListener;
        private BufferedSource bufferedSource;

        public DownloadProgressResponseBody(ResponseBody responseBody,
                                            DownloadProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                    if (null != progressListener) {
                        progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    }
                    return bytesRead;
                }
            };

        }
    }
}
