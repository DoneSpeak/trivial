/*
留言模块
操作留言数据文件
*/

var marked = require('marked');
var Msg = require('../lib/mongo').Msg;
var UserModel = require('./user');
var CommentModel = require('./comment');

// 给 msg 添加评论数 commentsCount 属性
Msg.plugin('addCommentsCount', {
  afterFind: function (msgs) {
    return Promise.all(msgs.map(function (msg) {
        return CommentModel.getCommentsCount(msg._id)
                .then(function (commentsCount) {
                    //将执行完成后的结果作为 msg 的一个属性
                    msg.commentsCount = commentsCount;
                    return msg;
                });
    }));
  },
  afterFindOne: function (msg) {
    if (msg) {
        return CommentModel.getCommentsCount(msg._id)
                .then(function (count) {
                    //将执行结果作为 msg 的评论数属性
                    msg.commentsCount = count;
                    return msg;
                });
    }
    return msg;
  }
});

// 将 msg 的 content 从 markdown 转换成 html
Msg.plugin('contentToHtml', {
  afterFind: function (msgs) {
    return msgs.map(function (msg) {
        msg.content = marked(msg.content);
        return msg;
    });
  },
  afterFindOne: function (msg) {
        if (msg) {
            msg.content = marked(msg.content);
        }
        return msg;
    }
});

module.exports = {
  // [数据库操作] 向数据库插入一条留言
  create: function create(msg) {
    return Msg.create(msg).exec();
  },

  // 通过留言 id 获取一条留言
  getMsgById: function getMsgById(msgId) {
    return Msg
      .findOne({ _id: msgId })
      .populate({ path: 'writer', model: 'User' })
      .addCreatedTime()      
      .addEditedTime()
      .addCommentsCount()
      .contentToHtml()
      .exec();
  },

  // 按创建时间降序获取所有用户留言或者某个特定用户的所有留言
  getMsgs: function getMsgs(writer) {
    var query = {};
    if (writer) {
        //设置查询条件
        query.writer = writer;
    }
    return Msg
      .find(query)
      .populate({ path: 'writer', model: 'User' })
      .sort({ _id: -1 })
      .addCreatedTime()
      .addEditedTime()
      .addCommentsCount()
      .contentToHtml()
      .exec();
  },

  // 通过用户名获取用户所有留言
  getMsgByUserName: function getMsgByUserName(name) {
      var user = UserModel.getUserByName(name);
      var query = {};
      query.writer = user._id;
      return Msg
        .find(query)
        .populate({ path: 'writer', model: 'User' })
        .sort({ _id: -1 })
        .addCreatedTime()
        .addCommentsCount()
        .contentToHtml()
        .exec();
  },

  // 通过留言 id 给 viewCount 加 1
  incViewCount: function incViewCount(msgId) {
    return Msg
      .update({ _id: msgId }, { $inc: { viewCount: 1 } })
      .exec();
  },

  // 通过留言 id 获取一篇makedown 原生文章
  getRawMsgById: function getRawMsgById(msgId) {
    return Msg
      .findOne({ _id: msgId })
      .populate({ path: 'writer', model: 'User' })
      .exec();
  },

  // 通过用户 id 和文章 id 更新一条留言
  updateMsgById: function updateMsgById(msgId, writer, data) {
    return Msg.update({ writer: writer, _id: msgId }, { $set: data }).exec();
  },

  // 通过用户 id 和文章 id 删除一条留言
  delMsgById: function delMsgById(msgId, writer) {
    return Msg.remove({ writer: writer, _id: msgId}).exec();
  }
};
