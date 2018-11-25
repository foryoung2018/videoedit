package com.wmlive.hhvideo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * getExternalFilesDir()目录是SDCard/Android/data/应用包名/files/，长时间保存的数据，对应-清除数据
 * getExternalCacheDir()目录是SDCard/Android/data/应用包名/cache/，临时缓存数据，对应-清除缓存
 *
 * @Title: FileUtils.java
 */
public class FileUtil {

    /**
     * 删除制定路径下所有文件
     *
     * @param path
     * @param deleteSelf 是否删除文件夹
     */
    public static void deleteAll(String path, boolean deleteSelf) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                deleteAll(file, deleteSelf);
            }
        }
    }

    public static void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirWihtFile(dir);
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    /**
     * 删除制定路径下所有文件
     *
     * @param file
     * @param deleteSelf 是否删除文件夹
     */
    public static void deleteAll(File file, boolean deleteSelf) {
        if (file.isFile() || file.list().length == 0) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteAll(f, deleteSelf);
                if (deleteSelf) {
                    f.delete();
                }
            }
            if (deleteSelf) {
                file.delete();
            }
        }
    }

    /**
     * 遍历目录下所有的文件
     *
     * @param dirPath
     * @return
     */
    public static List<File> listAllFiles(String dirPath) {
        KLog.i("=====需要遍历的文件夹：" + dirPath);
        List<File> files = new LinkedList<>();
        if (!TextUtils.isEmpty(dirPath)) {
            return listAllFiles(new File(dirPath));
        }
        return files;
    }

    /**
     * 遍历目录下所有的文件
     *
     * @param dir
     * @return
     */
    public static List<File> listAllFiles(File dir) {
        List<File> files = new LinkedList<>();
        if (dir != null && dir.exists() && dir.isDirectory()) {
            File[] list = dir.listFiles();
            for (File file : list) {
                if (file.isDirectory()) {
                    files.addAll(listAllFiles(file));
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

    public static List<String> listAllFileName(String dirPath, boolean absolutePath) {
        if (!TextUtils.isEmpty(dirPath)) {
            return listAllFileName(dirPath, new File(dirPath), absolutePath);
        }
        return new ArrayList<>(1);
    }

    public static List<String> listAllFileName(String rootPath, File dir, boolean absolutePath) {
        List<String> fileNames = new ArrayList<>(10);
        if (dir != null && dir.exists() && dir.isDirectory()) {
            File[] list = dir.listFiles();
            String filePath;
            KLog.i("========rootPath:" + rootPath);
            for (File file : list) {
                filePath = file.getAbsolutePath();
                if (!absolutePath) {
                    filePath = filePath.substring(rootPath.length() + 1, filePath.length());
                }
                fileNames.add(filePath + (file.isDirectory() ? File.separator : ""));
                if (file.isDirectory()) {
                    fileNames.addAll(listAllFileName(rootPath, file, absolutePath));
                }
            }
        }
        return fileNames;
    }

    /**
     * SD卡是否存在
     *
     * @return
     */
    public static boolean isSDCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡剩余大小
     *
     * @return
     */
    public static long getSDFreeSize() {
        if (!isSDCardExist()) {
            return 0;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSize();
        long freeBlocks = sf.getAvailableBlocks();
        return (freeBlocks * blockSize) >> 20; //单位MB
    }

    /**
     * 获取SD卡大小
     *
     * @return
     */
    public static long getSDAllSize() {
        if (!isSDCardExist()) {
            return 0;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSize();
        long allBlocks = sf.getBlockCount();
        return (allBlocks * blockSize) >> 20; //单位MB
    }

    /**
     * 获取当前App的缓存目录
     *
     * @param context
     * @return
     */
    public static String getAppCacheDirectory(Context context) {
        return context.getExternalCacheDir() != null ? context.getExternalCacheDir().getAbsolutePath() : "cache";
    }

    /**
     * 获取当前App的缓存目录
     *
     * @param context
     * @return
     */
    public static String getAppFilesDirectory(Context context) {
        return context.getExternalFilesDir(null) != null ? context.getExternalFilesDir(null).getAbsolutePath() : "files";
    }


    /**
     * 创建文件夹
     *
     * @param filePath
     */
    public static boolean createDirectory(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            return createDirectory(new File(filePath));
        }
        return false;
    }

    public static boolean createDirectory(File dirFile) {
        if (dirFile.exists()) {
            if (dirFile.isDirectory()) {
                return true;
            } else {
                return dirFile.mkdirs();
            }
        }
        return dirFile.mkdirs();
    }


    /**
     * 通过递归调用删除一个文件夹及下面的所有文件
     *
     * @param file
     */
    public static void deleteFiles(File file) {
        try {
            if (!file.isDirectory()) {//是文件
                file.delete();
            } else {//是目录
                String[] childFilePaths = file.list();
                for (String childFilePath : childFilePaths) {
                    File childFile = new File(file.getAbsolutePath() + File.separator + childFilePath);
                    deleteFiles(childFile);
                }
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成时间戳文件名
     *
     * @return 20161113204454676
     */
    public static String getRandomName() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        StringBuilder builder = new StringBuilder();
        builder.append(cal.get(Calendar.YEAR));
        builder.append(cal.get(Calendar.MONTH) + 1);
        builder.append(cal.get(Calendar.DAY_OF_MONTH));
        builder.append(cal.get(Calendar.HOUR_OF_DAY));
        builder.append(cal.get(Calendar.MINUTE));
        builder.append(cal.get(Calendar.SECOND));
        builder.append(cal.get(Calendar.MILLISECOND));
        return builder.toString();
    }


    public static String getRandomImageName() {
        return "pic_" + getRandomName() + ".jpg";
    }

    /**
     * 根据视频的保存路径创建缩略图
     *
     * @param path
     * @return
     */
    public static String createVideoThumb(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
        int end = path.lastIndexOf(".");
        String thumbPath = path.substring(0, end) + "_thumb.jpg";
        File fThumb = new File(thumbPath);
        try {
            fThumb.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fThumb);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
        try {
            if (fos != null) {
                fos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return thumbPath;
    }


    /**
     * 获取音频文件的长度
     *
     * @param context
     * @param audioPath
     * @return
     */
    public static int getAudioDuring(Context context, String audioPath) {
        MediaPlayer mp = MediaPlayer.create(context, Uri.parse(audioPath));
        int duration = mp.getDuration() / 1000;
        mp.release();
        return duration;
    }

    /**
     * 获取目录文件或者文件夹的大小
     *
     * @param dir
     * @return
     */
    public static long getFileSize(File dir) {
        if (dir == null || !dir.exists()) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return dir.length();
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    dirSize += file.length();
                } else if (file.isDirectory()) {
                    dirSize += file.length();
                    dirSize += getFileSize(file); // 递归调用继续统计
                }
            }
        }
        return dirSize;
    }


    public static File writeFromInput(String fullPathName, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            file = new File(fullPathName);
            if (file.exists())
                return file;

            int length = 0;
            output = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return file;
    }


    /**
     * <Move file to destination Path>
     *
     * @param oldLocation
     * @param file
     * @throws IOException
     */
    public static void moveFile(File oldLocation, File file) throws IOException {
        boolean isMkdirs = false;
        if (!file.exists()) {
            isMkdirs = file.mkdirs();
            if (!isMkdirs)
                return;
        }
        String childs[] = oldLocation.list();
        if (oldLocation != null && oldLocation.exists()) {
            for (int i = 0; i < childs.length; i++) {
                String childName = childs[i];
                String childPath = oldLocation.getPath() + File.separator
                        + childName;
                File filePath = new File(childPath);
                if (filePath.exists() && filePath.isFile()) {
                    FileInputStream fis = null;
                    FileOutputStream fos = null;
                    try {
                        fis = new FileInputStream(filePath);
                        File fout = new File(file.getPath() + File.separator
                                + childName);
                        if (fout.exists()) {
                            fout.delete();
                        }
                        fos = new FileOutputStream(fout, false);

                        byte[] buff = new byte[4096];
                        int readIn;
                        while ((readIn = fis.read(buff, 0, buff.length)) != -1) {
                            fos.write(buff, 0, readIn);
                        }
                        fos.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (fis != null) {
                                fos.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    /**
     * <Save the App crash info to sdcard>
     */
    public static String saveCrashInfoToFile(String excepMsg, String path) {
        File logFile = null;
        if (TextUtils.isEmpty(excepMsg)) {
            return null;
        }
        String errorlog = path;
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            if (isSDCardExist()) {
                File file = new File(errorlog);
                if (!file.exists()) {
                    file.mkdirs();
                }

                if (!file.exists()) {
                    return null;
                }
            }

            StringBuilder logSb = new StringBuilder();
            logSb.append("crashlog");
            logSb.append("(");
            logSb.append(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(new Date()));
            logSb.append(")");
            logSb.append(".txt");
            logFile = new File(errorlog, logSb.toString());
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            fw = new FileWriter(logFile, true);
            pw = new PrintWriter(fw);
            pw.write(excepMsg);
//			ex.printStackTrace(pw);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.flush();
                pw.close();
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                }
            }
        }
        KLog.i("KKK", "Save File Exception ");
        return logFile.getAbsolutePath();
    }

    public static void writeFile(String data, File file) throws IOException {
        writeFile(data.getBytes(Charset.forName("UTF-8")), file);
    }

    public static void writeFile(byte[] data, File file) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file, false));
        bos.write(data);
        bos.close();
    }

    public static String readFileAsString(File file) throws IOException {
        int length = 0;
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte buffer[] = new byte[4 * 1024];
        while ((length = bis.read(buffer)) > 0) {
            bos.write(buffer, 0, length);
        }
        byte[] contents = bos.toByteArray();
        bis.close();
        bos.close();
        return new String(contents, Charset.forName("UTF-8"));
    }


    /**
     * 读取资源文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String readAssets(Context context, String fileName) {
        InputStream is = null;
        String content = null;
        try {
            is = context.getAssets().open(fileName);
            if (is != null) {

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = is.read(buffer);
                    if (readLength == -1) break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                is.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            content = null;
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 列出root目录下所有子目录
     *
     * @return 绝对路径
     */
    public static List<String> listPath(String root) {
        List<String> allDir = new ArrayList<String>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        // 过滤掉以.开始的文件夹
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                if (f.isDirectory() && !f.getName().startsWith(".")) {
                    allDir.add(f.getAbsolutePath());
                }
            }
        }
        return allDir;
    }

    /**
     * 转换文件大小
     *
     * @param fileSize
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileSize) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString;
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format(fileSize / 1024.0) + "KB";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format(fileSize / 1048576.0) + "MB";
        } else {
            fileSizeString = df.format(fileSize / 1073741824.0) + "G";
        }
        return fileSizeString;
    }

    /**
     * 通过NIO的map内存方式复制文件
     *
     * @param sourcePath
     * @param destPath
     */
    public static void nioMapCopy(String sourcePath, String destPath) {
        File source = new File(sourcePath);
        File dest = new File(destPath);
        FileChannel sourceCh = null;
        FileChannel destCh = null;
        try {
            if (!dest.exists()) {
                dest.createNewFile();
            }
            FileInputStream fis = new FileInputStream(source);
            FileOutputStream fos = new FileOutputStream(dest);
            sourceCh = fis.getChannel();
            destCh = fos.getChannel();
            MappedByteBuffer mbb = sourceCh.map(FileChannel.MapMode.READ_ONLY, 0, sourceCh.size());
            destCh.write(mbb);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(sourceCh);
            closeStream(destCh);
        }
    }

    /**
     * 使用NIO的transferFrom复制文件
     *
     * @param fromFile
     * @param toFileDir
     * @param toFile
     * @return
     */
    public static boolean copy(String fromFile, String toFileDir, String toFile) {
        if (TextUtils.isEmpty(fromFile) || TextUtils.isEmpty(toFileDir)) {
            return false;
        }
        File from = new File(fromFile);
        if (!from.exists()) {
            return false;
        }
        File dir = new File(toFileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        boolean success = true;
        try {
            long start = System.currentTimeMillis();
            FileChannel src = new FileInputStream(from).getChannel();
            FileChannel dst = new FileOutputStream(new File(toFileDir + File.separator + toFile)).getChannel();
            dst.transferFrom(src, 0, src.size());
            Log.i("copy", "copy spend time: " + (System.currentTimeMillis() - start));
        } catch (IOException e) {
            success = false;
            e.printStackTrace();
        }
        return success;
    }

    public static boolean copy(String fromFile, String toFile) {
        File from = new File(fromFile);
        if (!from.exists()) {
            return false;
        }

        boolean success = true;
        long start = System.currentTimeMillis();
        try {
            File dest = new File(toFile);
            if (!dest.exists()) {
                dest.createNewFile();
            }
            FileChannel src = new FileInputStream(from).getChannel();
            FileChannel dst = new FileOutputStream(dest).getChannel();
            dst.transferFrom(src, 0, src.size());
        } catch (IOException e) {
            success = false;
            KLog.e("======复制文件出错：" + e.getMessage());
        }
        KLog.i("======复制文件完成，耗时：" + (System.currentTimeMillis() - start));
        return success;
    }


    /**
     * 用传统IO缓冲流复制文件函数
     *
     * @param fromFile
     * @param toFile
     */
    public static void copyFile(File fromFile, File toFile) {
        FileInputStream input = null;
        BufferedInputStream inBuff = null;
        FileOutputStream output = null;
        BufferedOutputStream outBuff = null;
        try {
            input = new FileInputStream(fromFile);
            inBuff = new BufferedInputStream(input, 10240);
            output = new FileOutputStream(toFile);
            outBuff = new BufferedOutputStream(output, 10240);
            int len;
            long start = System.currentTimeMillis();
            while ((len = inBuff.read()) != -1) {
                outBuff.write(len);
            }
            outBuff.flush();
            Log.i("copy", "copy spend time: " + (System.currentTimeMillis() - start));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(outBuff);
            closeStream(output);
            closeStream(inBuff);
            closeStream(input);
        }
    }

    /**
     * 传统IO复制文件
     *
     * @param sourcePath
     * @param destPath
     */
    public static void traditionalCopy(String sourcePath, String destPath) {
        File source = new File(sourcePath);
        File dest = new File(destPath);
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            if (!dest.exists()) {
                dest.createNewFile();
            }
            fis = new FileInputStream(source);
            fos = new FileOutputStream(dest);
            byte[] buf = new byte[512];
            int len;
            while ((len = fis.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(fis);
            closeStream(fos);
        }
    }

    /**
     * 按行读取文件
     *
     * @param filePath
     */
    public static void readByLine(String filePath) {
        String encoding = "GBK";
        File file = new File(filePath);
        if (file.isFile() && file.exists()) { //判断文件是否存在
            InputStreamReader read = null;
            BufferedReader bufferedReader = null;
            try {
                read = new InputStreamReader(new FileInputStream(file), encoding);//考虑到编码格式
                bufferedReader = new BufferedReader(read);
                String lineTxt;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    Log.i("readByLine:", lineTxt);
                }
            } catch (IOException e) {
                Log.i("readByLine:", "读取文件出错");
                e.printStackTrace();
            } finally {
                closeStream(bufferedReader);
                closeStream(read);
            }
        } else {
            Log.i("readByLine:", "找不到文件");
        }
    }

    public static void closeStream(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(long size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    /**
     * 根据文件的路劲获取文件的后缀名
     *
     * @param filename
     * @return
     */
    public static String getExtensionName(String filename) {
        String strFileExtensName = "";
        try {
            if ((filename != null) && (filename.length() > 0)) {
                int dot = filename.lastIndexOf('.');
                if ((dot > -1) && (dot < (filename.length() - 1))) {
                    strFileExtensName = filename.substring(dot + 1);
                }
            }
        } catch (Exception e) {
        }
        return strFileExtensName;
    }

    /**
     * 获取osstoken
     *
     * @param filename
     * @return
     */
    public static String getOSSFileExtensionName(String filename) {
        String strFileExtension = getExtensionName(filename);
        if (TextUtils.isEmpty(strFileExtension)) {
            return "";
        } else if ("png".equals(filename.toLowerCase())) {
            return "image/png";
        } else if ("jpg".equals(filename.toLowerCase())) {
            return "image/jpg";
        } else if ("mp4".equals(filename.toLowerCase())) {
            return "video/mp4";
        } else {
            return "";
        }
    }

    /**
     * 删除文件夹及其子文件
     *
     * @param file
     */
    private static void deleteFileDirs(File file) {
        //生成File[]数组   listFiles()方法获取当前目录里的文件夹  文件
        File[] files = file.listFiles();
        //判断是否为空   //有没有发现讨论基本一样
        if (files != null && files.length > 0) {
            //遍历
            for (File file2 : files) {
                //是文件就删除
                if (file2.isFile()) {
                    file2.delete();
                } else if (file2.isDirectory()) {
                    //是文件夹就递归
                    deleteFileDirs(file2);
                    //空文件夹直接删除
                    file2.delete();
                }
            }
        } else {
            //空文件
        }

    }


    /**
     * 使用文件通道的方式复制文件
     *
     * @param s 源文件
     * @param t 复制到的新文件
     */

    public static void fileChannelCopy(File s, File t) {
        if (s == null || !s.exists() || t == null)
            return;
        if (t.exists()) {
            t.delete();
        }

        try {
            t.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();// 得到对应的文件通道
            out = fo.getChannel();// 得到对应的文件通道
            in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fi != null) {
                    fi.close();
                }
                if (in != null) {
                    in.close();
                }
                if (fo != null) {
                    fo.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除具体的文件
     *
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        KLog.i("deleteFile==--" + filePath);
        if (TextUtils.isEmpty(filePath))
            return;
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                file.delete();
            }
        } catch (Exception e) {

        }
    }


    /**
     * 导出数据库
     *
     * @param packageName  应用包名
     * @param databaseName 数据库名
     * @param destFilePath 目标文件路径
     */
    public static void dumpDatabase(String packageName, String databaseName, String destFilePath) {
        Log.i("dumpDatabase", "=====packageName:" + packageName + ",databaseName:" + databaseName + ",destFilePath:" + destFilePath);
        if (TextUtils.isEmpty(packageName)
                || TextUtils.isEmpty(databaseName)
                || TextUtils.isEmpty(databaseName)) {
            return;
        }
        File dbFile = new File("/data/data/" + packageName + "/databases/" + databaseName);
        Log.i("dumpDatabase", "=====dbFile path:" + dbFile.getAbsolutePath());
        if (!dbFile.exists() || dbFile.isDirectory()) {
            Log.i("dumpDatabase", "dump database fail:database file not exist!!!");
            return;
        }
        InputStream fis = null;
        OutputStream fos = null;
        try {
            fis = new FileInputStream(dbFile);
            fos = new FileOutputStream(destFilePath);
            int i;
            while ((i = fis.read()) != -1) {
                fos.write(i);
            }
            fos.flush();
            Log.i("dumpDatabase", "dump database ok");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("dumpDatabase", "dump database fail:" + e.getMessage());
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

    }


    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }


    public static String getJsonConfigString(Context context, String path) {

        String sdFileString = getFileString(path);
        if (TextUtils.isEmpty(sdFileString)) {
            String s = readAssets(context, "config.json");
            return s;
        } else {
            return sdFileString;
        }
    }

    public static String getFileString(String path) {
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            byte temp[] = new byte[1024];
            StringBuilder sb = new StringBuilder("");
            int len = 0;
            while ((len = fis.read(temp)) > 0) {
                sb.append(new String(temp, 0, len));
            }
            Log.d("msg", "readSaveFile: \n" + sb.toString());
            fis.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }
}

