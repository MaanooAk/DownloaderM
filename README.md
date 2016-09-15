# DownloaderM
A Download Manager using multiple connection and multiple buffers to optimise the process of downloading a file. 

# Features
* Multiple connections (multithreading)
* Multiple buffering (for each connection)
* Fail recovery (continue downloading after connection error)
* Pause/Resume downloading 
* HTTP Redirects

# Interface
* Pause/Resume/Cancel donwloading
* Progress bars for each connection
* Settings menu

# Usage as library
Core files are in [com.maanoo.downloaderm.core](/src/com/maanoo/downloaderm/core)

```java
DownloadThreadGroup dtg = DownloadEngine.download(url, file);
dtg.start();
```

```java
DownloadThreadGroup dtg = DownloadEngine.download(url, file);

dtg.getStatus().addListener(new DownloadStatus.Listener() {

  @Override
  public void onStateChange(DownloadStatus.State state) {
    
  }

  @Override
  public void onPauseChange(int id, boolean paused) {
    
  }

  @Override
  public void onSubDone(int id) {
    
  }
  
});

dtg.start();
```
