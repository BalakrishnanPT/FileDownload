package in.balakrishnan.filedownloader.fileDownload;

import android.os.Parcel;
import android.os.Parcelable;

public class Download implements Parcelable {

    public static final Creator<Download> CREATOR = new Creator<Download>() {
        @Override
        public Download createFromParcel(Parcel source) {
            return new Download(source);
        }

        @Override
        public Download[] newArray(int size) {
            return new Download[size];
        }
    };

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public int getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(int currentFile) {
        this.currentFile = currentFile;
    }

    private int progress;
    private long currentFileSize;
    private long totalFileSize;
    private int totalFiles;
    private int currentFile;
    private String url;

    public Download() {
    }

    protected Download(Parcel in) {
        this.progress = in.readInt();
        this.totalFiles = in.readInt();
        this.currentFile = in.readInt();
        this.currentFileSize = in.readLong();
        this.totalFileSize = in.readLong();
        this.url = in.readString();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getCurrentFileSize() {
        return currentFileSize;
    }

    public void setCurrentFileSize(long currentFileSize) {
        this.currentFileSize = currentFileSize;
    }

    public long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.progress);
        dest.writeInt(this.totalFiles);
        dest.writeInt(this.currentFile);
        dest.writeLong(this.currentFileSize);
        dest.writeLong(this.totalFileSize);
        dest.writeString(this.url);
    }
}