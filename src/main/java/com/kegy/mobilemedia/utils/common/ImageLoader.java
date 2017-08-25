package com.kegy.mobilemedia.utils.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * <p>图片加载框架ImageLoader说明</p>
 * <p>有如下的的几种加载方法：</p>
 * <ul>
 * <li>{@link #loadBitmap(String, int, int)},{@link #loadBitmap(String)},
 * 这两种加载方式为同步加载，都表示从某个URL中加载图片，区别是表示将图片压缩到
 * 指定的尺寸，后一种则保持原始尺寸。URL可以是图片的网络地址，也可以是图片在内
 * 存卡中的路径。加载成功返回图片，失败返回null。同步加载并不适用于加载大量图片
 * 的情况，比如你要用图片填充一个GridView，这种情况下，使用异步加载更合适。但是
 * 如果加载不大，这时候用同步加载是合适的。同步加载的时候会阻塞，所以说不能用于
 * 大量的图片加载情况。</li>
 * <li>{@link #bindBitmap(String, ImageView, int)},{@link #bindBitmap(String,
 * ImageView, int, int, int)},这两种加载方式都是异步加载，都能从指定URL中加载图
 * 片，URL可以图片的网络地址或者是在内存卡中的路径。区别是前者无压缩效果，后者可以压
 * 缩到指定尺寸。加载完毕后将图片设置到ImageView中，如果加载出错，则设置errorId所
 * 对应的资源图片。异步加载方式适用于加载量较大的情况。</li>
 * <li>{@link #loadBitmapFromResource(Resources, int)},{@link #loadBitmapFromResource(
 *Resources, int, int, int)},这两种加载方式都是从资源文件中去加载图片，后者有压缩的
 * 效果。</li>
 * </ul>
 * <p>不论同步加载还是异步加载，加载后的图片都会放入缓存中，下次不会再次加载</p>
 * Created by kegy on 2017/7/26.
 */

public final class ImageLoader {
    private static final String TAG = "ImageLoader";

    private static final int LOAD_SUCCESS = 0x121;
    private static final int LOAD_FAILED = 0x122;

    private static final int DISK_CACHE_INDEX = 0;

    private static final int DEFAULT_CONNECTION_TIMEOUT = 5 * 1000;//5s
    private static final int DEFAULT_READ_TIMEOUT = 8 * 1000;//8s

    private static final int MAX_MEMORY_SIZE = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private static final int LRU_CACHE_SIZE = MAX_MEMORY_SIZE / 8;
    private static final long DISK_LRU_CACHE_SIZE = 50 * 1024 * 1024;//50M

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_SIZE = CPU_COUNT + 1;
    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 30;
    private static final ThreadPoolExecutor EXECUTORS;
    private static final BlockingDeque<Runnable> BLOCKING_DEQUE = new LinkedBlockingDeque<>(128);
    private static ThreadFactory sThreadFactory = new ThreadFactory() {

        private AtomicInteger mAtomicInteger = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ImageLoader#" + mAtomicInteger.getAndIncrement());
        }
    };
    private static final ExecutorService POOL = Executors.newCachedThreadPool();

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
                BLOCKING_DEQUE, sThreadFactory);
        EXECUTORS = threadPoolExecutor;
    }

    private static ImageLoader sInstance;

    //锁对象 给DiskLruCache加锁
    private Object mDiskLruCacheLock = new Object();

    private ImageDecoder mImageDecoder;

    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskCache;
    private boolean mDiskCacheCreated = false;

    private Context mContext;

    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    LoaderResult loaderResult = (LoaderResult) msg.obj;
                    ImageView imageView = loaderResult.imageView;
                    Bitmap bitmap = loaderResult.bitmap;
                    imageView.setImageBitmap(bitmap);
                    break;
                case LOAD_FAILED:
                    Log.d(TAG, "load bitmap failed");
                    break;
            }
        }
    };

    /**
     * 产生唯一实例
     *
     * @param context
     * @return
     */
    public static ImageLoader with(Context context) {
        if (sInstance == null) {
            synchronized (ImageLoader.class) {
                if (sInstance == null) {
                    sInstance = new ImageLoader(context);
                }
            }
        }
        return sInstance;
    }

    private ImageLoader(Context context) {
        mContext = context.getApplicationContext();
        mImageDecoder = new ImageDecoder();
        mMemoryCache = new LruCache<String, Bitmap>(LRU_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
        initDiskLruCache();
    }

    private void initDiskLruCache() {
        File file = getDiskCacheDir("images");
        try {
            mDiskCache = DiskLruCache.open(file, getAppVersion(), 1, DISK_LRU_CACHE_SIZE);
            mDiskCacheCreated = true;
            Log.d(TAG, "disk cache created " + file.getPath());
        } catch (IOException e) {
            mDiskCacheCreated = false;
            Log.e(TAG, "init disk cache error " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Bitmap loadBitmap(String url) {
        return loadBitmap(url, 0, 0);
    }

    /**
     * 同步加载图片 会阻塞
     *
     * @param url
     * @param reqWidth  压缩后的宽度
     * @param reqHeight 压缩后的高度
     * @return
     */
    public Bitmap loadBitmap(String url, int reqWidth, int reqHeight) {
        Log.d(TAG, "load bitmap from url " + url);
        Bitmap bitmap;
        String key = hashKeyForDisk(url);
        //尝试从内存缓存中获取
        if ((bitmap = loadFromMemoryCache(key)) != null) {
            Log.d(TAG, "load success from memory cache");
            return bitmap;
        }
        //尝试从磁盘缓存中获取
        if ((bitmap = loadFromDiskCache(key, reqWidth, reqHeight)) != null) {
            Log.d(TAG, "load success from disk cache");
            return bitmap;
        }
        //尝试从文件中获取这个图片
        if ((bitmap = mImageDecoder.decodeBitmapFromFile(url, reqWidth, reqHeight)) != null) {
            Log.d(TAG, "load success from file");
            saveToMemoryCache(key, bitmap);//存入LruCache
            return bitmap;
        }
        //下载
        if (mDiskCacheCreated) {
            try {
                DiskLruCache.Editor editor = mDiskCache.edit(key);
                if (editor != null) {
                    OutputStream os = editor.newOutputStream(DISK_CACHE_INDEX);
                    Log.d(TAG, "loadBitmap editor os is null? " + (os == null));
                    if (downloadBitmap(url, os)) {
                        Log.d(TAG, "download success from http and save in disk cache");
                        editor.commit();
                        return loadFromDiskCache(key, reqWidth, reqHeight);
                    } else {
                        Log.d(TAG, "load failed from http");
                        editor.abort();
                        return null;
                    }
                }
                mDiskCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void bindBitmap(final String url, final ImageView imageView, final int errorId) {
        bindBitmap(url, imageView, errorId, 0, 0);
    }

    /**
     * 绑定bitmap到ImageView
     *
     * @param url
     * @param imageView
     * @param errorId
     * @param reqWidth
     * @param reqHeight
     */
    public void bindBitmap(final String url, final ImageView imageView, final int errorId, final int reqWidth, final int reqHeight) {
        Log.d(TAG, "bind bitmap from url " + url);
        if (imageView == null)
            return;
        String key = hashKeyForDisk(url);
        Bitmap bitmap = loadFromMemoryCache(key);
        if (bitmap != null) {
            Log.d(TAG, "bind bitmap success from memory cache");
            imageView.setImageBitmap(bitmap);
            return;
        }
        Runnable bitmapRunnable = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(url, reqWidth, reqHeight);
                if (bitmap != null) {
                    Message message = Message.obtain();
                    message.what = LOAD_SUCCESS;
                    message.obj = new LoaderResult(imageView, bitmap);
                    mHandler.sendMessage(message);
                } else {
                    Message message = Message.obtain();
                    message.what = LOAD_SUCCESS;
                    Bitmap error = loadBitmapFromResource(mContext.getResources(), errorId);
                    message.obj = new LoaderResult(imageView, error);
                    mHandler.sendMessage(message);
                }
            }
        };
        EXECUTORS.execute(bitmapRunnable);
    }

    public Bitmap loadBitmapFromResource(Resources resources, int resId) {
        return mImageDecoder.decodeBitmapFromResource(resources, resId, 0, 0);
    }

    public Bitmap loadBitmapFromResource(Resources resources,
                                         int resId,
                                         int reqWidth,
                                         int reqHeight) {
        return mImageDecoder.decodeBitmapFromResource(resources, resId, reqWidth, reqHeight);
    }


    private boolean downloadBitmap(final String url, final OutputStream os) {
        Log.d(TAG, "download bitmap from url " + url);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.w(TAG, "download bitmap in ui thread");
            Callable<Boolean> bitmapCallable = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return executeDownLoad(url, os);
                }
            };
            Future<Boolean> bitmapFuture = POOL.submit(bitmapCallable);
            try {
                return bitmapFuture.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            } catch (ExecutionException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return executeDownLoad(url, os);
        }
    }

    /**
     * 具体执行下载的方法
     *
     * @param urlString
     * @param os
     * @return
     */
    private boolean executeDownLoad(String urlString, OutputStream os) {
        Log.d(TAG, "executeDownLoad from " + urlString);
        HttpURLConnection connection = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
            connection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
            InputStream is = connection.getInputStream();
            bis = new BufferedInputStream(is, 8 * 1024);
            bos = new BufferedOutputStream(os, 8 * 1024);
            int read;
            while ((read = bis.read()) != -1) {
                bos.write(read);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
            StreamUtil.close(bis);
            StreamUtil.close(bos);
        }
        return false;
    }

    /**
     * 从磁盘缓存中读
     *
     * @param key
     * @return
     */
    private Bitmap loadFromDiskCache(String key, int reqWidth, int reqHeight) {
        if (!mDiskCacheCreated) {
            return null;
        }
        Log.d(TAG, "loadFromDiskCache with key " + key);
        FileInputStream fis = null;
        try {
            synchronized (mDiskLruCacheLock) {
                DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
                if (snapshot != null) {
                    Log.d(TAG, "loadFromDiskCache snapshot is not null");
                    fis = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
                    FileDescriptor fileDescriptor = fis.getFD();
                    Bitmap bitmap = mImageDecoder.decodeBitmapFromFileDescriptor(fileDescriptor,
                            reqWidth, reqHeight);
                    Log.d(TAG, "loadFromDiskCache return bitmap is null? " + (bitmap == null));
                    saveToMemoryCache(key, bitmap);
                    return bitmap;
                } else {
                    Log.d(TAG, "loadFromDiskCache snapshot is null");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(fis);
        }
        return null;
    }

    /**
     * 存入内存缓存中
     *
     * @param key
     * @param value
     */
    private void saveToMemoryCache(String key, Bitmap value) {
        if (!TextUtils.isEmpty(key) && value != null) {
            mMemoryCache.put(key, value);
        }
    }

    /**
     * 从内存缓存中获取图片
     *
     * @param key
     * @return
     */
    private Bitmap loadFromMemoryCache(String key) {
        if (!TextUtils.isEmpty(key)) {
            return mMemoryCache.get(key);
        }
        return null;
    }

    /**
     * 生成存储文件名
     *
     * @param key
     * @return
     */
    private String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 获取缓存的路径
     *
     * @param uniqueName
     * @return
     */
    private File getDiskCacheDir(String uniqueName) {
        boolean isSDCardMounted = Environment.isExternalStorageEmulated();
        boolean isSDCardUnRemovable = Environment.isExternalStorageRemovable();
        String path;
        if (isSDCardMounted || isSDCardUnRemovable) {
            path = mContext.getExternalCacheDir().getPath();
        } else {
            path = mContext.getCacheDir().getPath();
        }
        File file = new File(path + File.separator + uniqueName);
        if (!file.exists())
            file.mkdirs();
        return file;
    }

    public static void bindVideoThumbnailWithPath(final String filePath, final ImageView iv) {
        POOL.submit(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                try {
                    retriever.setDataSource(filePath);
                    bitmap = retriever.getFrameAtTime();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        retriever.release();
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
                iv.setImageBitmap(bitmap);
            }
        });
    }

    /**
     * 获取APP的版本号
     *
     * @return
     */
    private int getAppVersion() {
        int version = 1;
        try {
            String packageName = mContext.getPackageName();
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(packageName, 0);
            version = packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 清除LruCache缓存
     */
    public void clearMemoryCache() {
        if (mMemoryCache != null) {
            mMemoryCache.evictAll();
        }
    }

    /**
     * 返回的是磁盘缓存已用字节
     *
     * @return
     */
    public long getDiskLruCacheSize() {
        return mDiskCache.size();
    }

    /**
     * 此方法不应该重复调用，只需要在activity onPause的时候调用即可
     */
    public void flush() {
        try {
            mDiskCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除所有的缓存数据
     */
    public void clearCache() {
        clearMemoryCache();
        if (mDiskCache != null) {
            synchronized (mDiskLruCacheLock) {
                try {
                    mDiskCache.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mDiskCache = null;
                initDiskLruCache();
            }
        }
    }

    private static final class StreamUtil {

        public static void close(OutputStream os) {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public static void close(InputStream is) {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static final class ImageDecoder {

        public Bitmap decodeBitmapFromResource(Resources resources,
                                               int resId,
                                               int reqWidth,
                                               int reqHeight) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(resources, resId, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(resources, resId, options);
        }

        public Bitmap decodeBitmapFromFile(String filePath,
                                           int reqWidth,
                                           int reqHeight) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(filePath, options);
        }

        public Bitmap decodeBitmapFromFileDescriptor(FileDescriptor fileDescriptor,
                                                     int reqWidth,
                                                     int reqHeight) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        }

        /**
         * 获取图片的压缩比
         *
         * @param options
         * @param reqWidth
         * @param reqHeight
         * @return
         */
        private int calculateInSampleSize(BitmapFactory.Options options,
                                          int reqWidth,
                                          int reqHeight) {
            if (reqWidth == 0 || reqHeight == 0)
                return 1;
            int inSampleSize = 1;
            final int width = options.outWidth;
            final int height = options.outHeight;
            if (width > reqHeight || height > reqHeight) {
                int widthRatio = Math.round((float) width / (float) reqWidth);
                int heightRatio = Math.round((float) height / (float) reqHeight);
                inSampleSize = Math.min(widthRatio, heightRatio);
            }
            Log.d(TAG, "inSampleSize is " + inSampleSize);
            return inSampleSize;
        }

    }

    private final class LoaderResult {
        ImageView imageView;
        Bitmap bitmap;

        public LoaderResult(ImageView imageView, Bitmap bitmap) {
            this.imageView = imageView;
            this.bitmap = bitmap;
        }
    }

}
