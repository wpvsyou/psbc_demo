package com.wp.demo.psbc.count;
/**
 * Created by wangpeng on 15-3-27.
 */
public class PSBCCount {
    public static final String DATABASE_NAME = "psbcdatabase";

    public interface Tables {
        public static final String COMPANY_DATA = "company_data";
        public static final String PERSONNEL = "personnel";
    }

    public interface Personnel {
        public static final String ID = "id";
        public static final String USER_NAME = "user_name";
        public static final String PASSWORD = "password";
        public static final String VALIDITY = "validity";
        public static final String LEVEL = "data_2";
        public static final String DATA_3 = "data_3";
    }

    public interface Company_data {
        public static final String ID = "id";
        public static final String DATA_TITLE = "data_1";
        public static final String DATA_INFORMATION = "data_2";
        public static final String DATA_IMAGE = "data_3";
        public static final String DATA_THUMBNAIL = "data_4";
        public static final String DATA_5 = "data_5";
        public static final String DATA_6 = "data_6";
        public static final String DATA_7 = "data_7";
        public static final String DATA_8 = "data_8";
        public static final String DATA_9 = "data_9";
    }
    public interface Uri {
        public static final android.net.Uri COMPANY_DATA_URI = android.net.Uri.parse("content://"
                + DATABASE_NAME + "/" + Tables.COMPANY_DATA);
        public static final android.net.Uri PERSONNEL_URI = android.net.Uri.parse("content://"
                + DATABASE_NAME + "/" + Tables.PERSONNEL);
    }

    public interface Login{
        public static final int LEVEL_UNFREEZE = 0;
        public static final int LEVEL_FREEZE = 1;
    }
}
