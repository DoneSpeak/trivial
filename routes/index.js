var express = require('express');
var router = express.Router();
var db = require('../modules/db');
var User = require('./users');
var crypto = require('crypto');

//rights storage.
var rights = new Array();


module.exports = function(app){
  app.get('/', function(req, res) {
    if(req.session.user){
      console.log('in: index.js :该用户利用session登录');
      res.redirect('/main');
    }else{
      res.redirect('/register');
    }
  });

  app.get('/register',function(req, res){
    res.render('register', {info : "请登录"  });
  });

  app.get('/main', function(req, res) {
    if(!req.session.user){
      res.redirect('/register');
    }
    /*
    此处对用户的角色权限进行检索。
    */
    var connection = db.getConnection();
    var retrieveSQL = "select role_name,role_right from user U, roles R1, rights_of_role R2 "+
    "where U.role_id = R1.role_id " +
    "and R2.role_id = R1.role_id " +
    "and R2.role_id = U.role_id " +
    "and U.role_id = ?;";

    connection.query(retrieveSQL, [req.session.user.role_id], function(err, rows, fields){
      if(err) throw(err);
      var rights = new Array(0,0,0,0,0);
      for(var i = 0; i < rows.length; i++){
        if(rows[i].role_right=='开班会'){
          rights[0] = 1;
        }else if(rows[i].role_right =='收班费'){
          rights[1] = 1;
        }else if(rows[i].role_right =='收党费'){
          rights[2] = 1;
        }else if(rows[i].role_right =='评优等生'){
          rights[3] = 1;
        }else if(rows[i].role_right == '考勤结果'){
          rights[4] = 1;
        }
      }
      req.session.user.rights = rights;
      if(typeof(rows[0]) != "undefined"){
        req.session.user.role_name = rows[0].role_name;
      }else{
        req.session.user.role_name = "班级成员";
      }
      res.render('main', {
         user: req.session.user,
        });
    })

  });

  app.get('/root',function(req, res){

    var connection = db.getConnection();
    var findAllRightsSQL = "select * from rights_of_role;"
    /*
      A.检索出所有角色以及其对应权利，存入rights数组中。
      B.接着在回调函数中检索所有用户的信息以及角色。
    */
    connection.query(findAllRightsSQL,function(err, rows, fields){
      if(err) throw(err);
      //根据SQL查询的结果，将所有角色的权限存入rights数组中
      rights = findAllRightsOfRole(rows);

      var findUsersSQL = "select user_id, name, email, role_name "+
      "from user U, roles R1 "+
      "where U.role_id = R1.role_id;"
      connection.query(findUsersSQL, function(err, users, fields){
        if(err) throw(err);

        res.render('root',{
          user : req.session.user,
          allrights : rights,
          alluser: users
        })
      });
    })
  })

  app.post('/root',function(req, res){

    //如果点击的是角色权限修改
    if(req.body.postBtn == "roleChanger"){
      var hiddenData = req.body.data;
      var newRights = new Array();
      //根据客户端发送的隐藏数据，重新组合rights数组。
      restoreTheRightsArrayAccordingToHiddenData(newRights,hiddenData);

      var connection = db.getConnection();
      var temp = 0;
      var deleteSQL = "delete from rights_of_role where role_right= ? and role_id = ? ;";
      var insertSQL = "insert into rights_of_role(role_right, role_id) values( ? , ? ); "
      var role_right = undefined;
      for(var i=0;i<4;i++){
        for(var j=0;j<5;j++){
          temp = newRights[i][j] - rights[i][j];
          if(temp==-1){
            role_right = findTheRole_right(j);
            console.log(role_right);
            connection.query(deleteSQL,[role_right,i+1 ],function(err, rows, fields){
              if(err) throw(err);
              console.log('成功去除某角色的一个权利');

            });

          }else if(temp == 1){
            role_right = findTheRole_right(j);
            connection.query(insertSQL,[role_right,i+1 ],function(err, rows, fields){
              if(err) throw(err);
                console.log('成功增加某角色的一个权利');

            });
          }

        }
      }
      console.log(temp);

      res.redirect('root');

    /*
      更新用户角色，根据提交上来的两个隐藏信息。
    */
    }else if(req.body.postBtn =="userChanger"){
        var userId = req.body.userId;
        var changeTo = req.body.changeTo;
        var role_Id = findRoleIdAccordingToRoleName(changeTo);
        var connection = db.getConnection();
        var updateSQL = "update user set role_id = ? where user_id = ?"

        connection.query(updateSQL,[role_Id,userId],function(err, rows, fields){
          if(err) throw(err);
          console.log("成功更新数据库了其实")
          console.log(rows);
    res.redirect('/root');
        });

    }

  });

  app.get('/userinfo',function(req, res){
    if(!req.session.user){
      res.redirect('/register');
    }
    res.render('userinfo', {
      user: req.session.user
    })
  })

 /*
  * 登陆与注册在此
  */
  app.post('/register',function(req, res){
    var operation = req.body.submit;
    var email = req.body.email;
    var password = req.body.password;

    //使用这一句getConnection就可以连接数据库了。
    var connection = db.getConnection();

    if(operation == "signIn") {   //登陆

      User.get(email, function(err, user){
        if(err) throw(err);
        if(!user){

          return res.render('register',{info : '用户不存在,请先注册'});
        }
        if(password != user.password){
          return res.render('register',{info : 'Wrong password,please check!'});
        }else{
          req.session.user = user;

          if(req.session.user.email == 'root@root'){
            //只有root用户才可以进入的界面
            return res.redirect('/root')
          }

          res.redirect('/main');
        }
      });
    }

    else {                    //注册
      //加密
      // var md5 = crypto.createHash('md5');
      // password = md5.update(req.body.password).digest('hex');
      var newUser = new User({
        password: password,
        email: email
      });

      newUser.save(function(err,user){
        if(err){
          console.log('in index.js :该用户已存在,注册失败');
          res.render('register',{info : '注册失败，已存在'});
        }else{
          User.get(newUser.email, function(err, user){
            if(err) throw(err);
            if(user){
              req.session.user = user;
              res.redirect('/userinfo');
            }else{
              console.log('in 注册: 找不到 大兄弟');
            }
          })
        }
      });
    }  //end of 注册

  }); //end of 注册页面的post

  app.post('/userinfo',function(req, res){
    /* 该函数需要做的事情：
    1.获取post的数据。
    2.根据获取的数据新建一个user对象。
    3.调用user对象的update方法更新数据库。
    4.重定位至main页面。
     */
    var userName = req.body.username;
    var userClass = req.body.classNum;
    var sex = req.body.userSex;
    var email = req.session.user.email;
    // console.log(userName+','+userClass+','+sex+','+email);
    User.update(email, userName, sex, userClass, function(err, user){
      console.log('好的，更新完毕，进行回调，回调的用户角色是：'+user.role_id);
      req.session.user = user;
      res.redirect('main');
    });
  })
};
function findRoleIdAccordingToRoleName(changeTo){
  if(changeTo=="班长"){
    return 1;
  }else if(changeTo=="团支书"){
    return 2;
  }else if(changeTo=="学习委员"){
    return 3;
  }else if (changeTo=="课代表"){
    return 4;
  }else {
    return 5;
  }
}
function restoreTheRightsArrayAccordingToHiddenData(newRights, hiddenData){
  for(var i = 0; i < 4; i++){
    newRights[i] = new Array(0,0,0,0,0);
  }
  for(var i=0,line=-1,row=0; i<hiddenData.length; i+=2,row++){
    if(i%10==0){
      line ++;
      row = 0;
    }
    if(hiddenData[i] == 1){
      newRights[line][row] = 1;
    }
  }
}
function findAllRightsOfRole(rows){
  for(var i = 0; i < 4 ;i++){
    rights[i] = new Array(0,0,0,0,0);
  }
  for(var i = 0; i < rows.length ; i++){
    if(rows[i].role_right=='开班会'){
      rights[rows[i].role_id-1][0] = 1;
    }else if(rows[i].role_right == '收班费'){
      rights[rows[i].role_id-1][1] = 1;
    }else if(rows[i].role_right == '收党费'){
      rights[rows[i].role_id-1][2] = 1;
    }else if(rows[i].role_right == '评优等生'){
      rights[rows[i].role_id-1][3] = 1;
    }else if(rows[i].role_right == '考勤结果'){
      rights[rows[i].role_id-1][4] = 1;
    }
  }
  return rights;
}
function findTheRole_right(j){
  var role_right;
  if(j==0){
    role_right = '开班会'
  }else if(j==1){
    role_right = '收班费'
  }else if(j==2){
    role_right = '收党费'
  }else if(j==3){
    role_right = '评优等生'
  }else if(j==4){
    role_right = '考勤结果'
  }
  return role_right;
}
