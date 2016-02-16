package cgtz.com.cgwallet.paymoney;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import java.util.ArrayList;
import java.util.List;

import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;

/**
 * Created by penny_yao on 2015/12/31.
 */

public class ContactManager {
    /**user类*/
    public static List<User> mUserList;
//
//    /**获取库Phon表字段**/
//    private static final String[] PHONES_PROJECTION = new String[] {
//            Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID };

    public  List<NumberBean> numberBeanList = new ArrayList<>();

    /**得到手机通讯录联系人信息**/
    public static List<User> getPhoneContacts(Context context) {
        User mUser;
        mUserList = new ArrayList<User>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cRawContact = resolver.query(ContactsContract.RawContacts.CONTENT_URI, new String[] { ContactsContract.RawContacts._ID }, null, null,
                null);
//        ContactBean contact;
        while (cRawContact.moveToNext()) {
            mUser = new User();

            long rawContactId = cRawContact.getLong(cRawContact.getColumnIndex(ContactsContract.RawContacts._ID));
            mUser.setRawContactId("" + rawContactId);

            Cursor dataCursor = resolver.query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.RAW_CONTACT_ID + "=?",
                    new String[] { String.valueOf(rawContactId) }, null);

            while (dataCursor.moveToNext()) {
                String data1 = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DATA1));
                String mimetype = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.MIMETYPE));

                if (mimetype.equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {
                    mUser.setName(data1);
                } else if (mimetype.equals(Phone.CONTENT_ITEM_TYPE)) {
                    mUser.setPhoneNum(data1);
                }

            }
            mUserList.add(mUser);
            dataCursor.close();
        }

        cRawContact.close();
        return mUserList;
    }



    // 根据联系人的姓名查询电话
    public static  String getQueryData(Context context,String name,String number) {
        String phoneNum = "";
        // 查联系人姓名这张表，有无用户输入的姓名
        ContentResolver contentResolver1 = context.getContentResolver();
        String[] colmuns1 = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };
        Cursor cursor1 = contentResolver1.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        //循环遍历
        for (cursor1.moveToFirst(); !(cursor1.isAfterLast()); cursor1
                .moveToNext()) {
            //拿到姓名
            String contactsName = cursor1.getString(cursor1
                    .getColumnIndex(colmuns1[1]));
            // 如果有，则开始查联系人电话这张表
            if (name.equals(contactsName)) {
                int id = cursor1.getInt(cursor1.getColumnIndex(colmuns1[0]));
                ContentResolver contentResolver2 = context.getContentResolver();
                String[] colmuns2 = new String[] { Phone.CONTACT_ID,
                        Phone.NUMBER };
                // 查询的联系人电话这张表时候，加入一个条件，就查目录联系人姓名那列数据中的id
                // 由于联系人电话这张表与联系人姓名这张表是同id的
                // 查询参数放在第三个参数这个位置
                Cursor cursor2 = contentResolver2.query(Phone.CONTENT_URI,
                        null, colmuns2[0] + "=" + id, null, null);
                while (cursor2.moveToNext()) {// 如果Cursor中有数据，要把初始位置为0的Cursor下拉一位才能拿到数据
                    phoneNum = cursor2.getString(cursor2
                            .getColumnIndex(colmuns2[1]));
                    if (phoneNum.contains(number)){
                        return phoneNum;
                    }

                }
                cursor2.close();
            }
        }
        cursor1.close();

        return phoneNum;// 查到就返回相应的电话，没查到就扔回0
    }

    public static String getName(Context context,String number,List<PhoneBean> list){
        String username,usernumber;
        String name = "";
        // 查联系人姓名这张表，有无用户输入的姓名
        ContentResolver contentResolver1 = context.getContentResolver();
        //String[] colmuns1 = new String[] { ContactsContract.Contacts._ID, Phone.HAS_PHONE_NUMBER};
        Cursor cursor = contentResolver1.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
       while (cursor.moveToNext()){
           //获得DATA表中的名字
           username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
           //条件为联系人ID
           String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
           // 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
           Cursor phone = contentResolver1.query(Phone.CONTENT_URI,
                   null,
                   Phone.CONTACT_ID + " = " + contactId,
                   null,
                   null);

           while (phone.moveToNext()) {
               PhoneBean phoneBean = new PhoneBean();
               usernumber = phone.getString(phone.getColumnIndex(Phone.NUMBER));
              // System.out.println("usernumber---------------" + usernumber);


               phoneBean.setUserName(username);
               phoneBean.setUserNumber(usernumber);

               list.add(phoneBean);

           }
           phone.close();
           for(int i = 0; i < list.size(); i++){
               number = number.replace(" ","");
               if (number.equals(list.get(i).getUserNumber())){
                   name = list.get(i).getUserName();
               }
           }
       }
        cursor.close();


        return name;// 查到就返回相应的电话，没查到就扔回0
    }


    public static  ArrayList<PhoneInfo> getData(Context context,ArrayList<PhoneInfo> phoneInfos){

        //获取联系人信息的Uri
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
//获取ContentResolver
        ContentResolver contentResolver = context.getContentResolver();
//查询数据，返回Cursor
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        while(cursor.moveToNext()) {

//获取联系人的ID
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//获取联系人的姓名
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//构造联系人信息

//查询电话类型的数据操作
            Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null, null);
            while (phones.moveToNext()) {
                PhoneInfo info = new PhoneInfo();
                String phoneNumber = phones.getString(phones.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
               // phoneNumber = phoneNumber.replace(" ","").replace("-","");
                phoneNumber = processMobileNumber(phoneNumber);
                info.setUserName(name);
                info.setUserNumber(phoneNumber);
                phoneInfos.add(info);

            }

            phones.close();

        }
        cursor.close();
        PhoneInfo phoneInfo = new PhoneInfo();
        phoneInfo.setUserNumber(Utils.getUserPhone(context));
        phoneInfo.setUserName("当前绑定号码");
        phoneInfos.add(phoneInfo);
//        for (int i = 0 ; i <phoneInfos.size();i++){
//
//            System.out.println("phoneInfos.get(i).getUserName()"+phoneInfos.get(i).getUserName());
//            System.out.println("phoneInfos.get(i).getUserNumber()"+phoneInfos.get(i).getUserNumber());
//        }

        return phoneInfos;
    }

    public static boolean contains(char[] nums, char chr) {
        boolean contains = false;
        for (char ichr : nums) {
            if (ichr == chr) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    public static String processMobileNumber(String mobileNumber) {
        char[] nums = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
        StringBuilder builder = new StringBuilder();
        int length = mobileNumber.length();
        for (int i = 0; i < length; i++) {
            char chr = mobileNumber.charAt(i);
            boolean contains = contains(nums, chr);
            if (contains) {
                builder.append(chr);
            }
        }
        String newMobileNumber = builder.toString();
        return newMobileNumber;
    }



}
