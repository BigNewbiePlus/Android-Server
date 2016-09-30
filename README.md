# Android-Server


当时使用的是一个免费，轻量级，开源的嵌入式HTTP服务器，NanoHTTPD。就一个java文件，代码量很小，你可以看作Android版的tomcat，不过更小，几乎不占用资源。
    
遇到的问题1 ： 下载路径好像不支持中文，如果支持中文，你可能需要在NanoHTTPD中进行中文的解码（在自己app中编码）。
     问题2 ： 支持MP4在线播放，如果其他，你可能需要在NanoHTTPD修改。
         
NanoHTTPD开源网站： https://github.com/NanoHttpd/nanohttpd
本人修改的文件见：   NanoHTTPD.java
下载和在线播放文件见：localfile_private.java 143行
