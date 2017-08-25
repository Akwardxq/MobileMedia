// IAudioPlayerService.aidl
package com.kegy.mobilevideo;

interface IAudioPlayerService {

    /**
     * 播放指定index的音乐
     * @param index
     */
     void playByIndex(int index);

    /**
     * 开始
     */
    void start();

    /**
     * 暂停
     */
    void pause();

    /**
     * 停止
     */
    void stop();

    /**
     * 歌曲的名称
     * @return
     */
    String getAudioName();

    /**
     * 歌曲的作者
     * @return
     */
    String getArtist();

    /**
     * 获取当前位置
     * @return
     */
    int getCurrentPosition();

    /**
     * 获取歌曲的路径
     * @return
     */
    String getAudioPath();

    /**
     * 获取总时长
     * @return
     */
    int getDuration();

    /**
     * 播放下一个
     */
    void playNext();

    /**
     * 播放前一个
     */
    void playPrevious();

    /**
     * 设置播放模式
     */
    void setPlayMode(int playMode);

    /**
     * 获取播放模式
     * @return
     */
    int getPlayMode();

    /**
    *是否正在播放
    *@return
    */
    boolean isPlaying();

    /**
    *移动到指定位置
    *
    */
    void seekTo(int position);
}
