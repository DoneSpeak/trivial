package donespeak.com.netsourcedownload;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.io.File;

import java.nio.charset.Charset;


import cpdetector.io.ASCIIDetector;
import cpdetector.io.ByteOrderMarkDetector;
import cpdetector.io.CodepageDetectorProxy;
import cpdetector.io.JChardetFacade;
import cpdetector.io.ParsingDetector;
import cpdetector.io.UnicodeDetector;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

/**
 * Created by DoneSpeak on 2016/5/8.
 */
public class Helper {

    //检查urlString是否合法，同时返回不合法信息
    public static int checkHTTP(String urlString){
        try{
            URL target =  new URL(urlString);
            if(target == null || !target.getProtocol().toUpperCase().equals("HTTP")){
                return HttpError.NOTHTTP;
            }
            return HttpError.CORRECT;
        }catch(MalformedURLException m){

            return HttpError.FORMATERROR;
        }
    }

    //判断是否有sd卡
    public static boolean hasSDcard(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
            //注意这存在sd卡是表示sd卡可以写入或只能读取，所以如果需要写入数据，这个函数判断是不好的
            return true;
        }
        return false;
    }

    //android获得打开文件的intent
    public static Intent getIntentToOpenFile( String filePath,String contentType)throws Exception {

        if(contentType == null || contentType.length() == 0){

            return null;

        }else if(contentType.equals("text/html")){
            Log.i("runnable","我是html,想用web流浪器打开");
            return getHtmlFileIntent(filePath,contentType);
//            return startBrower(filePath);

        }else if(contentType.equals("text/plain")){

            return getTextFileIntent(filePath, false);

        }else if(contentType.equals("application/vnd.android.package-archive")) {

            return getApkFileIntent(filePath);

        }else if(contentType.startsWith("image")) {

            return getImageFileIntent(filePath,contentType);

        }else if(contentType.startsWith("audio")) {

            return getAudioFileIntent(filePath,contentType);

        }else if(contentType.startsWith("video")) {

            return getVideoFileIntent(filePath,contentType);

        }

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri,contentType);
        return intent;
    }

    //android获得打开apk文件的intent
    public static Intent getApkFileIntent(String filePath)throws Exception{
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri,"application/vnd.android.package-archive");
        return intent;
    }

    //android获得打开Text文件的intent
    public static Intent getTextFileIntent(String filePath,boolean paramBoolean)throws Exception{

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = null;
        if(paramBoolean){
            uri = Uri.parse(filePath);
        }else{
            uri = Uri.fromFile(new File(filePath));
        }
        intent.setDataAndType(uri, "text/plain");
        return intent;
    }

    //android获得打开Html文件的intent
    public static Intent getHtmlFileIntent(String filePath,String contentType)throws Exception{

        Log.i("runnable","我在检查");
        Log.i("runnable","filePath: " + filePath);
        Log.i("runnable", "contentType: " + contentType);

        Intent intent=new Intent();
        intent.setAction("android.intent.action.VIEW");
        //content可能是较低版本的android才可以打开的
//        Uri uri = Uri.parse("content://com.android.htmlfileprovider" + filePath);
        Uri uri = Uri.parse("file://" + filePath);
        intent.setData(uri);
        intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
        return intent;
//        Uri uri = Uri.parse(filePath)
//                .buildUpon()
//                .encodedAuthority("com.android.htmlfileprovider")
//                .scheme("content")
//                .encodedPath(filePath)
//                .build();
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.setDataAndType(uri,contentType);
//        return intent;
    }


    public static Intent startBrower(String filePath)throws Exception{
        Uri uri = Uri.parse(filePath);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
         /*其实可以不用添加该Category*/
        intent.addCategory("android.intent.category.BROWSABLE");
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         /*如果想用浏览器打开本地html文件的话，则只能通过显式intent启动浏览器*/
        boolean explicitMode=false;
        String scheme=uri.getScheme();
        if(scheme!=null&&scheme.startsWith("file")) {
            explicitMode=true;
        }
        if(explicitMode) {
            intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
        } else {
            intent.addCategory("android.intent.category.BROWSABLE");
        }
        return intent;
    }

    //android获得打开video文件的intent
    public static Intent getVideoFileIntent(String filePath,String contentType)throws Exception{
        Intent intent =  new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot",0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri,contentType);
        return intent;
    }

    //android获得打开audio文件的intent。和video只有content-type不同
    public static Intent getAudioFileIntent(String filePath,String contentType)throws Exception{
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot",0);
        intent.putExtra("configchange",0);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri,contentType);
        return intent;
    }

    //android获得打开image文件的intent。和video只有content-type不同
    public static Intent getImageFileIntent(String filePath,String contentType)throws Exception{
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, contentType);
        return intent;
    }





    //

    /*
    获取文件和文件类型区域
     */

    //如果有多个相同的charset类型，可以使用""代替或者指定统一的文件类型，指定的语句需要放在所有相同的语句的第一个位置
    private static final String[][] MIME_StrTable = {
            //{后缀名，MIME类型}
            //Video
            {".3gp", "video/3gpp"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            //mp4 统一使用mp4
            {".mp4", "video/mp4"},
            {".mpg4", "video/*"},
            //mpeg 使用相应的默认程序打开，但不添加文件拓展名
            {"", "video/mpeg"},
            {".mpe", "video/*"},
            {".mpeg", "video/*"},
            {".mpg", "video/*"},

            //audio
            {".m3u", "audio/x-mpegurl"},
            //mp4a-latm 使用相应的默认程序打开，但不添加文件拓展名
            {"", "audio/mp4a-latm"},
            {".m4a", "audio/*"},
            {".m4b", "audio/*"},
            {".m4p", "audio/*"},

            //x-mpeg
            {".mp2", "x-mpeg"},
            {".mp3", "audio/x-mpeg"},

            {".mpga", "audio/mpeg"},
            {".ogg", "audio/ogg"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},

            //text
            //plain 使用相应的默认程序打开，但不添加文件拓展名
            {"", "text/plain"},
            {".c", "text/plain"},
            {".java", "text/plain"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".h", "text/plain"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".sh", "text/plain"},
            {".log", "text/plain"},
            {".txt", "text/plain"},
            {".xml", "text/plain"},

            //统一使用html
            {".html", "text/html"},
            {".htm", "text/html"},

            {".css", "text/css"},

            //image
            //jpeg统一使用jpg
            {".jpg", "image/jpeg"},
            {".jpeg", "image/jpeg"},


            {".bmp", "image/bmp"},
            {".gif", "image/gif"},
            {".png", "image/png"},

            //application
            {"", "application/octet-stream"},
            {".bin", "application/octet-stream"},
            {".class", "application/octet-stream"},
            {".exe", "application/octet-stream"},
            {"class", "application/octet-stream"},

            {".apk", "application/vnd.android.package-archive"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},

            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".jar", "application/java-archive"},
            {".js", "application/x-javascript"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".msg", "application/vnd.ms-outlook"},
            {".pdf", "application/pdf"},
            //vnd.ms-powerpoint 使用相应的默认程序打开，但不添加文件拓展名
            {"", "application/vnd.ms-powerpoint"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},

            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".rtf", "application/rtf"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".wps", "application/vnd.ms-works"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
//		{"", "*/*"}
    };

    static HashMap<String,String> mimeMapKeyIsContentType = null;
    static HashMap<String,String> mimeMapKeyIsExpands = null;

    //创建以content-type为key值的HahsMap
    public static HashMap<String,String> CreateMIMEMapKeyIsContentType(){

        HashMap<String,String> mimeHashMap = new HashMap<String,String>();

        for(int i = 0; i < MIME_StrTable.length; i ++){
            if(MIME_StrTable[i][1].length() > 0 && (!mimeHashMap.containsKey(MIME_StrTable[i][1]))){
                mimeHashMap.put(MIME_StrTable[i][1],MIME_StrTable[i][0]);
            }
        }
        return mimeHashMap;
    }

    //创建以拓展名为key值的HahsMap
    public static HashMap<String,String> CreateMIMEMapKeyIsExpands(){

        HashMap<String,String> mimeHashMap = new HashMap<String,String>();

        for(int i = 0; i < MIME_StrTable.length; i ++){
            if(MIME_StrTable[i][0].length() > 0 && (!mimeHashMap.containsKey(MIME_StrTable[i][0]))){
                mimeHashMap.put(MIME_StrTable[i][0],MIME_StrTable[i][1]);
            }
        }
        return mimeHashMap;
    }

    //获取MIME列表的HashMap，设置Content-type为key值
    public static HashMap<String,String> getMIMEMapKeyIsContentType(){
        //为了防止重复创建消耗时间和消耗资源，将mimeMapKeyIsContentType设置全局变量并赋初值null
        if(mimeMapKeyIsContentType == null){
            mimeMapKeyIsContentType = CreateMIMEMapKeyIsContentType();
        }
        return mimeMapKeyIsContentType;
    }

    //获取MIME列表的HashMap,设置拓展名为文件拓展名（含有"."）
    public static HashMap<String,String> getMIMEMapKeyIsExpands(){
        //为了防止重复创建消耗时间和消耗资源，将mimeMapKeyIsExpands设置全局变量并赋初值null
        if(mimeMapKeyIsExpands == null){
            mimeMapKeyIsExpands = CreateMIMEMapKeyIsExpands();
        }
        return mimeMapKeyIsExpands;
    }

    //通过拓展名获取拓展名
    public static String getContentType(String expandName){
        String expands = expandName;
        if(!expandName.startsWith(".")){
            expands = "." + expandName;
        }
        HashMap<String,String> expandMap = getMIMEMapKeyIsExpands();
        String contentType = expandMap.get(expands);
        return contentType == null?"":contentType; //当找不到的时候就会返回空
    }

    //通过文件名获取拓展名
    public static String getExtension(String fileName){
        int lastIndexOfDot = fileName.lastIndexOf(".");
        if(lastIndexOfDot < 0)
            return "";//没有拓展名
        String extension = fileName.substring(lastIndexOfDot+1);
        return extension;
    }

    //通过content-type获取拓展名，不含"."
    public static String getExpandName(String contentType){
        if(!contentType.startsWith(".")){
            contentType = "." + contentType;
        }
        HashMap<String,String> expandMap = getMIMEMapKeyIsContentType();
        return expandMap.get(contentType); //当找不到的时候就会返回空
    }

    //获取文件名出错时，返回空
    public static String getFileNameFromUrl(String urlStr){
        String fileName = null;
        fileName =  getFileNameWithContentTypeFromUrl(urlStr);
        return fileName.substring(fileName.indexOf("#") + 1);
    }

    //更具url字符串获取下载之后的文件名（含有拓展名）和文件类型（需要传入一个contentType的字符串变量来获取值）
    //contentType可以返回null
    public static String getFileNameWithContentTypeFromUrl(String urlStr){
        String contentType = null;
        //不含有[.]的符号字符串
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\]/<>?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        HashMap<String,String> contentTypeMap = new HashMap<String,String>();
        contentTypeMap =  CreateMIMEMapKeyIsContentType();

        HashMap<String,String> extensionMap = new HashMap<String,String>();
        extensionMap =  CreateMIMEMapKeyIsExpands();
        Log.i("runnable", "开始获取文件名");
        //一个完整的url必然会有 "/"
        String[] str = urlStr.trim().split(regEx);
        String expands = "";
        for(int i = str.length-1; i >= 0; i --){
//            System.out.println("第" + i + "部分：" + str[i]);//DEBUG
            if(str[i].contains(".")){
                expands = str[i].substring(str[i].lastIndexOf("."));
                System.out.println("expands:" + expands);
                if(expands.length() > 0 && extensionMap.containsKey(expands)) {
                    if(str[i].endsWith(".html")){
                        String name = getHtmlFileTitle(urlStr);
                        if(name != null){
                            Log.i("runnable", "我通过url得到文件名了");
                            contentType = "text/html";
                            return contentType + "#" + name + ".html";
                        }
                    }
                    Log.i("runnable", "我通过url得到文件名了");
                    Log.i("runnable", expands);
                    contentType = Helper.getContentType(expands.toLowerCase());
                    Log.i("runnable", "马上得到：" + contentType);
                    return contentType + "#" + str[i];


                }
            }
        }
        Log.i("runnable", "我没有通过url得到文件名了");
        //找不到符合的文件名，使用最后路径作为文件名
        String fileName = str[str.length-1];
        //利用content-Type来判断文件类型
        expands = "";

        //字符串截取
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        URLConnection uc = null;
        try {
            uc = url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // String contentType = uc.getContentType(); //这个运行速度有点慢
        contentType = uc.getHeaderField("content-type");
        Log.i("runnable", "拿到contentType");
        //含有content-Type
        if(contentType != null && contentType.length() != 0){ //先判断是否为null,再判断长度是否为0
            System.out.println("contentType： " + contentType);  //DEBUG
            int index = contentType.indexOf(";");
            if(index > 0){
                //含有contentType
                contentType = contentType.substring(0,index);
            }
            expands = contentTypeMap.get(contentType);
            if(expands == null){
                expands = "";
            }
        }

        if(expands.equals(".html")){
            Log.i("runnable", "原来我是html呀");
            String name = getHtmlFileTitle(urlStr);
            if(name != null){
                fileName = name;
            }
        }
        Log.i("runnable", "拿到文件名");
        return contentType + "#" +  fileName + expands;
    }


    //获取Html文件名--title的innerHTML
    public static String getHtmlFileTitle(String urlStr){
        BufferedInputStream bfin = null;
        String title = null;
//    	System.out.println("+++++++++++++++++");  //DEBUG
        try {
            HttpURLConnection conn = null;
            BufferedReader rd = null;

            String encode = getFileEncoding(new URL(urlStr));
            URL url = new URL(urlStr);

            try{
                conn = (HttpURLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5 * 1000);// 设置连接超时
                conn.setReadTimeout(20 * 1000);
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), encode));
            }catch(Exception e){
                //当网页设置权限限制爬虫抓取网页，设置服务代理，绕过限制
                System.out.println("需要服务代理");
                conn = (HttpURLConnection)new URL(urlStr).openConnection();
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5 * 1000);// 设置连接超时
                conn.setReadTimeout(20 * 1000);
                conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), encode));
            }
            //setRequestPropert

            Log.i("runnable", "开始找文件名吧！");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            String line = null;
            StringBuffer htmlSb = new StringBuffer();


            while ((line = rd.readLine()) != null) {
//				System.out.print(line+"\r\n");
                htmlSb.append(line);
                if(line.contains("</title>"))
                {
                    String temp = htmlSb.toString().trim().replaceAll("([(\r)(\n)]*)","").replaceAll(".*<title>\\s*", "");
                    title = temp.replaceAll("\\s*</title>.*", "");
                    title = title.replaceAll("[ ]+", " ");
                    break;
                }
            }
            Log.i("runnable", "找到html文件名了！耶！");
        } catch (IOException e) {
            Log.i("runnable", "不！怎么会出错呢！");

            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(bfin != null){
                try {
                    bfin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        return title;
    }

    //通过url获取网页编码,利用第三方包cpdetector,仅可以用于http协议的网页
    public static String getFileEncoding(URL url){
        try {
            CodepageDetectorProxy codepageDetectorProxy = CodepageDetectorProxy.getInstance();

            codepageDetectorProxy.add(JChardetFacade.getInstance());
            codepageDetectorProxy.add(ASCIIDetector.getInstance());
            codepageDetectorProxy.add(UnicodeDetector.getInstance());
            codepageDetectorProxy.add(new ParsingDetector(false));
            codepageDetectorProxy.add(new ByteOrderMarkDetector());
            Charset charset=null;
            charset = codepageDetectorProxy.detectCodepage(url);
            if(charset.equals(null)){
                return "UTF-8";
            }
            return charset.name();
        } catch (IOException e) {
            System.out.println("利用cpdetector获取网页编码失败！\n"+"使用默认编码UTF-8");
//  			e.printStackTrace();
            return "UTF-8";
        }
    }
}
