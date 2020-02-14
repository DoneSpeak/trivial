/*
登陆
*/
var sha1 = require('sha1');

var express = require('express');
var router = express.Router();

var UserModel = require('../models/user');
var checkNotLogin = require('../middlewares/check').checkNotLogin;
//获取登录界面
//get /
router.get('/',checkNotLogin,function(req, res, next){
	res.render('login');
});
//登录
// post
router.post('/', checkNotLogin, function(req, res, next){
	//或许需要的数据
	var name = req.fields.name;
	var password = req.fields.password;

	//判断是否数据库中已有用户
	UserModel.getUserByName(name)
		.then(function(user){
			if(!user){
				//用户不存在
				req.flash('error','用户不存在');
				return res.redirect('back');
			}
			if(sha1(password) != user.password){
				//用户和密码不匹配
				req.flash('error','用户名或密码错误');
				return res.redirect('back');
			}

			// req.flash('success','登陆成功');

			//用户信息写入session
			delete user.password;
			req.session.user = user;
			//跳至主页
			res.redirect('/msgs');
		})
		.catch(next);
});

module.exports = router;