//markdown转化模块
var marked = require('marked');
//数据库中的评论表格
var Comment = require('../lib/mongo').Comment;

// 利用 mongodb 的 可以自定插件功能
// 将 comment 的 content 从 markdown 转换成 html
Comment.plugin('contentToHtml', {
  afterFind: function (comments) {
    return comments.map(function (comment) {
    	//找到记录便将其进行如下操作 -- 转化为html
      	comment.content = marked(comment.content);
      	return comment;
    });
  }
});

module.exports = {
  	// 创建一个评论
  	create: function create(comment) {
  		//[数据库操作] 将评论数据插入到数据库
    	return Comment.create(comment).exec();
  	},

  	// 通过用户 id 和评论 id 删除一个评论
  	delCommentById: function delCommentById(commentId, writer) {
    	//[数据库操作] 删除记录
    	return Comment.remove({ writer: writer, _id: commentId }).exec();
  	},

    // 通过用户 id 和留言 id 删除一个留言下的所有评论
    delCommentByMsgId: function delCommentByMsgId(msgId, writer) {
      //[数据库操作] 删除记录
      return Comment.remove({ msgId: msgId, writer: writer }).exec();
    },

  	// 通过留言 id 获取该留言下所有评论，按评论创建时间升序
  	getComments: function getComments(msgId) {
    	return Comment
      	.find({ msgId: msgId })	//查找
      	.populate({ path: 'writer', model: 'User' })	//将writer字段转化为 User 数据记录类型
      	.sort({ _id: 1 })	//按照id (可以映射时间) 升序
      	.addCreatedTime()		//自定义插件，返回创建时间字段- createTime
      	.contentToHtml()	//转化成html格式的内容字段 - content
      	.exec();
  },

  // 通过文章 id 获取该留言下的评论数
  getCommentsCount: function getCommentsCount(msgId) {
  	//[数据库操作] count函数
    return Comment.count({ msgId: msgId }).exec();
  }
};