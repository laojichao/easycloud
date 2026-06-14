<?php
if(!defined('IN_CRONLITE'))exit();
class Page
{
	private $saveGet; //是否保留GET参数
	private $total; //总记录
	private $pageSize; //每页显示多少条
	private $limit; //limit
	private $pageBtn; //自定义页码按钮
	private $page; //当前页码
	private $pageNum; //总页码
	private $link; //Get参数
	private $url; //地址
	private $bothNum; //两边保持数字分页的量

	//构造方法初始化
	public function __construct($_total, $_pageSize, $_pageBtn=false , $_link="" ,$_saveGet = true)
	{
		$this->saveGet = $_saveGet;
		$this->total = $_total>0 ? $_total : 1;
		$this->pageSize = $_pageSize;
		$this->pageBtn = $_pageBtn;
		$this->link = $_link;
		$this->pageNum = ceil($this->total / $this->pageSize);
		$this->page = $this->setPage();
		$this->limit = " LIMIT " . ($this->page - 1) * $this->pageSize . ",".$this->pageSize;
		$this->url = $this->setUrl();
		$this->bothNum = 1;
	}
	//拦截器
	public function __get($_key)
	{
		return $this->$_key;
	}

	//获取当前页码
	private function setPage()
	{
		if (!empty($_GET['page'])) {
			if ($_GET['page'] > 0) {
				if ($_GET['page'] > $this->pageNum) {
					return $this->pageNum;
				} else {
					return $_GET['page'];
				}
			} else {
				return 1;
			}
		} else {
			return 1;
		}
	}

	//获取地址
	private function setUrl()
	{
		$_url = $_SERVER["REQUEST_URI"];
		$_par = parse_url($_url);
		if ($this->saveGet && isset($_par['query'])) {
			parse_str($_par['query'], $_query);
			unset($_query['page']);
			$_url = $_par['path'] . '?' . http_build_query($_query) . '&';
		} else {
			$_url = $_par['path'] . '?';
		}
		return $_url;
	}	//数字目录

	private function pageList()
	{
		$_pageList = null;
		for ($i = $this->bothNum; $i >= 1; $i--) {
			$_page = $this->page - $i;
			if ($_page < 1) continue;
			if ($this->pageBtn) {
				$_pageList .= '<li class="page-item"><a class="page-link" onclick="listTable(\'page='.$_page.$this->link.'\')">' . $_page . '</a></li>';
			}else{
				$_pageList .= '<li class="page-item"><a class="page-link" href="' . $this->url . 'page=' . $_page . '">' . $_page . '</a></li>';
			}
		}
		$_pageList .= '<li class="page-item active"><a class="page-link"><font color="white">' . $this->page . '</font></a></li> ';
		for ($i = 1; $i <= $this->bothNum; $i++) {
			$_page = $this->page + $i;
			if ($_page > $this->pageNum) break;
			if ($this->pageBtn) {
				$_pageList .= '<li class="page-item"><a class="page-link" onclick="listTable(\'page='.$_page.$this->link.'\')">' . $_page . '</a></li>';
			}else{
				$_pageList .= '<li class="page-item"><a class="page-link" href="' . $this->url . 'page=' . $_page . '">' . $_page . '</a></li>';
			}
		}
		return $_pageList;
	}

	//首页
	private function first()
	{
		if ($this->page == $this->bothNum + 2) {
			if ($this->pageBtn) {
				return '<li class="page-item"><a class="page-link" onclick="listTable(\'page=1'.$this->link.'\')">首页</a></li>';
			}
			return '<li class="page-item"><a class="page-link" href="' . $this->url . '">首页</a></li>';
		} elseif ($this->page > $this->bothNum + 2) {
			if ($this->pageBtn) {
				return '<li class="page-item"><a class="page-link" onclick="listTable(\'page=1'.$this->link.'\')">首页</a></li><li class="page-item"><span>...</span></li>';
			}
			return '<li class="page-item"><a class="page-link" href="' . $this->url . '">首页</a></li><li class="page-item"><span>...</span></li>';
		}
	}

	//上一页
	private function prev()
	{
		if ($this->page == 1) {
			return '<li class="page-item"><a class="page-link" href="javascript: void(0);" aria-label="Previous"><span aria-hidden="true">&laquo;</span><span class="sr-only">Previous</span></a></li>';
		}
		if ($this->pageBtn) {
			return '<li class="page-item"><a class="page-link" onclick="listTable(\'page='.($this->page - 1).$this->link.'\')">上一页</a></li>';
		}
		return '<li class="page-item"><a class="page-link" href="' . $this->url . 'page=' . ($this->page - 1) . '">上一页</a></li>';
	}

	//下一页
	private function next()
	{

		if ($this->page == $this->pageNum) {
			return '<li class="page-item"><a class="page-link" href="javascript: void(0);" aria-label="Next"><span aria-hidden="true">&raquo;</span><span class="sr-only">Next</span></a></li>';
		}
		if ($this->pageBtn) {
			return '<li class="page-item"><a class="page-link" onclick="listTable(\'page='.($this->page + 1).$this->link.'\')">下一页</a></li>';
		}
		return '<li class="page-item"><a class="page-link" href="' . $this->url . 'page=' . ($this->page + 1) . '">下一页</a></li>';
	}

	//尾页
	private function last()
	{
		if ($this->pageNum - $this->page == $this->bothNum + 1) {
			if ($this->pageBtn) {
				return '<li class="page-item"><a class="page-link" onclick="listTable(\'page='.$this->pageNum.$this->link.'\')">尾页</a></li>';
			}
			return '<li class="page-item"><a class="page-link" href="' . $this->url . 'page=' . $this->pageNum . '">尾页</a></li>';
		} elseif ($this->pageNum - $this->page > $this->bothNum + 1) {
			if ($this->pageBtn) {
				return '<li class="page-item"><span>...</span></li><li><a class="page-link"  onclick="listTable(\'page='.$this->pageNum.$this->link.'\')">尾页</a></li>';
			}
			return '<li class="page-item"><span>...</span></li><li><a href="' . $this->url . 'page=' . $this->pageNum . '">尾页</a></li>';
		}
	}

	//跳页
	private function jump()
	{
		if ($this->pageBtn) {
			return '<li><font style="display:inline-block;float:left;margin-left: 5px;line-height: 220%">共'.$this->pageNum.'页</font><input id="jumpPage" class="form-control" style="display:inline-block;float:left;max-width: 40px;padding: 5px 12px;margin-left: 10px;"/><font style="display:inline-block;float:left;margin-left: 5px;line-height: 220%">页</font>&nbsp;&nbsp;<a class="btn btn-primary mb-2" onclick="var page=$(\'#jumpPage\').val();var maxpage='.$this->pageNum.';if(Number(page)>maxpage)return layer.msg(\'最多\'+maxpage+\'页\', {icon: 5});if(Number(page)<1){return layer.msg(\'请输入正确的页码\', {icon: 5})};listTable(\'page=\'+page+\''.$this->link.'\')"><font color="white">跳转</font></a></li>';
		}else{
			return '<li><font style="display:inline-block;float:left;margin-left: 5px;line-height: 220%">共'.$this->pageNum.'页</font><input id="jumpPage" class="form-control" style="display:inline-block;float:left;max-width: 40px;padding: 5px 12px;margin-left: 10px;"/><font style="display:inline-block;float:left;margin-left: 5px;line-height: 220%">页</font>&nbsp;&nbsp;<a class="btn btn-primary mb-2" onclick="var page=$(\'#jumpPage\').val();var maxpage='.$this->pageNum.';if(Number(page)>maxpage)return layer.msg(\'最多\'+maxpage+\'页\', {icon: 5});if(Number(page)<1){return layer.msg(\'请输入正确的页码\', {icon: 5})};window.location.href=\'?page=\'+page+\''.$this->link.'\'"><font color="white">跳转</font></a></li>';
		}
	}

	//分页信息
	public function showPage()
	{
		$_page = '<ul class="pagination">';
		$_page .= $this->prev();
		$_page .= $this->first();
		$_page .= $this->pageList();
		$_page .= $this->last();
		$_page .= $this->next();
		$_page .= $this->jump();
		$_page .= '</ul>';
		return $_page;
	}
}
?>