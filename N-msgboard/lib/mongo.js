/*
指定配置文件
连接数据库

创建各个数据记录文件，及其字段格式
[用户信息文件]{name,password,avatar,gender,bio}
[留言文件]{writer,content,editTime,viewCout}
[评论文件]{writer,content,msgId}
*/
var config = require('config-lite');
//连接数据库
var Mongolass = require('mongolass');
var mongolass = new Mongolass();
mongolass.connect(config.mongodb);
//时间戳处理模块
var moment = require('moment');
//id 转 时间戳 模块，自动截取id中前表示时间部分，并转化为时间
//24 位长的 ObjectId 前 4 个字节是精确到秒的时间戳
var objectIdToTimestamp = require('objectid-to-timestamp');

//给mongolass自定义根据id生成时间的通用插件
//return createTime
mongolass.plugin('addCreatedTime',{
    //对多条记录的操作
    afterFind:function(results){
        results.forEach(function(item){
            item.createdTime = moment(objectIdToTimestamp(item._id)).format('YYYY-MM-DD HH:mm');
        });
        return results;
    },
    //对单条记录的操作
    afterFindOne:function(result){
        if(result){
            //未生成createTime记录生成 createTime
            result.createdTime = moment(objectIdToTimestamp(result._id)).format('YYYY-MM-DD HH:mm');
        }
        return result;
    }
});

//return editTime
mongolass.plugin('addEditedTime',{
    //对多条记录的操作
    afterFind:function(results){
        results.forEach(function(item){
            item.editedTime = moment(item.editedTimestamp).format('YYYY-MM-DD HH:mm');
        });
        return results;
    },
    //对单条记录的操作
    afterFindOne:function(result){
        if(result){
            //未生成createTime记录生成 editTime
            result.editedTime = moment(result.editedTimestamp).format('YYYY-MM-DD HH:mm');
        }
        return result;
    }
});



//[用户信息]
exports.User = mongolass.model('User',{
	name:{type:'string'},
	password:{type:'string'},
	avatar:{type:'string'},
	gender:{type:'string',enum:['m','f','x']},
	bio:{type:'string'}
});
//用户名设置为主键，并且唯一
exports.User.index({name:1},{unique:true}).exec();

//[留言]
exports.Msg = mongolass.model('Msg', {
    writer: { type: Mongolass.Types.ObjectId },
    content: { type: 'string' },
    editedTimestamp:{ type:'number'},
    viewCount: { type: 'number' }
});
//writer设置为主键，并且按照自动生成的_id降序排序，_id开始部分为时间部分，所以这个也是按照时间降序排序
exports.Msg.index({ writer: 1, _id: -1 }).exec();// 按创建时间降序查看用户的文章列表

//[评论]
exports.Comment = mongolass.model('Comment', {
    writer: { type: Mongolass.Types.ObjectId },
    content: { type: 'string' },
    msgId: { type: Mongolass.Types.ObjectId }
});
// 按留言id(创建时间)和评论id（创建时间)升序 -》用户获取留言下的评论
exports.Msg.index({ msgId: 1, _id: 1 }).exec();
// 通过用户 id 和评论 id 升序 --》 用于通过留言id和评论id删除留言
exports.Msg.index({ writer: 1, _id: 1 }).exec();