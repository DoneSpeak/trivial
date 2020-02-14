/*
用户模块
操作用户数据文件
*/
var User = require('../lib/mongo').User;

module.exports = {
  //[数据库操作] 插入一条用户记录
  create: function create(user) {
    return User.create(user).exec();
  },

  //[数据库操作] 通过用户名获取用户信息
  getUserByName: function getUserByName(name) {
    return User
      .findOne({ name: name })
      .addCreatedTime()
      .exec();
  },

  //[数据库操作] 通过用户id获取用户信息
  getUserById: function getUserById(id) {
    return User
      .findOne({ _id: id })
      .exec();
  }
};
