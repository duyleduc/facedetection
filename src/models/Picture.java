package models;

import android.graphics.Bitmap;

public class Picture {

	private String mPath;
	private String mBucketName;
	private long mDate;
	private int mHasFace; // 1: has face; 0: doesn't have; -1: don't know

	private Bitmap mBitmap;

	public Picture(Builder builder) {
		mPath = builder.mPath;
		mBucketName = builder.mBucketName;
		mDate = builder.mDate;
		mHasFace = builder.mHasFace;
		mBitmap = builder.mBitmap;
	}

	public String path() {
		return mPath;
	}

	public int hasFace() {
		return mHasFace;
	}

	public void hasFace(int pHasFace) {
		mHasFace = pHasFace;
	}

	public Bitmap bitmap() {
		return mBitmap;
	}

	public void bitmap(Bitmap bitmap) {
		mBitmap = bitmap;
	}

	public static class Builder {
		private String mPath;
		private String mBucketName;
		private long mDate;
		private int mHasFace;
		private Bitmap mBitmap;

		public Builder() {
			mHasFace = -1;
		}

		public Builder path(String pPath) {
			mPath = pPath;
			return this;
		}

		public Builder name(String pName) {
			mBucketName = pName;
			return this;
		}

		public Builder date(long pDate) {
			mDate = pDate;
			return this;
		}

		public Builder hasFace(int pHasFace) {
			mHasFace = pHasFace;
			return this;
		}

		public Picture build() {
			return new Picture(this);
		}

		public Builder bitmap(Bitmap bitmap) {
			mBitmap = bitmap;
			return this;
		}
	}
}
