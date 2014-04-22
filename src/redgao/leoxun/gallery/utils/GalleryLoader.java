package redgao.leoxun.gallery.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import redgao.leoxun.gallery.GalleryActivity;
import redgao.leoxun.gallery.GalleryFragment;
import redgao.leoxun.gallery.model.GalleryItem;
import redgao.leoxun.gallery.utils.GalleryController.GalleryChangeListener;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class GalleryLoader extends AsyncTask<String, Integer, Void>
{    
    private static int PRE_LOADING = 1;
    private static int SUCCESS_STATUS = 2;
    private static int ERROR_STATUS = 3;
    
    private GalleryChangeListener listener;
    private List<GalleryItem> mGalleryItems;
    private String currentUrl, nextUrl;

    public GalleryLoader(GalleryChangeListener listener)
    {
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(String... params)
    {
        try
        {
            publishProgress(PRE_LOADING);

            currentUrl = params[0];
            galleryParser(currentUrl);
            mGalleryItems = getMoreGalleryItem();
            nextUrl = getNextPage();
            
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
            listener.onThumbsPreLoading();
        }
        else if(values[0].intValue() == SUCCESS_STATUS)
        {
            listener.onThumbsLoaded(mGalleryItems, nextUrl);
        } 
        else if(values[0].intValue() == ERROR_STATUS)
        {
            listener.onThumbsLoadFail(new Exception("Error to connect network!"));
        }
    }
    
    private Document doc;
    private Elements gagsElements;
    private Element content;
    
    public void galleryParser(String url) throws IOException
    {
        String str = "";
        
        HttpClient mHttpClient = new DefaultHttpClient();
        final HttpParams params = mHttpClient.getParams();
        
        //Setup proxy
//        HttpHost proxy = new HttpHost("192.168.133.252", 3128, "http");
//        params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        
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
        gagsElements = content.select("td.thumbnails");
    }

    public List<GalleryItem> getMoreGalleryItem()
    {
        ArrayList<GalleryItem> mGalleryItems = new ArrayList<GalleryItem>();
        Iterator<Element> i = gagsElements.iterator();

        while(i.hasNext())
        {
            Element e = i.next();
            GalleryItem item = getItem(e);
            if(item != null)
                mGalleryItems.add(item);
        }

        return mGalleryItems;
    }

    public String getNextPage()
    {
        return null;
//        String page = "";
//        boolean isDone = false;
//        for(int i=currentUrl.length()-1; i >= 0; i--) {
//            if(currentUrl.charAt(i) != '/') page = currentUrl.charAt(i) + page;
//            else {
//                if(!isDone) {
//                    page = String.valueOf(Long.parseLong(page) + 1);
//                    isDone = true;
//                }
//                page = currentUrl.charAt(i) + page;
//            }
//        }        
//        
//        Log.e("nextPage", page);
//        return page;
    }

    private GalleryItem getItem(Element e)
    {
        try {      
            Element header = e.select("a").get(0);
            String galleryUrl = GalleryActivity.BASE_URL + header.attr("href");        
            String title = e.select("span.thumb_title").get(0).html();
            String imageUrl = GalleryActivity.BASE_URL + header.select("img.image").get(0).attr("src");   
            GalleryItem gallery = new GalleryItem(galleryUrl, title, imageUrl);
            
            BitmapFactory.Options ops = decodeImgSizeFromUrl(imageUrl);
            if(ops != null) {
                gallery.setThumbHeight(GalleryFragment.caculateItemHeight(ops.outWidth, ops.outHeight));
            }
    
            return gallery;
        } catch (Exception ex) {
            Log.e("Boc tach loi element", ex.getMessage());  
            return null;
        }
    }

    public BitmapFactory.Options decodeImgSizeFromUrl(String url) {
        try {
            HttpClient httpclient= new DefaultHttpClient();
            
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = (HttpResponse)httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            InputStream instream = bufHttpEntity.getContent();

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(instream, null, o);

            return o;
        } catch (FileNotFoundException e) {
            return null;
        } catch (ClientProtocolException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        
    }
}
