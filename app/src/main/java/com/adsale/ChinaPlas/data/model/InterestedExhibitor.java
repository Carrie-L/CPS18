package com.adsale.ChinaPlas.data.model;


import android.os.Parcel;
import android.os.Parcelable;

public class InterestedExhibitor implements Parcelable {
	/**展馆ID*/
	public String floorID;
	
	public String floorName;
	
	/**该展馆中包含的兴趣产品的数量（属于兴趣产品的参展商的数量）*/
	public int count;
	
	public int resDrawbale;
	
	


	@Override
	public String toString() {
		return "InterestedExhibitor [floorID=" + floorID + ", floorName=" + floorName + ", count=" + count + "]";
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.floorID);
		dest.writeString(this.floorName);
		dest.writeInt(this.count);
		dest.writeInt(this.resDrawbale);
	}

	public InterestedExhibitor() {
	}

	protected InterestedExhibitor(Parcel in) {
		this.floorID = in.readString();
		this.floorName = in.readString();
		this.count = in.readInt();
		this.resDrawbale = in.readInt();
	}

	public static final Creator<InterestedExhibitor> CREATOR = new Creator<InterestedExhibitor>() {
		public InterestedExhibitor createFromParcel(Parcel source) {
			return new InterestedExhibitor(source);
		}

		public InterestedExhibitor[] newArray(int size) {
			return new InterestedExhibitor[size];
		}
	};
}
