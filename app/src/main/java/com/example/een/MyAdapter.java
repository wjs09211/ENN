package com.example.een;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by 睡睡 on 2015/12/17.
 */
public class MyAdapter extends BaseAdapter implements AbsListView.OnScrollListener{
    Context context;
    List<RecordItem> rowItems;
    /**
     * 记录所有正在下载或等待下载的任务。
     */
    private HashSet<BitmapWorkerTask> taskCollection;

    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     */
    private LruCache<String, Bitmap> mMemoryCache;

    /**
     * GridView的实例
     */
    private ListView reportList;

    /**
     * 第一张可见图片的下标
     */
    private int mFirstVisibleItem;

    /**
     * 一屏有多少张图片可见
     */
    private int mVisibleItemCount;

    /**
     * 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会下载图片的问题。
     */
    private boolean isFirstEnter = true;
    public MyAdapter(Context context, List<RecordItem> items, ListView listView) {
        this.context = context;
        this.rowItems = items;
        reportList = listView;

        taskCollection = new HashSet<BitmapWorkerTask>();
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
        reportList.setOnScrollListener(this);
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtDate;
        TextView txtLocation;
        TextView txtState;
        ImageView imgReport;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.txtDate = (TextView) convertView.findViewById(R.id.txt_date);
            holder.txtLocation = (TextView) convertView.findViewById(R.id.txt_location);
            holder.txtState = (TextView) convertView.findViewById(R.id.txt_state);
            holder.imgReport = (ImageView) convertView.findViewById(R.id.img_report);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        RecordItem rowItem = (RecordItem) getItem(position);

        holder.txtDate.setText("時間 : " + rowItem.getDate());
        holder.txtLocation.setText("地點 : " + rowItem.getLocation());
        if( rowItem.getSolve() ) {
            holder.txtState.setText("已解決");
            holder.txtState.setTextColor(Color.BLACK);
        }
        else {
            holder.txtState.setText("未解決");
            holder.txtState.setTextColor(Color.RED);
        }

        final ImageView photo = (ImageView) convertView.findViewById(R.id.img_report);
        // 给ImageView设置一个Tag，保证异步加载图片时不会乱序
        photo.setTag(rowItem.getUrl());
        setImageView(rowItem.getUrl(), photo);

        Log.e("in", "getView "+position);
        return convertView;
    }
    private void setImageView(String imageUrl, ImageView imageView) {
        Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            //imageView.setImageResource(R.drawable.empty_photo);
        }
    }
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }
    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
        } else {
            cancelAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
        // 下载的任务应该由onScrollStateChanged里调用，但首次进入程序时onScrollStateChanged并不会调用，
        // 因此在这里为首次进入程序开启下载任务。
        if (isFirstEnter && visibleItemCount > 0) {
            loadBitmaps(firstVisibleItem, visibleItemCount);
            isFirstEnter = false;
        }
    }
    private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
        try {
            for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
                RecordItem rowItem = (RecordItem) getItem(i);
                String imageUrl = rowItem.getUrl();
                Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
                if (bitmap == null) {
                    BitmapWorkerTask task = new BitmapWorkerTask();
                    taskCollection.add(task);
                    task.execute(imageUrl);
                } else {
                    ImageView imageView = (ImageView) reportList.findViewWithTag(imageUrl);
                    if (imageView != null && bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void cancelAllTasks() {
        if (taskCollection != null) {
            for (BitmapWorkerTask task : taskCollection) {
                task.cancel(false);
            }
        }
    }
    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        /**
         * 图片的URL地址
         */
        private String imageUrl;

        @Override
        protected Bitmap doInBackground(String... params) {
            imageUrl = params[0];
            // 在后台开始下载图片
            Bitmap bitmap = downloadBitmap(params[0]);
            if (bitmap != null) {
                // 图片下载完成后缓存到LrcCache中
                addBitmapToMemoryCache(params[0], bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            // 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。
            ImageView imageView = (ImageView) reportList.findViewWithTag(imageUrl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            taskCollection.remove(this);
        }

        /**
         * 建立HTTP请求，并获取Bitmap对象。
         *
         * @param imageUrl
         *            图片的URL地址
         * @return 解析后的Bitmap对象
         */
        private Bitmap downloadBitmap(String imageUrl) {
            Bitmap bitmap = null;
            HttpURLConnection con = null;
            try {
                URL url = new URL(imageUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(10 * 1000);
                InputStream inputStream = con.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 20;
                bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
            return bitmap;
        }
    }
    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

    public class MemoryCache {

        private static final String TAG = "MemoryCache";
        private Map<String, Bitmap> cache= Collections.synchronizedMap(
                new LinkedHashMap<String, Bitmap>());//Last argument true for LRU ordering
        private long size=0;//current allocated size
        private long limit=1000000;//max memory in bytes

        public MemoryCache(){
            //use 25% of available heap size
            setLimit(Runtime.getRuntime().maxMemory()/4);
        }

        public void setLimit(long new_limit){
            limit=new_limit;
            Log.i(TAG, "MemoryCache will use up to "+limit/1024./1024.+"MB");
        }

        public Bitmap get(String id){
            try{
                if(!cache.containsKey(id))
                    return null;
                //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
                return cache.get(id);
            }catch(NullPointerException ex){
                ex.printStackTrace();
                return null;
            }
        }

        public void put(String id, Bitmap bitmap){
            try{
                if(cache.containsKey(id))
                    size-=getSizeInBytes(cache.get(id));
                cache.put(id, bitmap);
                size+=getSizeInBytes(bitmap);
                checkSize();
            }catch(Throwable th){
                th.printStackTrace();
            }
        }

        private void checkSize() {
            Log.i(TAG, "cache size="+size+" length="+cache.size());
            if(size>limit){
                Iterator<Map.Entry<String, Bitmap>> iter=cache.entrySet().iterator();//least recently accessed item will be the first one iterated
                while(iter.hasNext()){
                    Map.Entry<String, Bitmap> entry=iter.next();
                    size-=getSizeInBytes(entry.getValue());
                    iter.remove();
                    if(size<=limit)
                        break;
                }
                Log.i(TAG, "Clean cache. New size "+cache.size());
            }
        }

        public void clear() {
            try{
                //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
                cache.clear();
                size=0;
            }catch(NullPointerException ex){
                ex.printStackTrace();
            }
        }

        long getSizeInBytes(Bitmap bitmap) {
            if(bitmap==null)
                return 0;
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }
}


