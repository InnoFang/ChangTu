<div align="center">

<img src="https://github.com/InnoFang/ChangTu/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png?raw=true" height="100px" width="100px"/>

<h1> ChangTu (畅途) </h1>

<p>Changtu -- this is a service class application software that for the poor areas which have bad traffic safety,the crowd which have lower safety awareness and the people which go out to an unfamiliar place</p>

<p>“畅途” ———— 是一款结合针对交通安全较差的地区、安全意识较低人群以及面向出门远行人生地不熟而开发的一款服务类应用软件。</p>

<a href="https://github.com/InnoFang/ChangTu/blob/master/apk/changtu.apk?raw=true">点击下载</a>


<br />

<h2> Co-worker (合作者) </h2>

 <a href="https://github.com/DreamYHD">DreamYHD</a>

<br />

<h2 Feature (功能)</h2>

   <table border="1px solid green" style="border-collapse: collapse" cellpadding="15">
        <tr>
            <th width="100px">功能名称</th>
            <td align="center"><strong>功能描述</strong></td>
        </tr>
        <tr>
            <th>注册功能</th>
            <td align="center">用户通过注册功能可以拥有自己的账号密码，之后通过自己的信息，进入应用。</td>
        </tr>
        <tr>
            <th>用户位置实时显示</th>
            <td align="center">1) 方式一：创建行程，开始记录
用户通过选择指定联系人，乘坐交通工具，位置信息显示时间间隔，确认后开始记录并以列表的形式显示用户当前位置，时间及距离上一个地点的距离。<br/>
2) 方式二：
用户在主界面可以点击”快速定位”，应用将自动为用户以当前点为起点开始记录，并以列表的形式显示用户当前位置，时间及距离上一个地点的距离。
</td>
        </tr>
        <tr>
            <th>快速播报</th>
            <td align="center">用户通过点击该功能，应用将会将用户的行程开始位置，出发时间，乘坐交通工具，当前位置，以及播报原因发送给用户指定的联系人。</td>
        </tr>
        <tr>
            <th>拍照记录</th>
            <td align="center">当用户开始了行程之后，用户可以点击该功能按钮，可以拍照将，诸如计程车车牌等信息，以图片的形式记录下来，并上传到云端，方便查看。</td>
        </tr>
        <tr>
            <th>足迹记录</th>
            <td align="center">用户的所有行程记录都会保存在这里面，用户可以点击查看相应时间点的明细信息，包括用户行程起始位置，出发时间，行程结束时间，全程距离，备注信息，用户全程时间点的位置信息以及拍照记录的照片等信息。</td>
        </tr>
        <tr>
            <th>轨迹</th>
            <td align="center">当用户点开该界面后，应用自动开始将用户当前位置为起始点开始绘制用户的轨迹，如果用户需要停止，可以点击该界面的“结束轨迹”按钮停止绘制</td>
        </tr>
        <tr>
            <th>地图共享</th>
            <td align="center">首先用户要打开在界面工具栏的滑动按钮，共享自己的位置，然后点击右上角的联系人，选择指定联系人来与自己共享位置，同时，用户也可以查看到对方的位置信息；如果用户不想共享位置，可以关闭滑动按钮，则指定联系人就无法继续获取用户的位置信息。</td>
        </tr>
        <tr>
            <th>紧急播报</th>
            <td align="center">用户需要指定自己信任的联系人，当用户在行程中遇到了紧急情况，无法拨打电话时，用户通过快速点击四下电源键，应用将会自动将用户位置信息发送给指定联系人。</td>
        </tr>
        <tr>
            <th>附近</th>
            <td align="center">用户可以选择切换地点查看某个想去的陌生地方的信息(信息由当地畅途用户提供)，便于用户出门远行，了解当地风气。</td>
        </tr>
    </table>

<br />

<h2>Third-party libraries used (第三方库使用)</h2>

```
implementation 'com.android.support:appcompat-v7:27.1.1'
implementation 'com.android.support:support-v4:27.1.1'
implementation 'com.android.support:design:27.1.1'
implementation 'com.android.support:recyclerview-v7:27.1.1'
implementation 'com.android.support:cardview-v7:27.1.1'
implementation 'cn.bmob.android:bmob-sdk:3.5.1'
implementation 'cn.bmob.android:bmob-push:0.8'
implementation 'cn.bmob.android:bmob-im:2.0.5@aar'
implementation 'com.getbase:floatingactionbutton:1.10.1'
implementation 'com.alibaba:fastjson:1.1.46.android'
implementation 'cn.qqtheme.framework:WheelPicker:1.4.3'
implementation 'com.kyleduo.switchbutton:library:1.4.4'

implementation files('libs/BaiduTraceSDK_v2_1_12.jar')
implementation files('libs/BaiduLBS_Android.jar')
```

<h2>Screenshot (截图)</h2>

<img src="https://github.com/InnoFang/ChangTu/blob/master/picture/ppt_%E4%B8%BB%E7%95%8C%E9%9D%A2.png?raw=true"/><img src="https://github.com/InnoFang/ChangTu/blob/master/picture/ppt_%E5%BC%80%E5%A7%8B%E8%A1%8C%E7%A8%8B.png?raw=true"/>

<img src="https://github.com/InnoFang/ChangTu/blob/master/picture/ppt_%E4%BD%8D%E7%BD%AE%E5%85%B1%E4%BA%AB_1.png?raw=true"/><img src="https://github.com/InnoFang/ChangTu/blob/master/picture/ppt_%E5%85%B1%E4%BA%AB%E4%BD%8D%E7%BD%AE_2.png?raw=true"/>
<img src="https://github.com/InnoFang/ChangTu/blob/master/picture/ppt_%E6%92%AD%E6%8A%A5%E8%87%AA%E5%AE%9A%E4%B9%89%E6%A8%A1%E6%9D%BF.png?raw=true"/><img src="https://github.com/InnoFang/ChangTu/blob/master/picture/ppt_%E7%B4%A7%E6%80%A5%E6%B1%82%E5%8A%A9.png?raw=true"/>
<img src="https://github.com/InnoFang/ChangTu/blob/master/picture/ppt_%E8%B6%B3%E8%BF%B9%E8%AE%B0%E5%BD%95.png?raw=true"/><img src="https://github.com/InnoFang/ChangTu/blob/master/picture/ppt_%E8%BD%A8%E8%BF%B9%E6%98%BE%E7%A4%BA.png?raw=true"/>
<img src="https://github.com/InnoFang/ChangTu/blob/master/picture/ppt_%E9%99%84%E8%BF%91%E5%88%97%E8%A1%A8.png?raw=true"/><img src="https://github.com/InnoFang/ChangTu/blob/master/picture/ppt_%E9%99%84%E8%BF%91%E8%AF%84%E8%AE%BA.png?raw=true"/>


<h1><a href="https://github.com/InnoFang/ChangTu/blob/master/LICENSE">License</a></h1>
 
 </div> 

```
  ChangTu  Copyright (C) 2016  InnoFang
  This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
  This is free software, and you are welcome to redistribute it
  under certain conditions; type `show c' for details.
```
