package com.miaokao.android.app.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Base64;

/**
 * Bitmapt工具类， 包括 1：Bitmap-byte[]-String互换 ,2：获取圆角或倒影图片,获取图片压缩的bitmap
 * 
 * @author ouyangbo@kingnode.com
 */
public class BitmapUtil {

	/**
	 * 把bitmap转换成String
	 * 
	 * @param filePath
	 *            图片路径
	 * @param reqWidth
	 *            压缩后的宽
	 * @param reqHeight
	 *            压缩后的高
	 * @param quality
	 *            压缩质量比
	 * @return
	 */
	public static String bitmapToString(String filePath, int reqWidth, int reqHeight, int quality) {
		try {
			Bitmap bm = decodeThumbBitmapForFile(filePath, reqWidth, reqHeight);
			return bitmapToString(bm, quality);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 把bitmap转换成String
	 */
	public static String bitmapToString(Bitmap bitmap, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
		byte[] b = baos.toByteArray();
		return replaceBlank(Base64.encodeToString(b, Base64.DEFAULT));
	}

	/**
	 * 字符串转化为bitmap类型
	 */
	public static Bitmap stringtoBitmap(String string) {
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 将bitmap转换成bytes
	 */
	public static byte[] bitmapToBytes(Bitmap bitmap, int quality) {
		int size = bitmap.getWidth() * bitmap.getHeight() * 4;
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		try {
			bitmap.compress(Bitmap.CompressFormat.PNG, quality, out);
			out.flush();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 将bitmap转换成InputStream
	 */
	public static InputStream Bitmap2InputStream(Bitmap bm) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
			return sbs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将bytes转换成bitmap
	 */
	public static Bitmap Bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	/**
	 * 获取视频缩略图
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getVideoBase64Str(final String filePath) {
		try {
			Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, Thumbnails.MINI_KIND);
			if (null != bitmap) {
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, 80, 80);
				return replaceBlank(BitmapUtil.bitmapToString(bitmap, 20));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获得圆角图片
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 获带倒影图片
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w, h / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight()
				+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, h, w, bitmapWithReflection.getHeight() + reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * 将多个Bitmap合并成一个图片。
	 * 
	 * @param int 将多个图合成多少列
	 * @param Bitmap
	 *            要合成的图片
	 * @return
	 */
	public static Bitmap combineBitmaps(int columns, Bitmap... bitmaps) {
		if (columns <= 0 || bitmaps == null || bitmaps.length == 0) {
			throw new IllegalArgumentException("Wrong parameters: columns must > 0 and bitmaps.length must > 0.");
		}
		int maxWidthPerImage = 0;
		int maxHeightPerImage = 0;
		for (Bitmap b : bitmaps) {
			maxWidthPerImage = maxWidthPerImage > b.getWidth() ? maxWidthPerImage : b.getWidth();
			maxHeightPerImage = maxHeightPerImage > b.getHeight() ? maxHeightPerImage : b.getHeight();
		}
		int rows = 0;
		if (columns >= bitmaps.length) {
			rows = 1;
			columns = bitmaps.length;
		} else {
			rows = bitmaps.length % columns == 0 ? bitmaps.length / columns : bitmaps.length / columns + 1;
		}
		Bitmap newBitmap = Bitmap.createBitmap(columns * maxWidthPerImage, rows * maxHeightPerImage, Config.RGB_565);

		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < columns; y++) {
				int index = x * columns + y;
				if (index >= bitmaps.length)
					break;
				newBitmap = mixtureBitmap(newBitmap, bitmaps[index], new PointF(y * maxWidthPerImage, x
						* maxHeightPerImage));
			}
		}
		return newBitmap;
	}

	public static Bitmap mixtureBitmap(Bitmap first, Bitmap second, PointF fromPoint) {
		if (first == null || second == null || fromPoint == null) {
			return null;
		}
		Bitmap newBitmap = Bitmap.createBitmap(first.getWidth(), first.getHeight(), Config.ARGB_4444);
		Canvas cv = new Canvas(newBitmap);
		cv.drawBitmap(first, 0, 0, null);
		cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		return newBitmap;
	}

	/**
	 * 将下载下来的图片缓存到sd卡里
	 * 
	 * @param url
	 * @param bitmap
	 */
	public static boolean addBitmapToSDCardCache(Bitmap bitmap, File file) {
		boolean success = false;
		byte[] bytes = bitmapToBytes(bitmap, 100);
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(bytes);
			out.flush();
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return success;
	}

	/**
	 * base64 保存文件
	 * 
	 * @param url
	 * @param bitmap
	 */
	public static void addRecodeToSDCardCache(String base64Str, File file) {
		byte[] bitmapArray = Base64.decode(base64Str, Base64.DEFAULT);
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(bitmapArray);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 根据View(主要是ImageView)的宽和高来获取图片的缩略图
	 * @param path
	 * @param viewWidth
	 * @param viewHeight
	 * @return
	 */
	public static Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight){
		BitmapFactory.Options options = new BitmapFactory.Options();
		//设置为true,表示解析Bitmap对象，该对象不占内存
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		//设置缩放比例
		options.inSampleSize = computeScale(options, viewWidth, viewHeight);
		
		//设置为false,解析Bitmap对象加入到内存中
		options.inJustDecodeBounds = false;
		
		return BitmapFactory.decodeFile(path, options);
	}
	
	/**
	 * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
	 * @param options
	 * @param width
	 * @param height
	 */
	private static int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight){
		int inSampleSize = 1;
		if(viewWidth == 0 || viewWidth == 0){
			return inSampleSize;
		}
		int bitmapWidth = options.outWidth;
		int bitmapHeight = options.outHeight;
		
		//假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
		if(bitmapWidth > viewWidth || bitmapHeight > viewWidth){
			int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
			int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);
			
			//为了保证图片不缩放变形，我们取宽高比例最小的那个
			inSampleSize = widthScale < heightScale ? widthScale : heightScale;
		}
		return inSampleSize;
	}

	/**
	 * 根据View(主要是ImageView)的宽和高来获取图片的缩略图
	 * @param path
	 * @param viewWidth
	 * @param viewHeight
	 * @return
	 */
	public static Bitmap decodeThumbBitmapForFile(FileDescriptor fileDescriptor, int viewWidth, int viewHeight){
		BitmapFactory.Options options = new BitmapFactory.Options();
		//设置为true,表示解析Bitmap对象，该对象不占内存
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
		//设置缩放比例
		options.inSampleSize = computeScale(options, viewWidth, viewHeight);
		
		//设置为false,解析Bitmap对象加入到内存中
		options.inJustDecodeBounds = false;
		
		return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
	}
	
	/**
	 * (三星等手机 有如下问题) 如果是手机照的相片旋转了 90度，或者180度，或者270度，修复还原图片
	 * 
	 * @param orientation
	 *            旋转角度
	 * @param imagePath
	 *            图片路径
	 */
	public static void saveMobileMatrixImage(String imagePath) {

		int degree = readPictureDegree(imagePath);
		// 角度为0 代表不需要旋转图片
		if (degree == 0) {
			return;
		}
		Bitmap bMapRotate;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		options.inDither = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[32 * 1024];
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap bMap;
		bMap = BitmapFactory.decodeFile(imagePath, options);
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), matrix, true);
		FileOutputStream out;
		try {
			File imgFile = new File(imagePath);
			out = new FileOutputStream(imgFile);
			bMapRotate.compress(Bitmap.CompressFormat.JPEG, 100, out);
			if (bMapRotate != null) {
				bMapRotate.recycle();
				bMapRotate = null;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	
	/**
	 * 转为圆角图片
	 * 
	 * @param bitmap
	 *            原图
	 * @param pixels
	 *            圆角的度数，数值越大，圆角越大
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
	
	/**
	 * 删除字符串中的换行以及空白符  主要是处理图片转base64后里面带有回车
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
	
	public static byte[] File2byte(String filePath)
	{
		byte[] buffer = null;
		try
		{
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int n;
			while ((n = fis.read(b)) != -1)
			{
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return buffer;
	}

	
	public static void byte2File(byte[] buf, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {
				dir.mkdirs();
			}
			file = new File(filePath + File.separator + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(buf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
