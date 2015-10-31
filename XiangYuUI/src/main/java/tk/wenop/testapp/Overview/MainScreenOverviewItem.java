package tk.wenop.testapp.Overview;

/**
 * Created by wenop_000 on 2015/10/27.
 */
public class MainScreenOverviewItem {

    public enum gender_enum {
        MALE,
        FEMALE
    }
    public enum item_direction_enum {
        SENT,
        RECEIVED
    }
    public enum item_content_type_enum {
        PHOTO,
        AUDIO,
        PHOTO_wITH_AUDIO,
    }

    public String mNickName;
    public String mAudioTimeSec;
    public String mAvatar;
    public String mContentPhoto;
    public String mLocation;
    public int mCommentCount;
    public String mTime;

    // 这条是不是匿名消息
    public boolean isAnonymous;
    public item_direction_enum mItemDirection;
    public item_content_type_enum mContentType;
    // 性别
    public gender_enum mGender;


    // TODO   这些field用setter和getter

}
