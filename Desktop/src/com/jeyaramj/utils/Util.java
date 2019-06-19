package com.jeyaramj.utils;

import java.io.File;

public class Util {

    public static boolean deleteFile(File file) {
        try {
            if (file.isDirectory()) {
                boolean deletionSuccess = true;
                for (File f : file.listFiles()) {
                    if (!deleteFile(f)) deletionSuccess = false;
                }
                return (deletionSuccess) && file.delete();
            } else {
                return file.delete();
            }
        } catch (SecurityException se) {
            System.out.println("Delete File Failed:" + se.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Delete File Failed:" + e.getMessage());
            return false;
        }
    }

}
