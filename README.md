# DownloaderM

A Download Manager using multiple connection and multiple buffers to optimize the process of downloading a file. 

[Latest Release](https://github.com/MaanooAk/DownloaderM/releases/latest)

![Screenshot](/assets/dm.2.png)

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
When the downloading is stopped or interrupted a recovery file (`.dmrecovery`) is created. It can be used to resume the download even after the termination of the program. On the successful download the recovery file is deleted.

## Interface
* Pause/Resume/Cancel downloading
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

## License

All files in this repository are Copyright (c) 2014-2017 DownloaderM author list.

Code in this repository is licensed under the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html).

Data in this repository is licensed under the
[Creative Commons Attribution-ShareAlike 4.0 International License](http://creativecommons.org/licenses/by-sa/4.0/).

DownloaderM author list can be determined via `git shortlog -sne`.

