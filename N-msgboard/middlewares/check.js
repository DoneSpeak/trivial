module.exports = {
  checkLogin: function checkLogin(req, res, next) {
    if (!req.session.user) {
      	//通过 flash 设置错误信息 res.locals.error
      	req.flash('error', '未登录');
      	//重定向到登陆界面
      	return res.redirect('/login');
    }
    next();
  },

  checkNotLogin: function checkNotLogin(req, res, next) {
    if (req.session.user) {
      	req.flash('error', '已登录');
     	//返回之前的页面
      	return res.redirect('back');
    }
    next();
  }
};


