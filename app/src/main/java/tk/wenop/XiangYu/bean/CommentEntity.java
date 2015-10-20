package tk.wenop.XiangYu.bean;

import com.bmob.BmobProFile;

import cn.bmob.v3.BmobObject;

    public class CommentEntity extends BmobObject {
        private BmobProFile comment;
        private User toUser;
        private User ownerUser;
        private MessageEntity ownerMessage;

        public void setComment(BmobProFile comment) {
            this.comment = comment;
        }
        public BmobProFile getComment() {
            return comment;
        }
        public void setToUser(User toUser) {
            this.toUser = toUser;
        }
        public User getToUser() {
            return toUser;
        }
        public void setOwnerUser(User ownerUser) {
            this.ownerUser = ownerUser;
        }
        public User getOwnerUser() {
            return ownerUser;
        }
        public void setOwnerMessage(MessageEntity ownerMessage) {
            this.ownerMessage = ownerMessage;
        }
        public MessageEntity getOwnerMessage() {
            return ownerMessage;
        }

    }