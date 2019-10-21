package in.balakrishnan.filedownloader.fileDownload;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by BalaKrishnan
 */
public interface VideoDownloadInterface {
    // Retrofit 2 GET request for rxjava
    @Streaming
    @GET
    Flowable<ResponseBody> downloadFileByUrlRx(@Url String fileUrl);

}
