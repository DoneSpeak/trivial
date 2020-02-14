/*
注册用户信息的中间件
*/
var path = require('path');
var sha1 = require('sha1');
var express = require('express');
var router = express.Router();

var UserModel = require('../models/user');
var checkNotLogin = require('../middlewares/check').checkNotLogin;

//get 注册页
router.get('/',checkNotLogin,function(req, res, next){
    //先执行函数checkNotLogin,再进入该函数
    //checkNotLogin 判断用户登录状态，并进行相应跳转
    res.render('signin');
});
//post 注册操作
router.post('/',checkNotLogin,function(req, res, next){
    //如果是post '/' 执行该中间件，先执行回调函数 checkNotLogin 进行权限判断，在执行该函数
    //先要获取用户注册需要的信息
    var name = req.fields.name;
    var gender = req.fields.gender;
    var bio = req.fields.bio;
    var avatar = req.files.avatar.path.split(path.sep).pop();
    var password = req.fields.password;
    var repassword = req.fields.repassword;

    //检验参数是否输入错误
    try{
        //用户名称限制长度为 1 ~ 10
        if(name.length < 1 || name.length > 10){
            throw new Error('名字长度在1-10个字符');
        }
        //性别为选择
        if(['m','f','x'].indexOf(gender) === -1){
            throw new Error('性别只能是: 男/女/保密');
        }
        //个性签名限制长度 1 ~ 30
        if(bio.length < 1 || bio.length > 30){
            throw new Error('个性签名在1-30个字符');
        }
        //头像是否上传 [?]是否设置可不上传头像，使用默认头像
        if(!req.files.avatar.name){
            throw new Error('请上传头像');
        }
        //密码长度不足
        if(password.length < 6){
            throw new Error('密码至少6个字符');
        }
        //密码两次输入不同
        if(password !== repassword){
            throw new Error('两次输入密码不一致');
        }
    }catch(e){
        //设置消息通知key为 error,并设置相应的值，会传递到res.locals.error 
        req.flash('error',e.message);
        //回到登陆界面
        return res.redirect('/signin');
    }

    //密码加密保存
    password = sha1(password);

    //生成用户对象，用于插入数据库
    var user = {
        name:name,
        password:password,
        avatar:avatar,
        gender:gender,
        bio:bio
    };

    //利用mongolass 的 UserModel 将 用户记录插入到数据库中
    UserModel.create(user)
        .then(function(result){
            //数据插入完成后需要生成cookie和session，并进行页面跳转
            user = result.ops[0];
            //存入session
            delete user.password;
            req.session.user = user;
            // 设置 flash 的 success 值，将设置到 res.local.success中
            // req.flash('success','注册成功');
            //跳转到首页
            return res.redirect('/msgs');
        })
        .catch(function(e){
            //用户名已被占用
            if(e.message.match('E11000 duplicate key')){
                req.flash('error','用户名已被占用');
                return res.redirect('/signin');
            }
            next(e);
        });
});

module.exports = router;
