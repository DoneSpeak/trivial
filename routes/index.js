//路由分配入口
// 路由从上往下匹配路径进行查找
// 找到后进入相应的普通中间件
module.exports = function(app){
	//注册路由中间件，所以在index中可以直接使用routes(app)
	//也就是因为这个被注册为一个路由中间件的原因
	app.get('/',function(req, res){
		res.redirect('/msgs');
	});
	//app.use 注册普通中间件
	//注册
	app.use('/signin',require('./signin'));
	// //登录
	app.use('/login', require('./login'));
 	//登出
 	app.use('/logout', require('./logout'));
  	//首页
  	app.use('/msgs', require('./msgs'));
  	//首页
  	app.use('/self', require('./self'));

  	// 404 页
  	// 没有找到的路径都会来到这里
  	app.use(function (req, res) {
    if (!res.headersSent) {
      res.render('404');
    }
  });

}