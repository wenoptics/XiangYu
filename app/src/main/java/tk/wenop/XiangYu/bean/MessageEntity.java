package tk.wenop.XiangYu.bean;

import cn.bmob.v3.BmobObject;


    public class MessageEntity extends BmobObject {
        private String image;
        private String audio;
        private User ownerUser;
        private AreaEntity ownerArea;


        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getAudio() {
            return audio;
        }

        public void setAudio(String audio) {
            this.audio = audio;
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