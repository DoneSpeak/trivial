/*
作用：对留言进行处理

// ------------ 留言 ----------------//
//获取所有用户所有留言
//获取指定用户所有留言

//获取一条留言 - 页
//发表一条留言 - 提交
//编辑一条留言 - 页
//编辑一条留言 - 提交
//删除一条留言 - 提交

// ------------ 评论 -------------- //
//创建一条评论
//删除一条评论

*/

var express = require('express');
var router = express.Router();
// 留言模块
var MsgModel = require('../models/msg');
// 评论模块
var CommentModel = require('../models/comment');
// 是否检查模块
var checkLogin = require('../middlewares/check').checkLogin;

/*************** 留言 ******************/
//获取所有用户或者指定用户所有留言
// get /msgs?writer=
router.get('/',function(req, res, next){
	// res.send('msg-index');

	var writer = req.query.writer;

	MsgModel.getMsgs(writer)
		.then(function(msgs){
			res.render('msgs',{
				msgs: msgs
			});
		})
		//接受异常，一直往下抛到下一个组件，最后必要抛到栈底的错误处理中间件 -- error
		.catch(next);
});

//获取一条留言 - 页
//get /msgs/:msgId
router.get('/:msgId',function(req, res, next){
	// res.send('msg');
	var msgId = req.params.msgId;
	// res.send(req.path);
	// res.send(req.originalUrl);
	//从数据库中获取留言
	//从数据库中获取留言下的评论
	//浏览次数加一
	Promise.all([
		MsgModel.getMsgById(msgId),
		CommentModel.getComments(msgId),
		MsgModel.incViewCount(msgId)
	])
	.then(function(result){
		//获取得到的数据
		var msg = result[0];
		var comments = result[1];
		if(!msg){
			// 判断请求是否有效
			//评论者在发布者删除之后进行访问
			throw new Error('留言不存在');
		}
		//将结果进行渲染
		res.render('comments',{
			msg:msg,
			comments:comments
		});
	})
	.catch(next);
});

//发表一条留言 - 提交
//post /msgs
router.post('/',checkLogin,function(req, res, next){
	// res.send('msgs');
	//获取所需信息
	var writer = req.session.user._id;
	var content = req.fields.content;
	//校验信息合法性
	try{	
		if(!content.length){
			throw new Error('留言不能为空');
		}
	}catch(e){
		req.flash('error',e.message);
		return res.redirect('back');
	}
	//插入数据库
	var msg = {
		writer: writer,
		content: content,
		editedTimestamp: Date.now(),
		viewCount: 0
	};

	MsgModel.create(msg)
		.then(function(result){
			//将插入插入成功回馈的值返回页面
			res.redirect('/msgs');
		})
		.catch(next);
	//返回页面
});
//编辑一条留言 - 页
//get /msgs/:msgId/edit
router.get('/:msgId/edit',checkLogin,function(req, res, next){
	// res.send('edit');
	//获取相关信息
	var msgId = req.params.msgId;
	var writer = req.session.user._id;
	
	MsgModel.getRawMsgById(msgId)
		.then(function(msg){
			if(!msg){
				throw new Error('该文章不存在');
			}
			if(writer.toString() !== msg.writer._id.toString()){
				throw new Error('权限不足');
			}
			var wordNum = msg.content.length;
			res.render('edit',{
				msg:msg,
				wordNum:wordNum
			});
		})
		.catch(next);
	//计算评论长度
	//渲染
});
//编辑一条留言 - 提交
//post /msgs/:msgId/edit
router.post('/:msgId/edit',checkLogin,function(req, res,next){
	// res.send('post-edit');

	//获取相关数据
	var msgId = req.params.msgId;
	var writer = req.session.user._id;
	var content = req.fields.content;
	var editedTimestamp = Date.now();

	if(!content){
		req.flash('error','内容不能为空');
		res.redirect('back');
	}
	//更新到数据库
	MsgModel.updateMsgById(msgId, writer,{content:content,editedTimestamp:editedTimestamp})
		.then(function(msg){
			//重定向到该留言页面
			//反引号表示可执行片段
			res.redirect(`/msgs/${msgId}`);
		})
		.catch(next);
})
//删除一条留言 - 提交
router.get('/:msgId/remove',checkLogin,function(req, res, next){
	// res.send('delete');
	//获取相关数据
	var msgId = req.params.msgId;
	var writer = req.session.user._id;

	//[数据库操作] 删除
	MsgModel.delMsgById(msgId, writer)
		.then(function(){
			//删除该该留言下的所有评论
			CommentModel.delCommentByMsgId(msgId,writer)
				.then(function(){
					// req.flash('success','留言删除成功');
					//重定向到主页
					res.redirect('/msgs');
				})
		})
		.catch(next);
})

/*************** 评论 ******************/
//创建一条评论
//msgs/:msgId/comment
router.post('/:msgId/comment',checkLogin,function(req, res, next){
	// res.send('comments');
	//获取相关数据
	var msgId = req.params.msgId;
	var writer = req.session.user._id;
	var content = req.fields.content;
	var comment = {
		writer: writer,
		msgId: msgId,
		content: content
	};

	CommentModel.create(comment)
		.then(function(){
			// req.flash('success','评论成功');
			//回到原来的页面，也就是离开该操作路径
			res.redirect('back');
		})
		.catch(next);
});
//删除一条评论,通过留言id和评论id定位一条评论
//get /msgs/:msgId/comment/:commentId/remove
router.get('/:msgId/comment/:commentId/remove',checkLogin,function(req, res, next){
	// res.send('remove comment');
	//获取可以定位该评论的数据
	var commentId = req.params.commentId;
	var writer = req.session.user._id;

	CommentModel.delCommentById(commentId,writer)
		.then(function(){
			// req.flash('success','删除评论成功');
			res.redirect('back');
		})
		.catch(next);
});	

module.exports = router;