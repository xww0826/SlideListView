package listview.example.x.slidelistview;

/**
 * @Created by xww.
 * @Creation time 2018/8/21.
 */

public class ContactEntity {
    private int mPhoto;
    private String mName;
    private String mMessage;

    ContactEntity(int photo, String name, String message) {
        this.mPhoto = photo;
        this.mName = name;
        this.mMessage = message;
    }

    public int getmPhoto() {
        return mPhoto;
    }

    public String getmName() {
        return mName;
    }

    public String getmMessage() {
        return mMessage;
    }
}
