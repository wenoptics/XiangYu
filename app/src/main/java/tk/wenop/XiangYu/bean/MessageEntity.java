package tk.wenop.XiangYu.bean;

import com.bmob.BmobProFile;

import cn.bmob.v3.BmobObject;


    public class MessageEntity extends BmobObject {
        private BmobProFile image;
        private BmobProFile audio;
        private User ownerUser;
        private AreaEntity ownerArea;

        public void setImage(BmobProFile image) {
            this.image = image;
        }
        public BmobProFile getImage() {
            return image;
        }
        public void setAudio(BmobProFile audio) {
            this.audio = audio;
        }
        public BmobProFile getAudio() {
            return audio;
        }
        public void setOwnerUser(User ownerUser) {
            this.ownerUser = ownerUser;
        }
        public User getOwnerUser() {
            return ownerUser;
        }
        public void setOwnerArea(AreaEntity ownerArea) {
            this.ownerArea = ownerArea;
        }
        public AreaEntity getOwnerArea() {
            return ownerArea;
        }

    }