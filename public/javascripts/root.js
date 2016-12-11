
function dosomething(){


  /*为提交按钮监听事件*/
  var submitBtn = document.getElementById('confirmButton');
  submitBtn.onclick = updateRightsOfRoles;
  function updateRightsOfRoles(){
    var checkbox = document.getElementsByClassName('table-checkbox');
    var rights = new Array();
    for(var i = 0; i < 4; i ++){
      rights[i] = new Array(0,0,0,0,0);
    }

    for(var i=0,line=-1,row=0; i<checkbox.length; i++,row++){
      if(i%5==0){
        line ++;
        row = 0;
      }
      if(checkbox[i].checked){
        rights[line][row] = 1;
      }
    }

    var data = document.getElementById('data');
    data.value = rights;
    // return false;
  }


  /* 为修改角色的按钮添加监听事件*/
  var selectedUserId = undefined;  //用于数据库检索
  $('#myModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget);
    var roleName = button.data('role');
    var userName = button.data('name');
    selectedUserId = button.data('id');
    var modal = $(this)

    modal.find('.modal-title').text('用户角色修改' );
    modal.find('.modal-body p').text("将用户 【"+selectedUserId+"】 的角色从 【"+roleName+"】 修改至");
  })

  /* 为弹出窗口的角色选择按钮监听事件*/
  var buttons = document.getElementsByClassName("dropdown-item btn btn-primary btn-block");
  var btn = document.getElementsByClassName('btn btn-primary dropdown-toggle');
  for(var i = 0; i < buttons.length; i++){
    buttons[i].onclick = function(){
      btn[0].innerHTML = this.innerHTML;
    }
  }

  /* 为修改用户角色的按钮添加监听事件 */
  var secondSubmitBtn = document.getElementById('modal-submitBtn');
  secondSubmitBtn.onclick = function(){
    var data1 = document.getElementById('data1');
    var data2 = document.getElementById('data2');
    var postBtn2 = document.getElementById('postBtn2');
    data1.value = selectedUserId;
    data2.value = btn[0].innerHTML; //即选择按钮的文字
    
  }

}
dosomething();
