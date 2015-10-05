# hmp4android
HakodateMapPlus 4 Android 4.x+ built with Android Studio

## Install
```
$ git clone https://github.com/hakodatemapp/hmp4android.git
```
GitHubのアカウントでログインする必要があります．

## System Requirements

 * Android Studio
  * Eclipseではダメです．
 * Android SDK (API 14)
 * Android端末 **実機**
  * Android Emulatorでも動きますが，**設定がつらいです．**

## Caution

プロジェクトで使うリソース(画像など…)のファイル名には **半角の小文字英数字とアンダースコア(_)のみとし、ドットを含めないでください．**  
ビルド時に次のエラーが発生する原因となります．

```
Error:Execution failed for task ':app:mergeDebugResources'.
> /Users/yoshida/hmp4android/app/src/main/res/drawable-hdpi/miru_1.2.png: Error: '.' is not a valid file-based resource name character: File-based resource names must contain only lowercase a-z, 0-9, or underscore
```

 * わるい例: miru_1.2.png
 * よい例: miru_12.png
 
## See also

Androidアプリケーション開発関連の資料 - はこだてMap+班 - 高度ICT PBL 共用Redmineサイト
[http://ictpbl.per.c.fun.ac.jp/redmine/projects/tourism2015mapplus/wiki/Android%E3%82%A2%E3%83%97%E3%83%AA%E3%82%B1%E3%83%BC%E3%82%B7%E3%83%A7%E3%83%B3%E9%96%8B%E7%99%BA%E9%96%A2%E9%80%A3%E3%81%AE%E8%B3%87%E6%96%99
](http://ictpbl.per.c.fun.ac.jp/redmine/projects/tourism2015mapplus/wiki/Android%E3%82%A2%E3%83%97%E3%83%AA%E3%82%B1%E3%83%BC%E3%82%B7%E3%83%A7%E3%83%B3%E9%96%8B%E7%99%BA%E9%96%A2%E9%80%A3%E3%81%AE%E8%B3%87%E6%96%99
)