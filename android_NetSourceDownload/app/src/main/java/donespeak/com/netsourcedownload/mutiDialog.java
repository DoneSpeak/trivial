package donespeak.com.netsourcedownload;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.widget.Toast;

/**
 * Created by glorior on 2016/5/9.
 */
public class mutiDialog {

    public static void cteateDialog(Context context, CharSequence title,@DrawableRes int iconId,CharSequence msgStr,CharSequence btnStr ){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(iconId)
                .setMessage(msgStr)
                .setPositiveButton(btnStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do Nothing
                    }
                }).create().show();
    }
    public static void cteateTipDialog(Context context,String msgStr,String btnStr){
        new AlertDialog.Builder(context)
            .setTitle(R.string.tip)
            .setIcon(R.drawable.wrenw)
            .setMessage(msgStr)
            .setPositiveButton(btnStr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Do Nothing
                }
            }).create().show();
    }

    public static void cteateWarningDialog(Context context,String msgStr,String btnStr){
        new AlertDialog.Builder(context)
                .setTitle(R.string.warning)
                .setIcon(R.drawable.warning)
                .setMessage(msgStr)
                .setPositiveButton(btnStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do Nothing
                    }
                }).create().show();
    }

    public static void createDownloadFinishDialog(Context context, final String filePath, final String contentType){
        new AlertDialog.Builder(context)
                .setTitle(R.string.tip)
                .setIcon(R.drawable.wrenw)
                .setMessage("下载完成")
                .setPositiveButton("查看", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNeutralButton("打开路径", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which){

                    }
                })
                .create().show();

        }
    }
