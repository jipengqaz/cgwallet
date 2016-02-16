package cgtz.com.cgwallet.paymoney;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/24.
 */
public class MainPresenter {

    private MyGridView view;
    private  Context context;
    ArrayList<ProductsBean>  list;
    public String[] price ={"30","50","100","200","300","500"};
    public String[] disPrice ={"暂无","暂无","暂无","暂无","暂无","暂无"};
    public static final String[] PERSON= new String[]{ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER};

    public MainPresenter(Context context ,MyGridView view){
        this.context = context;
        this.view = view;
//        setData();
//        setAdapter();
    }

    //设置数据
    public  ArrayList<ProductsBean> setData() {
         list = new ArrayList<ProductsBean>();
        for (int i=0;i<price.length;i++){
            ProductsBean productsBean = new ProductsBean();
            productsBean.setParValue(price[i]);
            productsBean.setSalePrice(disPrice[i]);
            list.add(productsBean);
        }
        return list;

    }
    public Cursor getCursor(Context context){
        //取得内容解析者
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                PERSON, null, null, null);
        return cursor;
    }


    public String getQueryData(Context context,String name){
        String phoneNum = "";
        // 查联系人姓名这张表，有无用户输入的姓名
        ContentResolver contentResolver1 = context.getContentResolver();
        String[] colmuns1 = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };
        Cursor cursor1 = contentResolver1.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        for (cursor1.moveToFirst(); !(cursor1.isAfterLast()); cursor1.moveToNext()) {
            String contactsName = cursor1.getString(cursor1
                    .getColumnIndex(colmuns1[1]));
            // 如果有，则开始查联系人电话这张表
            if (name.equals(contactsName)) {
                int id = cursor1.getInt(cursor1.getColumnIndex(colmuns1[0]));
                ContentResolver contentResolver2 = context.getContentResolver();
                String[] colmuns2 = new String[] { ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.NUMBER };
                // 查询的联系人电话这张表时候，加入一个条件，就查目录联系人姓名那列数据中的id
                // 由于联系人电话这张表与联系人姓名这张表是同id的
                // 查询参数放在第三个参数这个位置
                Cursor cursor2 = contentResolver2.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, colmuns2[0] + "=" + id, null, null);
                if (cursor2.moveToNext()) {// 如果Cursor中有数据，要把初始位置为0的Cursor下拉一位才能拿到数据
                    phoneNum = cursor2.getString(cursor2
                            .getColumnIndex(colmuns2[1]));

                }
            }
        }
        return phoneNum;// 查到就返回相应的电话，没查到就扔回0
    }

}
