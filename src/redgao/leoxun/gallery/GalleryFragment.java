package redgao.leoxun.gallery;

import java.util.List;

import redgao.leoxun.gallery.model.GalleryItem;
import redgao.leoxun.gallery.utils.GalleryAdapter;
import redgao.leoxun.gallery.utils.GalleryController;
import redgao.leoxun.mileycyrus.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GalleryFragment extends Fragment implements GalleryController.GalleryChangeListener {

    private static Context mContext;
    public static float SCALE_DIP = 0, SCREEN_WIDTH = 0;
    private String dataUrl;        

    private View galleryView;
    private GalleryController mGalleryController;
    private String more_thumbs_link;
    public static int LOAD_IN_ONCE_NUMBER = 5;
    private GridView gridGallery;
    private ProgressBar progressBar;
    GalleryAdapter adapter;

    public static GalleryFragment newInstance(Context mContext, String dataUrl) {
        if(GalleryFragment.mContext == null) 
            GalleryFragment.mContext = mContext;
        setupScreenDimension();
        
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString("dataUrl", dataUrl);
        fragment.setArguments(args);
        
        return fragment;
    }
    
    public void loadMoreThumbs() {   
        if(more_thumbs_link != null)
            mGalleryController.getMoreThumbs(more_thumbs_link);
    }

    public void loadGalleryPage(final List<GalleryItem> thumbImageUrls) {        
        adapter.addAll(thumbImageUrls);
    }
    
    public GalleryAdapter getAdapter() {
        return adapter;
    }
    
    public static void setupScreenDimension() {
        if(SCREEN_WIDTH == 0) { 
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            SCALE_DIP = metrics.density;
            SCREEN_WIDTH = metrics.widthPixels;
        }
    }
    
    public static int caculateColumnWidth() {
        float width = (SCREEN_WIDTH - convertDipToPixels(10) * 4)/3;
        return (int)width;
    }
    
    public static int caculateItemHeight(int originWidth, int originHeight) {
        float height = originHeight * caculateColumnWidth() / originWidth;
        return (int)height;
    }
    
    public static int convertDipToPixels(float valueDips) {
        int valuePixels = (int)(valueDips * SCALE_DIP + 0.5f);
        return valuePixels;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataUrl = getArguments().getString("dataUrl");
        
        more_thumbs_link = dataUrl;
        
        mGalleryController = new GalleryController(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        galleryView = inflater.inflate(R.layout.gallery_pager, container, false);
        
        gridGallery = (GridView)galleryView.findViewById(R.id.gridGallery);
        progressBar = (ProgressBar)galleryView.findViewById(R.id.progressBar);
        gridGallery.setFastScrollEnabled(true);
        adapter = new GalleryAdapter(getActivity());
        gridGallery.setAdapter(adapter);
        AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                GalleryItem item = adapter.getItem(position);
                Intent i = new Intent(getActivity(), ViewActivity.class); 
                i.putExtra("GALLERY_URL", item.getGalleryUrl());           
                getActivity().startActivity(i);  
            }
        };
        gridGallery.setOnItemClickListener(mItemClickListener);
        
        return galleryView;
    }
    
    @Override
    public void onThumbsPreLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onThumbsLoaded(List<GalleryItem> thumbImageUrls, String nextUrl) {
        loadGalleryPage(thumbImageUrls);
        progressBar.setVisibility(View.GONE);
    }
    
    public String getBackPage()
    {
        return null;
    }

    @Override
    public void onThumbsLoadFail(Exception ex) {
//        ex.printStackTrace();
        progressBar.setVisibility(View.GONE);
        galleryView.setVisibility(View.GONE);
        
        final LinearLayout error = (LinearLayout)galleryView.findViewById(R.id.galleryError);
        error.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                error.setVisibility(View.GONE);
                galleryView.setVisibility(View.VISIBLE);
                loadMoreThumbs();
            }
        });
        error.setVisibility(View.VISIBLE);
    }
    
    
    /*
     * BitmapHandler coding  block
     */
//    private static final String IMAGE_CACHE_DIR = "SnB.data";
//    private ImageFetcher imageFetcher; 
//    public void setupBitmapHandler() {
//        int longest = (int)(SCREEN_WIDTH > SCREEN_WIDTH ? SCREEN_WIDTH : SCREEN_WIDTH) / 2;
//        ImageCacheParams cacheParams = new ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);
//        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
//
//        imageFetcher = new ImageFetcher(getActivity(), longest);
//        imageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
//    }
//
//    public ImageFetcher getImageFetcher() {
//        return imageFetcher;
//    }

    @Override
    public void onPause() {
        super.onPause();
        
//        imageFetcher.setPauseWork(false);
//        imageFetcher.setExitTasksEarly(true);
//        imageFetcher.flushCache();
    }

    @Override
    public void onResume() {
        super.onResume();
        
//        imageFetcher.setExitTasksEarly(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        imageFetcher.closeCache();
    }
    
    public Rect getTextSize(TextView textView, String text) {
        Rect bounds = new Rect();
        Paint textPaint = textView.getPaint();
        textPaint.getTextBounds(text,0,text.length(),bounds);
        
        return bounds;
    }
}
