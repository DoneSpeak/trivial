package donespeak.com.mail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import donespeak.com.mail.bean.MailBean;

/**
 * Created by glorior on 2016/6/21.
 */
//邮件适配器
public class AdapterInbox extends BaseAdapter {
    //待显示邮件列表
    private List<MailBean> mailList;
    private LayoutInflater listContainer;
    public AdapterInbox(Context context, List<MailBean> mailList){
        this.mailList = mailList;
        this.listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文
    }

    //ListView组件根据getCount()方法的返回值来确定显示的总数据条数
    @Override
    public int getCount(){
        return mailList.size();
    }

    //返回第position行条目，这里直接设为MailBean对象
    @Override
    public MailBean getItem(int position){
        return mailList.get(position);
    }

    //返回第position行条目的id,这里直接用position
    @Override
    public long getItemId(int position){
        return position;
    }

    //ListView显示每条数据时，都要调用getView()方法创建视图组件，然后传递给ListView显示
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //[优化]  见《第一行代码Android》第134页
        View view;
        ViewHolderOfMail viewHolder;
        if (convertView == null) {
            view = listContainer.inflate(R.layout.mail_item, null);
            //获得ViewHolder对象
            viewHolder = new ViewHolderOfMail();
            viewHolder.sender = (TextView) view.findViewById(R.id.mail_sender);
            viewHolder.mailTitle = (TextView) view.findViewById(R.id.mail_title);
            viewHolder.mailDate = (TextView) view.findViewById(R.id.mail_date);
            viewHolder.mailIcon = (ImageView) view.findViewById(R.id.mail_icon);

            //将viewHolder对象存入到View中
            view.setTag(viewHolder);
        }else{
            //直接从缓存中读取控件实例,而不用去重新用findViewById去查找
            view = convertView;
            viewHolder = (ViewHolderOfMail)view.getTag();
        }

        //获取第position行的数据,由于是从缓存中读取到数据，所以会存在一些读取不完整的问题
        //TODO 使得ListView的数据完整
        MailBean item = getItem(position);
        String senderStr = item.fromWho;
        if(senderStr == null || senderStr.length() == 0){
            senderStr = item.fromAddress;
        }
        String titleStr = item.title;
        String descriptionStr = item.time;

        //将第position行的数据显示到布局界面中

        viewHolder.sender.setText(senderStr);
        viewHolder.mailTitle.setText(titleStr);
        viewHolder.mailDate.setText(descriptionStr);
        return view;
    }

    class ViewHolderOfMail{
        ImageView mailIcon;
        TextView sender;
        TextView mailDate;
        TextView mailTitle;
//        TextView mailDescription;

    }
}

