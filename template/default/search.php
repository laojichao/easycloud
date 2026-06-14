<?php
$search = $_GET['search'];
      if(isset($search)) {
      $rs = $DB->query("select * from yixi_program where name LIKE'%".$search."%' and visible='1'");
		 if(mysqli_num_rows($rs)==0){
		     echo <<<EOT
    <div class="col-sm-12">

            <a target="" class="block block-link-hover2 ribbon ribbon-modern ribbon-success" href="#">

                <div class="block-content">

                    <div class="h4 push-5">API不存在</div>

                    <p class="text-muted">没有找到相关的API，请换个关键词试试，若有想添加的API请联系站长进行咨询！！</p>

                </div>

            </a>

        </div>
EOT;
    exit();
    }
		 }else{
		$rs=$DB->query("SELECT * FROM yixi_program WHERE visible='1'");
		 }
           
        foreach($rs as $res):?>
        <div class="col-sm-4">
        <a target="_blank" class="block block-link-hover2 ribbon ribbon-modern ribbon-<?php if($res['active']=="1"){echo'success';}else{echo'danger';}?>"
                href="doc.php?act=<?php echo $res['authfile'];?>">
                <div class="ribbon-box font-w600">调用：<?php echo $res['catalog'];?></div>
                <div class="block-content">
                    <div class="h4 push-5"><?php echo $res['name'];?></div>
                    <p class="text-muted"><?php echo $res['desc'];?></p>
                </div>
            </a>
        </div>
        <?php endforeach;
           ?>