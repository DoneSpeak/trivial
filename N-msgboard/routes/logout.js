/*
登出用户
*/
var express = require('express');
var router = express.Router();

var checkLogin = require('../middlewares/check').checkLogin;

//退出用户
// get /logout
router.get('/',checkLogin,function(req, res, next){
	//删除session
	req.session.user = null;
	//返回登录页
	res.redirect('/msgs');
});

module.exports = router;