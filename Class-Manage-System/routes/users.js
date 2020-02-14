var db = require('../modules/db');

function User(user) {
  this.password = user.password;
  this.email = user.email;
  this.name = user.name;
  this.sex = user.sex;
  this.role_id = user.role_id;
  this.class_id = user.class_id;
};

module.exports = User;

//存储用户信息
User.prototype.save = function(callback) {
  //要存入数据库的用户文档
  var user = {
      password: this.password,
      email: this.email,
      name: this.name,
      sex: this.sex,
      role_id: this.role_id,
      class_id: this.class_id
  };

  var connection = db.getConnection();
  var deafultRole = 5;
  var retrieveSQL = "select email from user where email=?";
  connection.query(retrieveSQL,[user.email],function(err, rows, fields){
    if(err) throw(err);
    //如果数据库中没有该用户，才允许插入。
    if(rows.length == 0 ){
      var inserSQL = "insert into user(email,password,role_id) values(?,?,?)";
      connection.query(inserSQL,[user.email,user.password,deafultRole], function(err, rows, fields){
        if(err) throw(err);
        console.log('in users.js :插入成功');
        callback(null,user)
      });
    }else{
      callback('exist',user);
    }
  })
};

//读取用户信息
User.get = function(email, callback) {

  var connection = db.getConnection();
  var retrieveSQL = "select * from user where email = ?";
  connection.query(retrieveSQL,[email],function(err, rows, fields){
    if(err) throw(err);
    if(rows.length != 0){
      var newUser = new User({
        password: rows[0].password,
        email: rows[0].email,
        name: rows[0].name,
        sex: rows[0].sex,
        role_id: rows[0].role_id,
        class_id: rows[0].class_id
      });

      callback(err, newUser);
    }else {
      return callback(err);
    }
  });

};

User.update = function(email, name, sex, classNum, callback){

  var connection = db.getConnection();
  var updateSQL = "UPDATE user SET name = ?,sex = ?, class_id = ? where email = ?";
  connection.query(updateSQL, [name, sex, classNum, email], function(err, rows, fileds){
    if(err) throw(err);
    if(rows.length ==0){
      console.log('in user.js: 数据并没有得到更新');
      callback(err);
    }else{
      console.log('in user.js: 成功更新数据,即将返回一个user');
      var searchSQL = "select email, name, sex, role_id, class_id from user where email = ?"
      connection.query(searchSQL,[email],function(err, rows, fields){
        if(err) throw(err);
        var newUser = new User({
          password: rows[0].password,
          email: rows[0].email,
          name: rows[0].name,
          sex: rows[0].sex,
          role_id: rows[0].role_id,
          class_id: rows[0].class_id
        });
        callback(err, newUser);
      });

    }

  })

}
