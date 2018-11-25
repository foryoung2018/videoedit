package com.wmlive.hhvideo.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 文件的压缩与解压
 * Created by admin on 2016/12/29.
 */

public class FileZipAndUnZip {
    /**
     * DeCompress the ZIP to the path
     *
     * @param zipFileString name of ZIP
     * @param outPathString path to be unZIP
     * @throws Exception
     */
    public static String UnZipFolder(String zipFileString, String outPathString) throws Exception {
        String strFileDirsPath = "";
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(outPathString + File.separator + szName);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
                strFileDirsPath = file.getParentFile().getPath();
            }
        }
        inZip.close();
        return strFileDirsPath;
    }

    /**
     * Compress file and folder
     *
     * @param srcFileString file or folder to be Compress
     * @param zipFileString the path name of result ZIP
     * @throws Exception
     */
    public static void ZipFolder(String srcFileString, String zipFileString) throws Exception {
        //create ZIP
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileString));
        //create the file
        File file = new File(srcFileString);
        //compress
        ZipFiles(file.getParent() + File.separator, file.getName(), outZip);
        //finish and close
        outZip.finish();
        outZip.close();
    }

    /**
     * compress files
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private static void ZipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam) throws Exception {
        if (zipOutputSteam == null)
            return;
        File file = new File(folderString + fileString);
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileString);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else {
            //folder
            String fileList[] = file.list();
            //no child file and compress
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }
            //child files and recursion
            for (int i = 0; i < fileList.length; i++) {
                ZipFiles(folderString, fileString + File.separator + fileList[i], zipOutputSteam);
            }//end of for
        }
    }

    /**
     * return the InputStream of file in the ZIP
     *
     * @param zipFileString name of ZIP
     * @param fileString    name of file in the ZIP
     * @return InputStream
     * @throws Exception
     */
    public static InputStream UpZip(String zipFileString, String fileString) throws Exception {
        ZipFile zipFile = new ZipFile(zipFileString);
        ZipEntry zipEntry = zipFile.getEntry(fileString);
        return zipFile.getInputStream(zipEntry);
    }

    /**
     * return files list(file and folder) in the ZIP
     *
     * @param zipFileString  ZIP name
     * @param bContainFolder contain folder or not
     * @param bContainFile   contain file or not
     * @return
     * @throws Exception
     */
    public static List<File> GetFileList(String zipFileString, boolean bContainFolder, boolean bContainFile) throws Exception {
        List<File> fileList = new ArrayList<File>();
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(szName);
                if (bContainFolder) {
                    fileList.add(folder);
                }

            } else {
                File file = new File(szName);
                if (bContainFile) {
                    fileList.add(file);
                }
            }
        }
        inZip.close();
        return fileList;
    }

    public static boolean unZipFile(String zipFilePath, String outDirPath) {
        boolean unzipOk = true;
        if (TextUtils.isEmpty(zipFilePath) || !new File(zipFilePath).exists()
                || TextUtils.isEmpty(outDirPath)) {
            return unzipOk;
        }
        KLog.i("======zipFilePath:" + zipFilePath + "\noutDirPath:" + outDirPath);
        InputStream fis = null;
        FileOutputStream fos;
        ZipInputStream zis = null;
        File unzipFile;
        byte[] buffer;
        int len;
        try {
            fis = new FileInputStream(zipFilePath);
            zis = new ZipInputStream(fis);
            ZipEntry zipEntry;
            String entryName;
            while ((zipEntry = zis.getNextEntry()) != null) {
                entryName = zipEntry.getName();
                KLog.i("=======entryName:" + entryName);
                if (zipEntry.isDirectory()) {
                    // get the folder name of the widget
                    entryName = entryName.substring(0, entryName.length() - 1);
                    KLog.i("=======entryName1:" + entryName);
                    File folder = new File(outDirPath + File.separator + entryName);
                    folder.mkdirs();
                } else {
                    unzipFile = new File(outDirPath + File.separator + entryName);
                    if (!unzipFile.getParentFile().exists()) {
                        unzipFile.getParentFile().mkdirs();
                    }
                    unzipFile.createNewFile();
                    fos = new FileOutputStream(unzipFile);
                    buffer = new byte[1024];
                    while ((len = zis.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        fos.flush();
                    }
                    fos.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            unzipOk = false;
            KLog.i("======unZipFile出错:" + e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return unzipOk;
    }

    /**
     * 获取zip文件中的所有文件名
     *
     * @param zipPath
     * @return
     */
    public static Map<String, List<String>> getZipFileList(String zipPath) {
        Map<String, List<String>> fileList = new HashMap<>(6);
        if (!TextUtils.isEmpty(zipPath)) {
            File zipFile = new File(zipPath);
            if (zipFile.exists() && zipFile.isFile() && zipFile.getName().endsWith(".zip")) {
                fileList.put("dir", new ArrayList<>(4));
                fileList.put("file", new ArrayList<>(4));
                InputStream fis = null;
                ZipInputStream zis = null;
                String fileName;
                try {
                    fis = new FileInputStream(zipPath);
                    zis = new ZipInputStream(fis);
                    ZipEntry zipEntry;
                    while ((zipEntry = zis.getNextEntry()) != null) {
                        fileName = zipEntry.getName();
                        if (!fileName.startsWith(".")) {//这里忽略隐藏文件
                            fileList.get(zipEntry.isDirectory() ? "dir" : "file").add(zipEntry.getName());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (zis != null) {
                        try {
                            zis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return fileList;
    }

    public static boolean zipFileMatch(String zipFilePath, String unzipFileDirPath) {
        if (TextUtils.isEmpty(zipFilePath) || !zipFilePath.endsWith(".zip") || TextUtils.isEmpty(unzipFileDirPath)) {
            return false;
        }
        File zipFile = new File(zipFilePath);
        File unzipDir = new File(unzipFileDirPath);
        if (!zipFile.exists() || zipFile.isDirectory() || !unzipDir.exists() || unzipDir.isFile()) {
            return false;
        }

        Map<String, List<String>> zipFileList = getZipFileList(zipFilePath);
        if (zipFileList.size() > 0) {
            List<String> unzipFilesName = FileUtil.listAllFileName(unzipFileDirPath, false);
            KLog.i("=====解压目录的文件路径:\n" + CommonUtils.printList(unzipFilesName));
            List<String> dirs = zipFileList.get("dir");
            if (dirs != null) {
                List<String> files = zipFileList.get("file");
                if (files != null) {
                    dirs.addAll(files);
                }
                KLog.i("=====zip文件目录:\n" + CommonUtils.printList(dirs));
                for (String fileName : dirs) {
                    boolean find = false;
                    for (String another : unzipFilesName) {
                        if (fileName.equals(another)) {
                            find = true;
                            break;
                        }
                    }
                    if (!find) {
                        KLog.i("=====没有找到匹配的文件");
                        return false;
                    }
                }
            }
            return true;
        } else {
            return true;
        }
    }
}
