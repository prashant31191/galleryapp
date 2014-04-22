package redgao.leoxun.gallery.utils;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;

import redgao.leoxun.gallery.GalleryActivity;
import redgao.leoxun.gallery.model.GalleryItem;
import redgao.leoxun.mileycyrus.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GalleryAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<GalleryItem> data = new ArrayList<GalleryItem>();

	public GalleryAdapter(Context mContext) {
		infalter = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public GalleryItem getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void addAll(List<GalleryItem> addData) {

		try {
			this.data.clear();
			this.data.addAll(addData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		notifyDataSetChanged();
	}

    @Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {

			convertView = infalter.inflate(R.layout.gallery_item, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id._galleryItemImage);
//			holder.text = (TextView) convertView.findViewById(R.id._galleryItemTitle);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		try {
//            holder.text.setText(data.get(position).getTitle());
		    setLayoutForGalleryItem(holder.image, data.get(position));
            ImageLoader imageloader = ((GalleryActivity)mContext).getImageLoader();
            imageloader.displayImage(data.get(position).getImageUrl(), holder.image);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}
    
    public void setLayoutForGalleryItem(ImageView view, GalleryItem item) {
        
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, item.getThumbHeight());
        view.setLayoutParams(params);
    }

	public class ViewHolder {
		TextView text;
		ImageView image;
	}

	public void clear() {
		data.clear();
		notifyDataSetChanged();
	}
}
