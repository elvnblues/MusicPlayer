在线网页音乐播放器 MusicPlayer
===========
功能需求
-----------
### 已经完成的功能需求
* 简易播放器
* 在线下载音乐
* 建sqlite存储多条数据
* 读取sqlite数据，用ListView显示数据
* 播放网络音乐，先检查是否已经存在，存在则播放，否则下载后再播放
* 点击未下载过的音乐，在当前选中的音乐ListItem 后有数字显示下载进度
* 音乐进度条
* 多种音乐播放模式（顺序播放、随机播放、单曲循环）

### 需要完成的功能
* ListView有字母搜索排序
* 随机模式修改为打乱后的音乐播放顺序
* 在音乐列表为空时提供两种音乐载入方案
     
 1) 到SD卡读取
     
 2) 到官网读取(即后期获取的JSON格式的音乐名+音乐下载地址)


已经发现需要修补的BUG
---------------
### 未解决的BUG
### 已解决的BUG
* 在运行MusicPlayer的时候自动播放音乐
      
>怀疑初始化调用了OnCompletionListener触发器


