package com.jeyaramj;

import java.io.File;
import java.util.Comparator;

class Model {

    public static class JJFile extends File {

        public static Comparator<JJFile> GroupIDComparator = new Comparator<JJFile>() {
            public int compare(JJFile jjf1, JJFile jjf2) {
                return jjf1.getGroupID().compareTo(jjf2.getGroupID());//ascending order
                //return jjf2.getGroupID().compareTo(jjf1.getGroupID());//descending order
            }
        };
        public static Comparator<JJFile> ModifiedDateComparator = new Comparator<JJFile>() {
            public int compare(JJFile jjf1, JJFile jjf2) {
                return Long.valueOf(jjf1.lastModified()).compareTo(jjf2.lastModified());
            }
        };
        private boolean toBeDeleted = false;
        private String key, groupID;
        private Type type;

        public JJFile(String file) {
            super(file);
            if (file == "-") {
                type = Type.DIVIDER;
            } else {
                type = Type.FILE;
            }
        }

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getGroupID() {
            return this.groupID;
        }

        public void setGroupID(String groupID) {
            this.groupID = groupID;
        }

        public boolean getToBeDeleted() {
            return this.toBeDeleted;
        }

        public void setToBeDeleted(boolean toBeDeleted) {
            this.toBeDeleted = toBeDeleted;
        }

        public String toCheckboxListItem() {
            return ((toBeDeleted) ? "[DEL]" : "") + ((type == Type.DIVIDER) ? "-" : Global.dateTimeFormat.format(lastModified()) + " | " + getAbsolutePath());
        }

        private enum Type {FILE, DIVIDER}

    }
}
