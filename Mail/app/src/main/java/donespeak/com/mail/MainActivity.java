package donespeak.com.mail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String userEmail = "";
    public static String userPassword = "";

    public static final int RECIPIENTTYPE_TO = 0;
    public static final int RECIPIENTTYPE_CC = 1;
    public static final int RECIPIENTTYPE_BCC = 2;

    public static boolean needRefresh = false;

    public static final int BOXTYPE_DELETED = 4;
    public static final int BOXTYPE_SPAM = 3;
    public static final int BOXTYPE_DRAFTS = 2;
    public static final int BOXTYPE_SENT = 1;
    public static final int BOXTYPE_INBOX = 0;

    public static Fragment cerentFragment = null;

    public static final Boolean DEBUG = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //工具栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("收件箱");

        //侧边导航栏
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //读取sharePreferences，获得用户名和用户密码
        SharedPreferences sharedPreferences= getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        userEmail =sharedPreferences.getString("userEmail", ""); //第二个参数为默认值
        userPassword =sharedPreferences.getString("userPassword", "");

        //设置第一项被选中，也就是收件箱被选中
//        navigationView.setSelected(true);
        MenuItem item = navigationView.getMenu().getItem(0);
        item.setChecked(true);
        //设置收件箱为进入状态的Fragment
        switchFragmentSupport(R.id.content, tabs[0]);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_writemail) {
            if(MainActivity.userEmail != null && MainActivity.userEmail.length() != 0) {
                Intent intent = new Intent(MainActivity.this, Activity_WriteMail.class);
                startActivity(intent);
            }else{
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("未登陆")
                        .setMessage("您尚未登陆邮箱" + "\r\n请登陆之后再写邮件")
                        .setPositiveButton("确定", null)
                        .show();
            }
        }
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private String tabs[] = {"inbox","sendMail","flagMail","addrbook","drafts","spam","deleteMail"};
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //打开相应的fragment
        switch(id){
            //tabs[] = {"inbox","sendMail","flagMail","addrbook","drafts","spam","deleteMail"};
            case R.id.nav_inbox:{
                setTitle("收件箱");
                switchFragmentSupport(R.id.content, tabs[0]);
                break;
            }case R.id.nav_sendmail:{
                setTitle("已发送");
                switchFragmentSupport(R.id.content, tabs[1]);
                break;
            }case R.id.nav_flagmail:{
                setTitle("红旗邮件");
                switchFragmentSupport(R.id.content, tabs[2]);
                break;
            }case R.id.nav_addrbook:{
                setTitle("通讯录");
                switchFragmentSupport(R.id.content, tabs[3]);
                break;
            }case R.id.nav_drafs:{
                setTitle("草稿箱");
                switchFragmentSupport(R.id.content, tabs[4]);
                break;
            }case R.id.nav_spam:{
                setTitle("垃圾邮件");
                switchFragmentSupport(R.id.content, tabs[5]);
                break;
            }case R.id.nav_deletemail:{
                setTitle("已删除");
                switchFragmentSupport(R.id.content, tabs[6]);
                break;
            }case R.id.nav_setting:{
                Intent intent = new Intent(MainActivity.this, Activity_log.class);
                startActivity(intent);
                break;
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
    *动态切换组件中显示的界面
     * @param containerId 待切换界面的布局控件
     * @param tag 目标Fragment的标签名称
     */
    //tabs[] = {"inbox","sendMail","flagMail","addrbook","drafts","spam"deleteMail"};
    public void switchFragmentSupport(int containerId,String tag){
        //获取FragmentManger管理器
        FragmentManager manager = getSupportFragmentManager();
        //根据tag标签名查找是否已存在对应的Fragment对象
        Fragment destFragment = manager.findFragmentByTag(tag);
        //如果tag标签对应的Fragment对象不存在，则初始化
        Log.i("tag",tag);
        //利用多个fragment独立生成
        if(destFragment == null){
            if(tag.equals(tabs[0])){
                Log.i("tag","在inbox");
                destFragment = new Fragment_inbox();
            }else if(tag.equals(tabs[1])){
                //TODO
                Log.i("tag","在sendedMail");
                destFragment = new Fragment_sentMail();
            }else if(tag.equals(tabs[2])){
                Log.i("tag","在flagMail");
                destFragment = new Fragment_flagMail();
            }else if(tag.equals(tabs[3])){
                Log.i("tag","在addrbook");
                destFragment = new Fragment_addrbook();
            }else if(tag.equals(tabs[4])){
                Log.i("tag","在drafts");
                destFragment = new Fragment_drafts();
            }else if(tag.equals(tabs[5])){
                Log.i("tag","在spam");
                destFragment = new Fragment_spam();
            }else if(tag.equals(tabs[6])){
                Log.i("tag","在deleted");
                destFragment = new Fragment_deletedMail();
            }
        }

        //单一Fragment做适配
//        if(destFragment == null){
//            FragmentForAll oneFragment = new FragmentForAll();
//
//            if(tag.equals(tabs[0])){
//                Log.i("tag","在inbox");
//                oneFragment.setFragmentForAll(R.layout.fragment_inbox,"INBOX");
//            }else if(tag.equals(tabs[1])){
//                //TODO
//                Log.i("tag","在sendedMail");
//                oneFragment.setFragmentForAll(R.layout.fragment_sentmail, "已发送");
//                destFragment = oneFragment;
//            }else if(tag.equals(tabs[2])){
//                Log.i("tag","在flagMail");
//                //TODO 需要事项标记邮箱
//                destFragment = new Fragment_flagMail();
//            }else if(tag.equals(tabs[3])){
//                Log.i("tag","在addrbook");
//                //需要实现通讯录
//                destFragment = new Fragment_addrbook();
//            }else if(tag.equals(tabs[4])){
//                Log.i("tag", "在drafts");
//                String substr = userEmail.substring(userEmail.lastIndexOf("@") + 1);
//                String bt = "草稿箱";
//                if(substr.substring(0,substr.indexOf(".")).equalsIgnoreCase("sina")){
//                    bt = "草稿夹";
//                }
//                oneFragment.setFragmentForAll(R.layout.fragment_drafts,bt);
//                destFragment = oneFragment;
//            }else if(tag.equals(tabs[5])){
//                Log.i("tag","在spam");
//                oneFragment.setFragmentForAll(R.layout.fragment_spam, "垃圾邮件");
//                destFragment = oneFragment;
//            }else if(tag.equals(tabs[6])){
//                Log.i("tag","在deleted");
//                oneFragment.setFragmentForAll(R.layout.fragment_deletedmail, "已删除");
//                destFragment = oneFragment;
//            }
//
//        }

        Toast.makeText(this,tag,Toast.LENGTH_LONG).show();
        //获取FragmentTransaction事务对象
        FragmentTransaction ft = manager.beginTransaction();
        //将组件id为containerId的内容替换为destFragment,并把desFragment的标签设置为tag变量值
        //标签标记失败，实际测试中总是没有找到。只有点击同一个选项的时候才会有
        ft.replace(containerId,destFragment,tag);


        //TODO 测试是否需要使用下面的两行代码
        //设置Fragment切换效果
//        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        //将状态保持到回退栈，这样按下back键时将返回到前一个Fragment界面
//        ft.addToBackStack(null);
        ft.commit();
    }
}
