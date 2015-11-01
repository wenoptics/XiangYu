package tk.wenop.XiangYu.bean;

import cn.bmob.v3.BmobObject;

    public class CommentEntity extends BmobObject {
        private String comment;
        private User toUser;
        private User ownerUser;
        private MessageEntity ownerMessage;
        private Boolean anonymous;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
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

        public Boolean getAnonymous() {
            return anonymous;
        }

        public void setAnonymous(Boolean anonymous) {
            this.anonymous = anonymous;
        }

    }