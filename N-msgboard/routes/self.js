/*
个人页面
*/
var express = require('express');
var router = express.Router();
// 留言模块
var MsgModel = require('../models/msg');
// 评论模块
var CommentModel = require('../models/comment');
//用户模块
var UserModel = require('../models/user');
// 是否检查模块
var checkLogin = require('../middlewares/check').checkLogin;

/*************** 留言 ******************/
//获取指定用户所有留言
// get /self?writer=
router.get('/',function(req, res, next){
	var writer = req.query.writer;
	if(!writer){
		if(!req.session.user){
			return res.redirect('/login');
		}
		writer = req.session.user._id;
	}
	
	MsgModel.getMsgs(writer)
		.then(function(msgs){
			UserModel.getUserById(writer)
				.then(function(user){
					// res.send(writer);
					res.render('self',{
						writer: user,
						msgs: msgs
					});
				})
			
		})
		//接受异常，一直往下抛到下一个组件，最后必要抛到栈底的错误处理中间件 -- error
		.catch(next);

	// if(req.query.writer){
	// 	writer = req.query.writer;

	// 	MsgModel.getMsgs(writer)
	// 		.then(function(msgs){
	// 			res.render('self',{
	// 				msgs: msgs
	// 			});
	// 		})
	// 		//接受异常，一直往下抛到下一个组件，最后必要抛到栈底的错误处理中间件 -- error
	// 		.catch(next);
	// }else{
	// 	MsgModel.getMsgByUserName(writer)
	// 		.then(function(msgs){
	// 			res.render('self',{
	// 				msgs: msgs
	// 			});
	// 		})
	// 		//接受异常，一直往下抛到下一个组件，最后必要抛到栈底的错误处理中间件 -- error
	// 		.catch(next);
	// }

	
});

module.exports = router;