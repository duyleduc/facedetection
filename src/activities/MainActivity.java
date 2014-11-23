package activities;

import java.util.ArrayList;
import java.util.List;

import models.Picture;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.widget.ProgressBar;

import com.example.filtrerimage.R;

import fragments.FragmentImages;
import fragments.FragmentImages.Data;

public class MainActivity extends Activity implements Data {

	private List<Picture> mPicGallery;
	private ProgressBar mProgBar;
	private FragmentImages mFgImages;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lt_act_main);
		mPicGallery = new ArrayList<Picture>();
		setInterface();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setData();
	}

	private void setInterface() {
		mProgBar = (ProgressBar) findViewById(R.id.act_main_loading_processbar);
		mFgImages = (FragmentImages) getFragmentManager().findFragmentById(
				R.id.act_main_fg);
	}

	private void setData() {
		getImagesFromGallery();
		mFgImages.data(this);

	}

	private void getImagesFromGallery() {

		mProgBar.setVisibility(View.VISIBLE);
		String[] projection = new String[] { MediaColumns.DATA,
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
				MediaStore.Images.Media.DATE_TAKEN };

		Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

		@SuppressWarnings("deprecation")
		Cursor cur = managedQuery(images, projection, null, null,
				MediaStore.Images.Media.DATE_TAKEN + " DESC");

		if (cur.moveToFirst()) {
			String bucket;
			String date;
			String path;
			int bucketColumn = cur
					.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

			int dateColumn = cur
					.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);

			int pathColumn = cur.getColumnIndex(MediaColumns.DATA);
			do {
				// Get the field values
				bucket = cur.getString(bucketColumn);
				date = cur.getString(dateColumn);
				path = cur.getString(pathColumn);

				Picture.Builder builder = new Picture.Builder();
				builder.date(Long.parseLong(date)).name(bucket).path(path);
				mPicGallery.add(builder.build());
			} while (cur.moveToNext());

		}
	}

	public void hideProBar() {
		mProgBar.setVisibility(View.GONE);
	}

	@Override
	public List<Picture> pictures() {
		return mPicGallery;
	}
}
