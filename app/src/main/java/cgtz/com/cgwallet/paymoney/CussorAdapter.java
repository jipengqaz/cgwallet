package cgtz.com.cgwallet.paymoney;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.TextView;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.utils.Utils;


/**
 * Created by penny_yao on 2015/12/29.
 */
public class CussorAdapter extends CursorAdapter /*implements Filterable*/{
    private final String number;
    private Context context;
    private ContentResolver resolver;
    private Cursor cursor;
    private String[] columns = new String[] { ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER};// 一会儿，游标查询出来的表就两列，一列是id，一列是联系人姓名
    TextView tv_num,tv_name;
    String num;
    public CussorAdapter(Context context, Cursor cursor, int flags,String number) {
        // 调用父类构造方法，此处ContactListAdapter(Context context, Cursor
        // c)方法已经过时，多出了一个参数flags
        // 其实都没有什么用，flags扔个0给它就OK
        super(context, cursor, flags);
        this.cursor = cursor;
        this.number = number;
        resolver = context.getContentResolver();// 初始化ContentResolver
        this.context = context;
    }

    // 这里要自己形成一个下拉菜单
    @Override
    public void bindView(View view, Context arg1, Cursor cursor) {
        Log.e("==2=", "=================================");
        num = ContactManager.getQueryData(context, cursor.getString(1).toString(),number);

        tv_num.setText(num);
        tv_name.setText(cursor.getString(1));
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.e("==1=","=================================");
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(
               R.layout.item_auto_adapter, parent, false);
         tv_num =(TextView) view.findViewById(R.id.tv_num);
         tv_name =(TextView) view.findViewById(R.id.tv_name);

//        if(cursor !=null){
//            Log.e("==1=",""+cursor.getString(2));
//            Log.e("==2=",""+cursor.getString(1));//姓名
//            Log.e("==3=",""+cursor.getString(0));
//
//            tv_num.setText(convertToString(cursor));
//            tv_name.setText(cursor.getString(0));
//        }
//        String data1 = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA1));
//        String mimetype = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
//        if (mimetype.equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {
//        } else if (mimetype.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
//            tv_num.setText(cursor.getString(1));
//        }
//        view.setText(cursor.getString(1));// 各项项的文字，就是游标查询出来的，联系人姓名那一列
        return view;
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {

            Log.e("==3=","================================="+cursor.getString(1).toString());
        Log.e("==4=","================================="+num);

        return ContactManager.getQueryData(context, cursor.getString(1).toString(),number);
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        FilterQueryProvider filter = getFilterQueryProvider();

        if (filter != null) {
            if (constraint.toString().equals(Utils.getUserPhone(context))){
               filter.runQuery(constraint);
            }
            return filter.runQuery(constraint);
        }
        // 这里是指明游标的查询的表，与查询结果列
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI,
                Uri.encode(constraint.toString()));
        Log.e("==0=","================================="+constraint.toString());
        return resolver.query(uri, columns, null, null, null);
    }
}

