<!-- 角色表 -->
CREATE TABLE ROLES(
	role_id int(2) not null,
	role_name ENUM('班长','团支书','学习委员','课代表','班级成员') not null,
	primary key (role_id)
)
<!-- 用户表 -->
CREATE TABLE User(
	user_id INT(6)unsigned AUTO_INCREMENT not null ,
	name varchar(20),
	email varchar(30),
	sex ENUM('male','female'),
	class_id int(2),
	password int(20),
	role_id int (2) not null REFERENCES ROLES(role_id),

	PRIMARY KEY (USER_ID)
)
<!-- 角色-权利表 -->
CREATE TABLE RIGHTS_OF_ROLE(
	role_right ENUM('开班会','收班费','收党费','评优等生','考勤结果','回复') not null,
	role_id int (2) not null REFERENCES ROLES(role_id)

)

<!-- 开班会 -->
CREATE TABLE metting(
	metting_id int(10) AUTO_INCREMENT ,
	class_id int(2),
	time date,
	place varchar(20),
	numbers_required int(3),
	numbers_gotten int(3),
	topic varchar(30),
	user_id int (2) REFERENCES User(user_id),

	primary key(metting_id)
);

CREATE TABLE METTING_AGREE(
	metting_id int(10) REFERENCES metting(metting_id),
	user_id int (2) REFERENCES User(user_id)
);

CREATE TABLE METTING_DISAGREE(
	metting_id int(10) REFERENCES metting(metting_id),
	user_id int (2) REFERENCES User(user_id)
);

<!-- 收班费 -->
CREATE TABLE CLASS_MONEY(
	CM_ID int(10) AUTO_INCREMENT,
	class_id int(2),
	deadline date,
	howmuch NUMERIC(10,7),

	primary key(CM_ID),
	user_id int (2) REFERENCES User(user_id)
);


CREATE TABLE payed_members(
	CM_ID int(10) REFERENCES CLASS_MONEY(CM_ID),
	user_id int (2) REFERENCES User(user_id)
);

CREATE TABLE unpayed_members(
	CM_ID int(10) REFERENCES CLASS_MONEY(CM_ID),
	user_id int (2) REFERENCES User(user_id)
);

<!-- 评优等生 -->
CREATE TABLE GOOD_STUDENT(
	GS_ID int(10)  AUTO_INCREMENT,
	title varchar(20) ,

	primary key(GS_ID),
	student int (2) REFERENCES User(user_id),
	poster int (2) REFERENCES User(user_id)
)

CREATE TABLE CHECK_IN(
	CHECKIN_ID int(10) auto_increment,
	class_id int (2),
	truant int (2) REFERENCES User(user_id),
	course_name varchar(20),
	missing_day date,

	PRIMARY key(CHECKIN_ID)
);

// 插入【角色表】数据
insert into roles(role_id, role_name) values(1,'班长');
insert into roles(role_id, role_name) values(2,'团支书');
insert into roles(role_id, role_name) values(3,'学习委员');
insert into roles(role_id, role_name) values(4,'课代表');
insert into roles(role_id, role_name) values(5,'班级成员');

// 插入【角色-权限】表数据
insert into RIGHTS_OF_ROLE(role_id, role_right) values(1,'开班会');
insert into RIGHTS_OF_ROLE(role_id, role_right) values(1,'收班费');
insert into RIGHTS_OF_ROLE(role_id, role_right) values(1,'收党费');
insert into RIGHTS_OF_ROLE(role_id, role_right) values(1,'评优等生');
insert into RIGHTS_OF_ROLE(role_id, role_right) values(1,'考勤结果');


insert into RIGHTS_OF_ROLE(role_id, role_right) values(2,'收党费');
insert into RIGHTS_OF_ROLE(role_id, role_right) values(2,'评优等生');
insert into RIGHTS_OF_ROLE(role_id, role_right) values(2,'考勤结果');

insert into RIGHTS_OF_ROLE(role_id, role_right) values(3,'评优等生');
insert into RIGHTS_OF_ROLE(role_id, role_right) values(3,'考勤结果');

insert into RIGHTS_OF_ROLE(role_id, role_right) values(4,'考勤结果');


//更新数据的sql
update user
set name = 'carrie1',
sex = 'Female',
class_id = 3
where email = '11@333111';

//删除表中所有数据，并插入一条
delete from user;
insert into user values(2014150000,'Caren','caren_szu@foxmail.com','male', 2, 741851, 1);
insert into user values(2014150101,'Carrie','whatever@xxx.com', 'female', 1, 1234, 3);


//根据用户的user_id获取用户的role_id，再获取用户的role_name;
select role_name,role_right from user U, roles R1, rights_of_role R2
where U.role_id = R1.role_id
and R2.role_id = R1.role_id
and R2.role_id = U.role_id
and U.role_id = 1
and user_id = ?;

//
select * from rights_of_role;

//删除与增加权限的SQL
	var deleteSQL = "delete from rights_of_role where role_right= ? and role_id = ? ;";
	var insertSQL = "insert into rights_of_role(role_right, role_id) values( ? , ? ); "

//检索用户信息
select user_id, name, email, role_name
from user U, roles R1
where U.role_id = R1.role_id

// 更新用户权限
update user set role_id = 6 where user_id = 2014150126
