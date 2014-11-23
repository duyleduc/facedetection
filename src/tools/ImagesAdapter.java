package tools;

import java.util.List;

import models.Picture;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.filtrerimage.R;

public class ImagesAdapter extends BaseAdapter {
	private static final int MAX_FACES_DETECTION = 1;
	private List<Picture> mPics;
	private Holder mHolder;
	private Context mContext;

	public ImagesAdapter(List<Picture> pPics, Context pContext) {
		mPics = pPics;
		mContext = pContext;
	}

	@Override
	public int getCount() {
		return mPics.size();
	}

	@Override
	public Object getItem(int position) {
		return mPics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {

		Picture tPic = (Picture) getItem(position);

		if (convertView == null) {
			mHolder = new Holder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.lt_fg_ig_picture, null);
			mHolder.mIvImage = (ImageView) convertView
					.findViewById(R.id.fg_ig_picture);
			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}

		mHolder.mIvImage.setImageBitmap(tPic.bitmap());
		return convertView;
	}

	private void detectFaces(Picture pPic) {

		Bitmap bitmap = getBitmapFrom(null);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		FaceDetector pFaceDetector;

		FaceDetector.Face[] pFaces = new FaceDetector.Face[MAX_FACES_DETECTION];

		pFaceDetector = new FaceDetector(width, height, MAX_FACES_DETECTION);

		int faceDetected = pFaceDetector.findFaces(bitmap, pFaces);
		pPic.hasFace(faceDetected > 0 ? 1 : 0);
	}

	private Bitmap getBitmapFrom(Picture pPic) {
		return null;
	}

	private class Holder {
		ImageView mIvImage;
	}

}
