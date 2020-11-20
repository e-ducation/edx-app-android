package org.edx.mobile.course;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import org.edx.mobile.model.api.StartType;
import org.edx.mobile.util.UrlUtil;

public class CourseDetail implements Parcelable {
    public String course_id;
    public String name;
    public String number;
    public String org;
    public String short_description;
    public String start;
    public StartType start_type;
    public String start_display;
    public String end;
    public String enrollment_start;
    public String enrollment_end;
    public String blocks_url;
    public Media media;
    public String effort;
    public String overview;
    public Boolean invitation_only;
    public Boolean is_vip;
    public Boolean has_cert;
    public Boolean is_enroll;
    public Boolean is_normal_enroll;
    public Boolean is_subscribe_pay;
    public VipRecommendedPackage recommended_package;
    public String professor_name;
    public Boolean can_free_enroll;

    public static class VipRecommendedPackage implements Parcelable {
        public int id;
        public String name;
        public int month;
        public String price;
        public String suggested_price;
        public boolean is_recommended;

        protected VipRecommendedPackage(Parcel in) {
            id = in.readInt();
            name = in.readString();
            month = in.readInt();
            price = in.readString();
            suggested_price = in.readString();
            is_recommended = (Boolean) in.readValue(Boolean.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(name);
            dest.writeInt(month);
            dest.writeString(price);
            dest.writeString(suggested_price);
            dest.writeValue(is_recommended);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<VipRecommendedPackage> CREATOR = new Parcelable.Creator<VipRecommendedPackage>() {
            @Override
            public VipRecommendedPackage createFromParcel(Parcel in) {
                return new VipRecommendedPackage(in);
            }

            @Override
            public VipRecommendedPackage[] newArray(int size) {
                return new VipRecommendedPackage[size];
            }
        };
    }

    public static class Media implements Parcelable {
        public Image course_image;
        public Video course_video;

        protected Media(Parcel in) {
            course_image = (Image) in.readValue(Image.class.getClassLoader());
            course_video = (Video) in.readValue(Video.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(course_image);
            dest.writeValue(course_video);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Media> CREATOR = new Parcelable.Creator<Media>() {
            @Override
            public Media createFromParcel(Parcel in) {
                return new Media(in);
            }

            @Override
            public Media[] newArray(int size) {
                return new Media[size];
            }
        };
    }

    public static class Image implements Parcelable {
        private String uri;

        protected Image(Parcel in) {
            uri = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(uri);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
            @Override
            public Image createFromParcel(Parcel in) {
                return new Image(in);
            }

            @Override
            public Image[] newArray(int size) {
                return new Image[size];
            }
        };

        @Nullable
        public String getUri(String baseURL) {
            return UrlUtil.makeAbsolute(uri, baseURL);
        }
    }

    public static class Video implements Parcelable {
        public String uri;

        protected Video(Parcel in) {
            uri = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(uri);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
            @Override
            public Video createFromParcel(Parcel in) {
                return new Video(in);
            }

            @Override
            public Video[] newArray(int size) {
                return new Video[size];
            }
        };
    }

    protected CourseDetail(Parcel in) {
        course_id = in.readString();
        name = in.readString();
        number = in.readString();
        org = in.readString();
        short_description = in.readString();
        start = in.readString();
        start_type = (StartType) in.readValue(StartType.class.getClassLoader());
        start_display = in.readString();
        end = in.readString();
        enrollment_start = in.readString();
        enrollment_end = in.readString();
        blocks_url = in.readString();
        media = (Media) in.readValue(Media.class.getClassLoader());
        effort = in.readString();
        overview = in.readString();
        invitation_only = (Boolean) in.readValue(Boolean.class.getClassLoader());
        is_vip = (Boolean) in.readValue(Boolean.class.getClassLoader());
        has_cert = (Boolean) in.readValue(Boolean.class.getClassLoader());
        is_enroll = (Boolean) in.readValue(Boolean.class.getClassLoader());
        is_normal_enroll = (Boolean) in.readValue(Boolean.class.getClassLoader());
        is_subscribe_pay = (Boolean) in.readValue(Boolean.class.getClassLoader());
        recommended_package = (VipRecommendedPackage) in.readValue(VipRecommendedPackage.class.getClassLoader());
        professor_name = in.readString();
        can_free_enroll = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(course_id);
        dest.writeString(name);
        dest.writeString(number);
        dest.writeString(org);
        dest.writeString(short_description);
        dest.writeString(start);
        dest.writeValue(start_type);
        dest.writeString(start_display);
        dest.writeString(end);
        dest.writeString(enrollment_start);
        dest.writeString(enrollment_end);
        dest.writeString(blocks_url);
        dest.writeValue(media);
        dest.writeString(effort);
        dest.writeString(overview);
        dest.writeValue(invitation_only);
        dest.writeValue(is_vip);
        dest.writeValue(has_cert);
        dest.writeValue(is_enroll);
        dest.writeValue(is_normal_enroll);
        dest.writeValue(is_subscribe_pay);
        dest.writeValue(recommended_package);
        dest.writeString(professor_name);
        dest.writeValue(can_free_enroll);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CourseDetail> CREATOR = new Parcelable.Creator<CourseDetail>() {
        @Override
        public CourseDetail createFromParcel(Parcel in) {
            return new CourseDetail(in);
        }

        @Override
        public CourseDetail[] newArray(int size) {
            return new CourseDetail[size];
        }
    };
}
