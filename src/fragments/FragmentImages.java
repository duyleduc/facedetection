package fragments;

import java.util.ArrayList;
import java.util.List;

import models.Picture;
import tools.ImagesAdapter;
import activities.MainActivity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.filtrerimage.R;

public class FragmentImages extends Fragment {

	private static final int MAX_IMAGE_IN_THREAD = 3;
	private static final int WIDTH = 220;

	public interface Data {
		public List<Picture> pictures();
	}

	private GridView mGridPics;
	private Data mData;
	private List<Picture> mPicsFace;
	private ImagesAdapter mAdapter;
	private int mCurrentIndex;

	private volatile int mNbImageProc;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View tView = inflater.inflate(R.layout.lt_fg_images, null);
		mGridPics = (GridView) tView.findViewById(R.id.fg_images_gv);
		return tView;
	}

	@Override
	public void onResume() {
		super.onResume();
		mPicsFace = new ArrayList<Picture>();
		mAdapter = new ImagesAdapter(mPicsFace, getActivity());
		mGridPics.setAdapter(mAdapter);
		detectFaces();
	}

	public void data(Data pData) {
		mData = pData;
	}

	private void detectFaces() {
		mNbImageProc = 0;
		mCurrentIndex = 0;

		while (mCurrentIndex < mData.pictures().size()
				&& mNbImageProc < MAX_IMAGE_IN_THREAD) {
			exec();
		}
	}

	private void exec() {
		if (mData.pictures().size() > mCurrentIndex) {
			Picture tPic = mData.pictures().get(mCurrentIndex);
			mCurrentIndex++;
			if (tPic.hasFace() == 1) {
				new Thread(new TResizeImage(tPic)).start();
			} else if (tPic.hasFace() == -1) {
				new Thread(new TDetectFace(tPic)).start();
				mNbImageProc++;
			}
		} else {
		 ((MainActivity) getActivity()).hideProBar(); 
		}
	}

	private class TResizeImage implements Runnable {
		private Picture mPicture;

		public TResizeImage(Picture pPic) {
			mPicture = pPic;
		}

		@Override
		public void run() {
			mPicture.bitmap(decodeSampledBitmapFromUri(mPicture.path(), WIDTH,
					WIDTH));
			Message msg = new Message();
			msg.arg1 = mPicture.hasFace();
			mHander.sendMessage(msg);
		}

	}

	private class TDetectFace implements Runnable {
		private static final int MAX_FACES_DETECTION = 1;
		private Picture mPicture;
		private FaceDetector mFaceDetector;
		private FaceDetector.Face[] mFaces;

		public TDetectFace(Picture pPic) {
			mPicture = pPic;
		}

		@Override
		public void run() {
			mPicture.bitmap(decodeSampledBitmapFromUri(mPicture.path(), WIDTH,
					WIDTH));

			Bitmap mFaceBitmap = mPicture.bitmap().copy(Bitmap.Config.RGB_565,
					true);
			int width = mFaceBitmap.getWidth();
			int height = mFaceBitmap.getHeight();

			mFaces = new FaceDetector.Face[MAX_FACES_DETECTION];
			mFaceDetector = new FaceDetector(width, height, MAX_FACES_DETECTION);

			int faceDetected = mFaceDetector.findFaces(mFaceBitmap, mFaces);

			if (faceDetected > 0) {
				mPicture.hasFace(1);
				mPicsFace.add(mPicture);
			}

			mNbImageProc--;
			Message msg = new Message();
			msg.arg1 = faceDetected;
			mHander.sendMessage(msg);
		}

	}

	private Handler mHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int faceDetected = msg.arg1;
			if (faceDetected > 0)
				mAdapter.notifyDataSetChanged();
			exec();
		}
	};

	private Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
			int reqHeight) {

		Bitmap bm = null;
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		options.inPreferredConfig = Config.RGB_565;
		options.inJustDecodeBounds = false;
		options.inDither = true;
		bm = BitmapFactory.decodeFile(path, options);

		return bm;
	}

	private int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}

}
