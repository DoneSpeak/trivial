//用于config-lite
//作为默认设置
module.exports ={
	port: 3000,
	session:{
		secret:'msgboard',
		key:'msgboard',
		maxAge:2592000000
	},
	mongodb:'mongodb://localhost:27017/msgboard'
};