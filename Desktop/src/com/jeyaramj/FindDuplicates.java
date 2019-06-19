package com.jeyaramj;

import com.jeyaramj.Model.JJFile;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class FindDuplicates {

    private static List<JJFile> dupliateFileList = new ArrayList<JJFile>();
    private static MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("cannot initialize SHA-512 hash function", e);
        }
    }

    public FindDuplicates(String[] args) {
        String path = "", initialPath;
        if (args.length > 0) {
            initialPath = System.getProperty(args[0]);
        } else initialPath = "";
        JFileChooser fc = new JFileChooser(initialPath);
        // fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int result = fc.showOpenDialog(null);
        switch (result) {
            case JFileChooser.APPROVE_OPTION:
                File file = fc.getSelectedFile();

                path = fc.getCurrentDirectory().getAbsolutePath();
                //System.out.println( "path="+path+"\nfile name="+file.toString());
                break;
            case JFileChooser.CANCEL_OPTION:
                System.exit(0);
            case JFileChooser.ERROR_OPTION:
                System.exit(0);
                break;
        }

        if (path.length() < 1) {
            System.out.println("Please supply a directory path");
            return;
        }
        File dir = new File(path);
        if (!dir.isDirectory()) {
            System.out.println("Supplied directory does not exist.");
            return;
        }
        Map<String, List<String>> lists = new HashMap<String, List<String>>();
        FindDuplicates.findDuplicateFolders(lists, dir);
        PrintDuplicates(lists);

        //dupliateFileList.sort(Model.JJFile.ModifiedDateComparator);
        CheckboxListItem[] fileItemCheckBoxes = new CheckboxListItem[dupliateFileList.size()];
        int i = 0;
        for (JJFile jjf : dupliateFileList) {
            fileItemCheckBoxes[i++] = new CheckboxListItem(jjf.toCheckboxListItem());
        }
        if (fileItemCheckBoxes.length > 0) {
            GUI gui = new GUI();
            gui.addItems(fileItemCheckBoxes);
        }
    }

    public static String getUniqueFileHash(FileHashMethod fhm, File file) {
        switch (fhm) {
            case MD:
                try {
                    FileInputStream fileInput = new FileInputStream(file);
                    byte[] fileData = new byte[(int) file.length()];
                    fileInput.read(fileData);
                    fileInput.close();
                    return new BigInteger(1, messageDigest.digest(fileData)).toString(16);
                } catch (IOException e) {
                    throw new RuntimeException("cannot read file " + file.getAbsolutePath(), e);
                }
            case NAME_AND_SIZE:
                return file.length() + file.getName();
            default:
                return null;
        }

    }

    public static String getUniqueFolderHash(FileHashMethod fhm, File file) {
        if (file.isDirectory()) {
            String hashString = "";
            for (File dirChild : file.listFiles()) {
                if (dirChild.isDirectory()) {
                    hashString += getUniqueFolderHash(fhm, dirChild);
                } else {
                    hashString += getUniqueFileHash(fhm, dirChild);
                }
            }
            return hashString;
        } else {
            return getUniqueFileHash(fhm, file);
        }
    }

    public static void findDuplicateFolders(Map<String, List<String>> folderList, File directory) {
        for (File dirChild : directory.listFiles()) {
            if (dirChild.isDirectory()) {
                findDuplicateFolders(folderList, dirChild);
                String uniqueFileHash = getUniqueFolderHash(FileHashMethod.NAME_AND_SIZE, dirChild);
                List<String> identicalList = folderList.get(uniqueFileHash);
                if (identicalList == null) {
                    identicalList = new LinkedList<String>();
                }
                identicalList.add(dirChild.getAbsolutePath());
                folderList.put(uniqueFileHash, identicalList);
            }
        }
    }

    public static void findDuplicateFiles(Map<String, List<String>> filesList, File directory) {
        for (File dirChild : directory.listFiles()) {
            if (dirChild.isDirectory()) {
                findDuplicateFiles(filesList, dirChild);
            } else {
                String uniqueFileHash = getUniqueFileHash(FileHashMethod.NAME_AND_SIZE, dirChild);
                List<String> identicalList = filesList.get(uniqueFileHash);
                if (identicalList == null) {
                    identicalList = new LinkedList<String>();
                }
                identicalList.add(dirChild.getAbsolutePath());
                filesList.put(uniqueFileHash, identicalList);

            }
        }
    }

    public static void PrintDuplicates(Map<String, List<String>> lists) {
        for (String key : lists.keySet()) {
            List<String> list = lists.get(key);
            if (list.size() > 1) {
                //System.out.println("\n");
                dupliateFileList.add(new JJFile("-"));
                int j = 0;
                for (String file : list) {
                    JJFile jjf = new JJFile(file);
                    //if((jjf).list().length>0) {
                    //System.out.println(jjf.toCheckboxListItem());
                    jjf.setGroupID(key);
                    if (Global.deleteFile && ++j != list.size()) jjf.setToBeDeleted(true);
                    dupliateFileList.add(jjf);
                    //}
                }
            }
        }
    }

    private enum FolderType {NOT_SET, EMPTY, DUPLICATE}

    private enum FileHashMethod {MD, NAME_AND_SIZE}
}
    

