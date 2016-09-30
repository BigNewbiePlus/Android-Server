# Android-Server


  当时使用的是一个免费，轻量级，开源的嵌入式HTTP服务器，NanoHTTPD。就一个java文件，代码量很小，你可以看作Android版的tomcat，不过更小，几乎不占用资源。<br><br>
遇到的问题：<br>
  问题1 ： 下载路径好像不支持中文。如果支持中文，你可能需要在NanoHTTPD中进行中文的解码（在自己app中编码）。<br>
  问题2 ： 支持MP4在线播放，如果其他，你可能需要在NanoHTTPD修改。<br><br>
上面遇到的问题都是当时版本情况，现在版本情况不知。<br><br>

NanoHTTPD开源网站： https://github.com/NanoHttpd/nanohttpd 我浏览了一遍，工程文件很多，但其实核心文件就NanoHTTPD.java文件。<br>
本人修改的文件见：   NanoHTTPD.java  供参考。<br>
下载和在线播放示例文件见：localfile_private.java 143行<br><br>
上面2个文件都是真实项目中的文件，原工程都是能够执行的。
