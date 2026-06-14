<?php
include_once '../includes/common.php';
if($islogin==1){}else sysmsg("您还未登录，请先登录",2,'./login.php',true);
$title='卡密列表';
include_once './header.php';
?>
<div class="row">
    <div class="col-lg-12">
        <div class="card">
            <div class="card-header">
                卡密列表&nbsp;&nbsp;&nbsp;<a href="javascript:searchClear()" class="badge badge-danger-info" title="刷新应用列表"><i class="layui-icon layui-icon-refresh"></i> 刷新</a>
				<span class="badge badge-primary-lighten" onclick="app_kmqk();">清空所有</span>
                <span class="badge badge-warning-lighten" onclick="app_kmqk1();">清空已使用</span>
                <span class="badge badge-success-lighten" onclick="app_kmqk2();">清空未使用</span>
				<span class="badge badge-info-lighten" onclick="app_kmqk3();">清空已过期</span>
            </div>
            <div class="card-body" style="z-index: 1">
                <!-- <div class="layui-elem-quote" id="blocktitle"></div> -->
				 <form onsubmit="return searchOrder()" method="GET" class="form layui-form">
                          <div class="form-row">
							<div class="form-group col-md-5">
								<select name="appid" class="form-control" lay-search>
								<option value="0">全部</option>
                                   <?php
                                   $rs=$DB->query("SELECT * FROM yixi_apps WHERE 1 order by id desc");
                                   while($res = $DB->fetch($rs))
                                  {
                                  echo '<option value="'.$res['id'].'">'.$res['name'].'</option>';
                                  }
                                   ?>
                                   </select>
							</div>
							<div class="form-group col-md-7">
								<select class="form-control" name="use" lay-search>
									<option value="0">全部</option><option value="1">未使用</option><option value="2">已使用</option><option value="3">已过期</option>
								</select>
							</div>
						</div>
					<div class="form-group mb-3">
                        <select class="form-control" name="type" lay-search><option value="0">全部</option><option value="1">卡密ID</option><option value="2">卡密</option><option value="3">备注</option></select>
                    </div>
                    <div class="form-group mb-3" id="searchword">
                        <input type="text" class="form-control" name="kw" placeholder="搜索内容" value="">
                    </div>
                    <div class="form-group mb-3">
                        <select class="form-control" name="method" lay-search><option value="0">精确搜索</option><option value="1">模糊搜索</option></select>
                    </div>
                    <div class="form-group mb-3">
                        <button class="btn btn-outline-primary" type="submit"><i class="layui-icon layui-icon-search"></i> 搜索</button>&nbsp;
                        <a href="addappkm.php<?php if (isset($_GET['appid']))echo'?appid='.intval($_GET['appid']);?>" class="btn btn-outline-secondary"><i class="layui-icon layui-icon-add-circle"></i> 添加卡密</a>
						<span class="btn btn-group p-0">
                            <select class="form-control" style="width: auto;" name="aid" lay-search lay-filter="aid">
                                <option selected>批量操作</option>
								<option value="1">&gt;改为封禁</option>
								<option value="2">&gt;改为激活</option>
								<option value="3">&gt;删除选中</option>
								<option value="4">&gt;导出选中</option>
								<option value="5">&gt;导出全部</option>
								<option value="6">&gt;加时选中</option>
								<option value="7">&gt;加时全部</option>
								<option value="8">&gt;扣时选中</option>
								<option value="9">&gt;扣时全部</option>
								<option value="10">&gt;解绑选中</option>
								<option value="11">&gt;解绑全部</option>
                            </select>
                        </span>
<div class="input-group-prepend" id="frame_set1" style="display:none">
	<div class="input-group">
	 <input type="number" id="amount" name="amount" class="form-control" value='1' placeholder="需要增加卡密时长">
		<select name="km_time" class="form-control" lay-search lay-filter="km_time"><option value="hour">小时</option><option value="day">天</option><option value="week">周</option><option value="month">月</option><option value="season">季</option><option value="year">年</option></select>
</div>
</div>
					<span class="btn btn-primary" onclick="change()"> 确定 </span>
                    </div>
				</form>
            <!-- <div id="listTable"></div> -->
			<table class="layui-hide" id="test-table-totalRow" lay-filter="test-table-totalRow"></table>
           </div>
		</div>
    </div>
</div>
<?php
include_once './bottom.php';
?>
<script src="https://cdn.bootcss.com/clipboard.js/2.0.4/clipboard.js"></script>
<script type="text/javascript">
layui.use(['form'], function () {
    var form = layui.form;
    form.on('select(aid)', function(data){ 
        if(data.value == '6'){
			      $("#frame_set1").css("display","inherit");
        }else if(data.value == '7'){
			      $("#frame_set1").css("display","inherit");
		}else if(data.value == '8'){
			      $("#frame_set1").css("display","inherit");
		}else if(data.value == '9'){
			      $("#frame_set1").css("display","inherit");
        }else{
                  $("#frame_set1").css("display","none");
		}
    });
});
layui.use(['form', 'table'], function () {
        var table = layui.table;
        table.render({
            elem: '#test-table-totalRow'
            , url: 'ajax.php?act=appkmlist_table<?php if (isset($_SERVER["QUERY_STRING"]))echo $_SERVER["QUERY_STRING"];?>'
            , toolbar: '#test-table-totalRow-toolbarDemo'
            , title: '卡密列表'
            , cellMinWidth: 120
            , id: 'reportReload'
            , cols: [[
                {type: 'checkbox', fixed: 'left'}
                , {field: 'id', title: 'ID', fixed: 'left', unresize: true, sort: true, width: 80}
			    , {field: 'name', templet: '#name', title: '归属应用'}
                , {field: 'type', templet: '#type', title: '卡密类型'}
                , {field: 'kami', templet: '#kami', title: '卡密', totalRow: true,width: 160}
                , {field: 'note', templet: '#note', title: '备注'}
                , {field: 'xq', title: '详情', templet: '#xq', totalRow: true,width: 240}
                , {field: 'state', templet: '#state', title: '状态', sort: true, totalRow: true}
                , {field: 'user', templet: '#user', title: '设备码', sort: true, totalRow: true}
                , {field: 'user_ip', templet: '#ip', title: '登录IP', sort: true, totalRow: true}
                , {field: 'km_change', title: '解绑次数', templet: '#kmchange', sort: true, totalRow: true}
                , {field: 'addtime', title: '添加时间', sort: true, totalRow: true}
                , {field: 'use_time', templet: '#usetime', title: '使用时间', sort: true, totalRow: true}
                , {field: 'end_time', templet: '#endtime', title: '到期时间', sort: true, totalRow: true}
				 , {
                    field: 'operation',
                    templet: '#operation',
                    title: '操作',
                    unresize: true,
                    sort: true,
                    width: 130
                }
            ]]
            , page: true
        });
  })
	function status(id) {
    layer.open({
            title: '信息',
            content: '你确定要执行此操作吗？',
            icon: 3,
            anim: 3,
            btn: ['确定', '取消'],
            yes: function (layero, index) {
                layer.msg('正在加载中...', {
                    time: 999999,
                    icon: 16
                });
                layer.msg('正在加载中...', {
                    time: 999999,
                    icon: 16
                });
                $.ajax({
                    type: "post",
                    url: 'ajax.php?act=app_kmactive'+'&id='+id,
                    data: {id: id},
                    dataType: "json",
                    success: function (data) {
					location.reload();
                    },
                    error: function () {
                        layer.alert('失败！');
                    }
                });
            }
        })
      }
function searchOrder(){
	var zappid=$("select[name='appid']").val();
	var zuse=$("select[name='use']").val();
    var ztype=$("select[name='type']").val();
    var zkw=$("input[name='kw']").val();
    var method=$("select[name='method']").val();
    if(appid=='0'&&use=='0'&&kw=='0'){
        location.reload();
    }else{
       if(zappid=='0'){
		var appid='';
	  }else {
        var appid='&appid='+zappid;
	  }
	  if(zuse=='0'){
		var use='';
	  }else {
        var use='&use='+zuse;
	  }
	  if(ztype=='0'){
		var type='';
	  }else {
        var type='&type='+ztype;
	  }
	  if(zkw==''){
		var kw='';
	  }else {
        var kw='&kw='+zkw;
	  }
        location.href="appkmlist.php?"+appid+use+type+kw+'&method='+method;
    }
    return false;
}
function searchClear(){
    location.href="appkmlist.php";
}
function change(){
	var amount=$("input[name='amount']").val();
    var aid=$("select[name='aid']").val();
	var km_time=$("select[name='km_time']").val();
	var checkStatus = layui.table.checkStatus('reportReload');
	var checkbox =[];
	for (var i= 0; i< checkStatus. data.length; i++){checkbox. push(checkStatus. data[i].id);
	}
    var ii = layer.msg('正在操作中,请稍后...', {icon: 16, time: 10 * 1000});
    $.ajax({
        type : 'POST',
        url : 'ajax.php?act=appkm_change',
        data : {checkbox:checkbox,aid:aid,amount:amount,km_time:km_time},
        dataType : 'json',
        success : function(data) {
            layer.close(ii);
            if(data.code == 0){
                layer.msg(data.msg, {
                    icon: 6,
                    end: function (layero, index) {
                        location.reload();
                    }
                });
            }
			if(data.code == 1){
                layer.open({
                  type: 1,
                  title: '导出卡密',
                  skin: 'layui-layer-rim',
                  content: data.data
                });
			layer.msg(data.msg, {icon: 6});
			}
        },
        error:function(data){
            layer.msg('请求超时', {icon: 5});
        }
    });
    return false;
}
function app_kmjb(id) {
    var confirmobj = layer.confirm('你确定要解绑该卡密吗？', {
      btn: ['确定','取消']
    }, function(){
      $.ajax({
        type : 'POST',
        url : 'ajax.php?act=app_kmjb',
		data : {id:id},
        dataType : 'json',
        success : function(data) {
            if(data.code == 0){
                layer.msg(data.msg, {
                    icon: 6,
                    end: function (layero, index) {
                        window.location.reload();
                    }
                });
            } else {
                layer.msg(data.msg, {icon: 5});
            }
        },
        error:function(data){
            layer.msg('服务器错误', {icon: 5});
            return false;
        }
      });
    }, function(){
      layer.close(confirmobj);
    });
}
function app_kmqk() {
    var confirmobj = layer.confirm('你确定要清空所有卡密吗？', {
      btn: ['确定','取消']
    }, function(){
      $.ajax({
        type : 'GET',
        url : 'ajax.php?act=app_kmqk',
        dataType : 'json',
        success : function(data) {
            if(data.code == 0){
                layer.msg(data.msg, {
                    icon: 6,
                    end: function (layero, index) {
                        window.location.reload();
                    }
                });
            } else {
                layer.msg(data.msg, {icon: 5});
            }
        },
        error:function(data){
            layer.msg('服务器错误', {icon: 5});
            return false;
        }
      });
    }, function(){
      layer.close(confirmobj);
    });
}
function app_kmqk1() {
    var confirmobj = layer.confirm('你确定要清空所有已使用的卡密吗？', {
      btn: ['确定','取消']
    }, function(){
      $.ajax({
        type : 'GET',
        url : 'ajax.php?act=app_kmqk1',
        dataType : 'json',
        success : function(data) {
            if(data.code == 0){
                layer.msg(data.msg, {
                    icon: 6,
                    end: function (layero, index) {
                        window.location.reload();
                    }
                });
            } else {
                layer.msg(data.msg, {icon: 5});
            }
        },
        error:function(data){
            layer.msg('服务器错误', {icon: 5});
            return false;
        }
      });
    }, function(){
      layer.close(confirmobj);
    });
}
function app_kmqk2() {
    var confirmobj = layer.confirm('你确定要清空所有未使用的卡密吗？', {
      btn: ['确定','取消']
    }, function(){
      $.ajax({
        type : 'GET',
        url : 'ajax.php?act=app_kmqk2',
        dataType : 'json',
        success : function(data) {
            if(data.code == 0){
                layer.msg(data.msg, {
                    icon: 6,
                    end: function (layero, index) {
                        window.location.reload();
                    }
                });
            } else {
                layer.msg(data.msg, {icon: 5});
            }
        },
        error:function(data){
            layer.msg('服务器错误', {icon: 5});
            return false;
        }
      });
    }, function(){
      layer.close(confirmobj);
    });
}
function app_kmqk3() {
    var confirmobj = layer.confirm('你确定要清空所有已过期的卡密吗？', {
      btn: ['确定','取消']
    }, function(){
      $.ajax({
        type : 'GET',
        url : 'ajax.php?act=app_kmqk3',
        dataType : 'json',
        success : function(data) {
            if(data.code == 0){
                layer.msg(data.msg, {
                    icon: 6,
                    end: function (layero, index) {
                        window.location.reload();
                    }
                });
            } else {
                layer.msg(data.msg, {icon: 5});
            }
        },
        error:function(data){
            layer.msg('服务器错误', {icon: 5});
            return false;
        }
      });
    }, function(){
      layer.close(confirmobj);
    });
}
function kmdel(id) {
    var confirmobj = layer.confirm('你确定要删除这张卡密吗？', {
      btn: ['确定','取消']
    }, function(){
      $.ajax({
        type : 'GET',
        url : 'ajax.php?act=appkm_del&id='+id,
        dataType : 'json',
        success : function(data) {
            if(data.code == 0){
                layer.msg(data.msg, {
                    icon: 6,
                    end: function (layero, index) {
                        window.location.reload();
                    }
                });
            } else {
                layer.msg(data.msg, {icon: 5});
            }
        },
        error:function(data){
            layer.msg('服务器错误', {icon: 5});
            return false;
        }
      });
    }, function(){
      layer.close(confirmobj);
    });
}
function getkmnote(id) {
    var ii = layer.msg('正在获取中,请稍后...', {icon: 16, time: 10 * 1000});
    $.ajax({
        type : 'GET',
        url : 'ajax.php?act=getkmNote&id='+id,
        dataType : 'json',
        success : function(data) {
            layer.close(ii);
            if(data.code == 0){
                layer.open({
                  type: 1,
                  title: '备注',
                  skin: 'layui-layer-rim',
                  content: data.data
                });
            }else{
                layer.msg(data.msg, {icon: 5});
            }
        },
        error:function(data){
            layer.msg('服务器错误', {icon: 5});
            return false;
        }
    });
}
function savekmnote(id) {
    var kmnote=$("#kmnote").val();
    $('#save').val('Loading');
    var ii = layer.msg('正在修改中,请稍后...', {icon: 16, time: 10 * 1000});
    $.ajax({
        type : "POST",
        url : "ajax.php?act=editkmNote",
        data : {id:id,kmnote:kmnote},
        dataType : 'json',
        success : function(data) {
            layer.close(ii);
            if(data.code == 0){
                layer.msg('保存成功！', {
                    icon: 6,
                    end: function (layero, index) {
                        listTable();
                    }
                });
            } else {
                layer.msg(data.msg, {icon: 5});
            }
            $('#save').val('保存');
        } 
    });
}
function timestampToTime(timestamp) {
	    var date = new Date(timestamp * 1000);
	    var Y = date.getFullYear() + '-';
	    var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
	    var D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) + ' ';
	    var h = (date.getHours() < 10 ? '0' + (date.getHours()) : date.getHours()) + ':';
	    var m = (date.getMinutes() < 10 ? '0' + (date.getMinutes()) : date.getMinutes()) + ':';
	    var s = (date.getSeconds() < 10 ? '0' + (date.getSeconds()) : date.getSeconds());
	    return Y + M + D + h + m + s;
}
</script>
<script type="text/html" id="name">
<i class="layui-icon layui-icon-component"></i>{{d.name}}
</script>
<script type="text/html" id="user">
{{# if(d.user==null ){ }}
卡密未使用
{{# }else if(d.user=='' ){ }}
解绑未使用
{{# }else{ }}
{{d.user}} <buttom class="layui-btn layui-btn-xs btn-primary" onclick="app_kmjb({{d.id}})">解绑</buttom>
{{# } }}
</script>
<script type="text/html" id="ip">
{{# if(d.user_ip==null ){ }}
卡密未使用
{{# }else if(d.user_ip=='' ){ }}
解绑未使用
{{# }else{ }}
{{d.user_ip}}
{{# } }}
</script>
<script type="text/html" id="endtime">
{{# if(d.type=='code' ){ }}
{{# if(d.state=='n' ){ }}
    卡密已封禁
{{# }else if(d.end_time==null ){ }}
    卡密未使用
{{# }else{ }}
    {{timestampToTime(d.end_time)}}
{{# } }}
   {{# }else{ }}
    次数卡计次
    {{# } }}
</script>
<script type="text/html" id="usetime">
{{# if(d.type=='code' ){ }}
{{# if(d.state=='n' ){ }}
    卡密已封禁
{{# }else if(d.end_time==null ){ }}
    卡密未使用
{{# }else{ }}
    {{timestampToTime(d.use_time)}}
{{# } }}
   {{# }else{ }}
    次数卡计次
    {{# } }}
</script>
<script type="text/html" id="kmchange">
{{d.km_change}}/次
</script>
<script type="text/html" id="note">
{{# if(d.note==null ){ }}
<span onclick="getkmnote({{d.id}})" title="备注"><i class="layui-icon layui-icon-dialogue"></i>备注</span>
{{# }else if(d.note=='' ){ }}
<span onclick="getkmnote({{d.id}})" title="备注"><i class="layui-icon layui-icon-dialogue"></i>备注</span>
{{# }else{ }}
<span onclick="getkmnote({{d.id}})" title="备注">{{d.note}}</span>
{{# } }}
</script>
<script type="text/html" id="type">
    {{# if(d.type=='code'){ }}
    单码卡密
    {{# }else if(d.type=='vip'){ }}
    会员卡密
	{{# }else if(d.type=='fen'){ }}
    积分卡密
	{{# }else if(d.type=='single'){ }}
    次数卡密
    {{# }else{ }}
    未知类型
    {{# } }}
</script>
<script type="text/html" id="xq">
{{# if(d.type=='code' ){ }}
    {{# if(d.km_time=='hour' ){ }}
    可用于<font color="red">{{d.name}}</font>[{{d.amount}}小时]单码卡密登录
    {{# }else if(d.km_time=='day'){ }}
    可用于<font color="red">{{d.name}}</font>[{{d.amount}}天]单码卡密登录
	{{# }else if(d.km_time=='week'){ }}
    可用于<font color="red">{{d.name}}</font>[{{d.amount}}周]单码卡密登录
	{{# }else if(d.km_time=='month'){ }}
    可用于<font color="red">{{d.name}}</font>[{{d.amount}}月]单码卡密登录
    {{# }else if(d.km_time=='season'){ }}
    可用于<font color="red">{{d.name}}</font>[{{d.amount}}季]单码卡密登录
    {{# }else if(d.km_time=='year'){ }}
    可用于<font color="red">{{d.name}}</font>[{{d.amount}}年]单码卡密登录
    {{# }else if(d.km_time=='longuse'){ }}
    可用于<font color="red">{{d.name}}</font>[永久卡]单码卡密登录
    {{# }else{ }}
    <font color="red">未知卡密类型</font>
    {{# } }}
    {{# }else if(d.type=='vip'){ }}
    可用于<font color="red">{{d.name}}</font>兑换[{{d.amount}}天]会员
	{{# }else if(d.type=='fen'){ }}
    可用于<font color="red">{{d.name}}</font>兑换[{{d.amount}}]积分
	{{# }else if(d.type=='single'){ }}
    可用于<font color="red">{{d.name}}</font>[{{d.amount}}]次单码卡密登录
    {{# }else{ }}
    <font color="red">未知卡密类型</font>
    {{# } }}
</script>
<script type="text/html" id="state">
{{# if(d.type=='code'&&d.end_time!=null&&d.end_time<'<?php echo time();?>'){ }}
    <font color="red">已过期</font>
{{# }else if(d.type=='single'&&d.amount<=0){ }}
    <font color="red">已过期</font>
{{# }else{ }}
    {{# if(d.state=='n' ){ }}
    <font color="red">已封禁</font>
{{# }else if(d.km_use=='n' ){ }}
    <font color="green">未使用</font>
{{# }else{ }}
    <font color="blue">已使用</font>
{{# } }}
{{# } }}
</script>
<script type="text/html" id="kami">
{{d.kami}}<span class="layui-btn layui-btn-xs btn-success" data-clipboard-text="{{d.kami}}" data-clipboard-action="copy" data-clipboard-target="#btn_code" id="btn_code">复制</span>
</script>
<script type="text/html" id="operation">
    {{# if(d.state=='y' ){ }}
    <buttom class="layui-btn layui-btn-xs btn-success" onclick="status({{d.id}})">激活</buttom>
    {{# }else{ }}
    <buttom class="layui-btn layui-btn-xs" style="background-color: rgba(127 123 123)" onclick="status({{d.id}})">
        封禁
    </buttom>
	{{# } }}
    <buttom class="layui-btn layui-btn-xs" style="background-color: rgba(255,0,63,0.6)" onclick="kmdel({{d.id}})">
        删除
    </buttom>
</script>
<script>
    var codeClipboard = new ClipboardJS('#btn_code');
   codeClipboard.on('success', function (e) {
        layer.msg("卡密复制成功", {icon: 6});
        e.clearSelection();
    });
    codeClipboard.on('error', function (e) {
        layer.msg('复制失败,请手动复制~', {icon: 5});
    });
</script>