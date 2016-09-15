# DownloaderM
A Download Manager using multiple connection and multiple buffers to optimise the process of downloading a file. 

[Latest Release](https://github.com/MaanooAk/DownloaderM/releases/latest)

## Features
* Multiple connections (multithreading)
* Multiple buffering (for each connection)
* Fail recovery (continue downloading after connection error)
* Pause/Resume downloading 
* HTTP Redirects

### Multiple connections
Splits the file to be downloaded in multiple parts and opens one connection for each of them. All connections write parallel to the same file.

### Multiple buffering
If number of buffers is set to greater than 1 then each connection uses a multiple buffering logic. 
While one thread downloads data to one buffer, another thread writes data to the disk from an other buffer.

### Recovery
When the downloading is stoped or interrupted a recovery file (`.dmrecovery`) is created. It can be used to resume the donwload even after the termination of the program. On the successful download the recovery file is deleted.

## Interface
* Pause/Resume/Cancel donwloading
* Progress bars for each connection
* Settings menu

## Usage as library
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

```java
DownloadThreadGroup dtg = DownloadEngine.recovery(file);
dtg.start();
```
