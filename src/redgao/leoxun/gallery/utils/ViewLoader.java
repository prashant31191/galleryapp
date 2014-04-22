package redgao.leoxun.gallery.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import redgao.leoxun.gallery.GalleryActivity;
import redgao.leoxun.gallery.model.ViewItem;
import redgao.leoxun.gallery.utils.ViewController.ViewChangeListener;

import android.os.AsyncTask;
import android.util.Log;

public class ViewLoader extends AsyncTask<String, Integer, Void>
{    
    private static int PRE_LOADING = 1;
    private static int SUCCESS_STATUS = 2;
    private static int ERROR_STATUS = 3;

    private ViewChangeListener listener;
    private ArrayList<ViewItem> mViewItems;
    private String parseUrl;

    public ViewLoader(ViewChangeListener listener)
    {
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(String... params)
    {
        try
        {
            publishProgress(PRE_LOADING);

            parseUrl = params[0];
//            Log.e("parse link", parseUrl);
            viewParser(parseUrl);
            mViewItems = getMoreViewItem();
            
            publishProgress(SUCCESS_STATUS);
        }
        catch(Exception ex)
        {     
            publishProgress(ERROR_STATUS);            
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values)
    {
        if(values[0].intValue() == PRE_LOADING)
        {
            listener.onViewPreLoading();
        }
        else if(values[0].intValue() == SUCCESS_STATUS)
        {
            listener.onViewLoaded(mViewItems);
        } 
        else if(values[0].intValue() == ERROR_STATUS)
        {
            listener.onViewLoadFail(new Exception("Error to connect network!"));
        }
    }
    
    private Document doc;
    private Elements elements;
    private Element content;
    
    public void viewParser(String url) throws IOException
    {
        String str = "";
        
        HttpClient mHttpClient = new DefaultHttpClient();
        final HttpParams params = mHttpClient.getParams();
        
        HttpConnectionParams.setConnectionTimeout(params, 10000);
        HttpConnectionParams.setSoTimeout(params, 10000);
        ConnManagerParams.setTimeout(params, 10000);

        HttpGet httpget = new HttpGet(url);
        HttpResponse response = mHttpClient.execute(httpget);
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
            str = new String(bytes, "UTF-8");
        }

        doc = Jsoup.parse(str);
        content = doc.select(".maintable").get(1);        
        elements = content.select("td.thumbnails");
    }

    public ArrayList<ViewItem> getMoreViewItem()
    {
        ArrayList<ViewItem> mViewItems = new ArrayList<ViewItem>();
        Iterator<Element> i = elements.iterator();

        while(i.hasNext())
        {
            Element e = i.next();
            ViewItem item = getItem(e);
            if(item != null)
                mViewItems.add(item);
        }

        return mViewItems;
    }

    private ViewItem getItem(Element e)
    {
        try {
            Element header = e.select("a").get(0);
//            String sourceUrl = BASE_URL + header.attr("href"); 
//            String imageUrl = parseImageUrl(sourceUrl);
            
            String imageUrl = GalleryActivity.BASE_URL + header.select("img.image").get(0).attr("src");
            imageUrl = imageUrl.replace("/thumb_", "/normal_");
            
//            Log.e("imageUrl", imageUrl);
    
            return new ViewItem(imageUrl);
        } catch (Exception ex) {
            Log.e("Boc tach loi element", ex.getMessage());  
            return null;
        }
    }
    
    public String parseImageUrl(String sourceUrl) throws IOException {
        String str = "";
        
        HttpClient mHttpClient = new DefaultHttpClient();
        final HttpParams params = mHttpClient.getParams();
        
        HttpConnectionParams.setConnectionTimeout(params, 10000);
        HttpConnectionParams.setSoTimeout(params, 10000);
        ConnManagerParams.setTimeout(params, 10000);

        HttpGet httpget = new HttpGet(sourceUrl);
        HttpResponse response = mHttpClient.execute(httpget);
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
            str = new String(bytes, "UTF-8");
        }

        doc = Jsoup.parse(str);
        Element content = doc.select("td.display_media").get(0);        
        String imageUrl = GalleryActivity.BASE_URL + content.select("img.image").get(0).attr("src");
        return imageUrl;
    }
    
    public String getThumbName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

}
