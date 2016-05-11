package com.lbsnews.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import com.lbsnews.R;

public class FileUtils {
	private String TAG = "*********FileUtils******";
	private static String PATH = FileUtils.getSdcardPath() + "/LBSNews/";
	
	/**
	 * ���sd��·��
	 * @return File����
	 */
	public static File getSdcard() {
		return Environment.getExternalStorageDirectory();
	}
	
	/**
	 * ����sd��·���ַ���
	 * @return String����
	 */
	public static String getSdcardPath() {
		return getSdcard().toString();
	}
	
	/**
	 * �����ļ�
	 * @param filePathAndName
	 * @return
	 */
	public static File createFile(String filePathAndName) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				String filePath = filePathAndName.toString();
				File myFile = new File(filePath);
				if (!myFile.exists()) {
					myFile.getAbsolutePath();
					myFile.getParentFile().mkdirs();
				}
				return myFile;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * �����ļ�·����ȡ�ļ���
	 * @param path
	 * @return
	 */
	public static String getFileNameByPath(String path) {
		if (!("".equals(path))){
			File file=new File(path);
			return file.getName();
		}
		
		return null;
	}

}
