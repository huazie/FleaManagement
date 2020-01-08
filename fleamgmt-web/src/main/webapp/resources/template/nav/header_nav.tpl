<!-- 
	{
		"NAV_BG_COLOR" 				: "grey",
		"NAV_ICON"     				: "fa-tasks",
		"NAV_BADGE_COLOR"			: "badge-grey",
		"NAV_BADGE_NUM"    			: "4",
		"NAV_DROPDOWN_MENU_COLOR"	: "",
		"NAV_DROPDOWN_HEADER_ICON"  : "fa-check",
		"NAV_DROPDOWN_HEADER_TITLE"	: "未任务完成",
  		"NAV_CONTENT"	:	[{
  			"PROGRESS_BAR_NAME"			: "软件更新",
  			"PROGRESS_BAR_PERCENTTAGE" 	: "65%",
  			"PROGRESS_BAR_STYLE"		: "",
  			"PROGRESS_BAR_STATE"		: ""	
  		},{
  			"PROGRESS_BAR_NAME"			: "硬件更新",
  			"PROGRESS_BAR_PERCENTTAGE" 	: "35%",
  			"PROGRESS_BAR_STYLE"		: "",
  			"PROGRESS_BAR_STATE"		: "progress-bar-danger"	
  		},{
  			"PROGRESS_BAR_NAME"			: "单元测试",
  			"PROGRESS_BAR_PERCENTTAGE" 	: "15%",
  			"PROGRESS_BAR_STYLE"		: "",
  			"PROGRESS_BAR_STATE"		: "progress-bar-warning"	
  		},{
  			"PROGRESS_BAR_NAME"			: "错误修复",
  			"PROGRESS_BAR_PERCENTTAGE" 	: "90%",
  			"PROGRESS_BAR_STYLE"		: "progress-striped active",
  			"PROGRESS_BAR_STATE"		: "progress-bar-success"	
  		}],
  		"NAV_DROPDOWN_BOTTOM_TITLE"	: "查看任务详情"
	}
-->
<script id="tpl_header_nav_progress" type="text/x-handlebars-template">
	<li class="{{NAV_BG_COLOR}}">
		<a data-toggle="dropdown" class="dropdown-toggle" href="#">
			<i class="fa {{NAV_ICON}} fa-lg"></i>
			<span class="badge {{NAV_BADGE_COLOR}}">{{NAV_BADGE_NUM}}</span>
		</a>

		<ul class="pull-right dropdown-navbar dropdown-menu dropdown-caret dropdown-close {{NAV_DROPDOWN_MENU_COLOR}}">
			<li class="dropdown-header">
				<i class="fa {{NAV_DROPDOWN_HEADER_ICON}} fa-lg"></i>
				{{NAV_DROPDOWN_HEADER_TITLE}}
			</li>
			
			{{#common_list NAV_CONTENT}}
			<li>
				<a href="#">
					<div class="clearfix">
						<span class="pull-left">{{PROGRESS_BAR_NAME}}</span>
						<span class="pull-right">{{PROGRESS_BAR_PERCENTTAGE}}</span>
					</div>

					<div class="progress progress-mini {{PROGRESS_BAR_STYLE}}">
						<div style="width:{{PROGRESS_BAR_PERCENTTAGE}}" class="progress-bar {{PROGRESS_BAR_STATE}}"></div>
					</div>
				</a>
			</li>
			{{/common_list}}

			<li>
				<a href="#">
					{{NAV_DROPDOWN_BOTTOM_TITLE}}
					<i class="fa fa-arrow-right"></i>
				</a>
			</li>
		</ul>
	</li>
</script>

<!--
	{
		"NAV_BG_COLOR" 				: "purple",
		"NAV_ICON"     				: "fa-bell icon-animated-bell",
		"NAV_BADGE_COLOR"			: "badge-important",
		"NAV_BADGE_NUM"    			: "8",
		"NAV_DROPDOWN_MENU_COLOR"	: "navbar-pink",
		"NAV_DROPDOWN_HEADER_ICON"  : "fa-warning",
		"NAV_DROPDOWN_HEADER_TITLE"	: "通知",
  		"NAV_CONTENT"	:	[{
  			"NOTICE_ICON_COLOR"			: "btn-inverse",
  			"NOTICE_ICON" 				: "fa-comment",
  			"NOTICE_TITLE"				: "新闻评论",
  			"NOTICE_BADGE"				: "badge-info",
  			"NOTICE_NUM"				: "12"
  		},{
  			"NOTICE_ICON_COLOR"			: "btn-primary",
  			"NOTICE_ICON" 				: "fa-user",
  			"NOTICE_TITLE"				: "用户数",
  			"NOTICE_BADGE"				: "",
  			"NOTICE_NUM"				: "10"
  		},{
  			"NOTICE_ICON_COLOR"			: "btn-success",
  			"NOTICE_ICON" 				: "fa-shopping-cart",
  			"NOTICE_TITLE"				: "新订单",
  			"NOTICE_BADGE"				: "badge-success",
  			"NOTICE_NUM"				: "8"	
  		},{
  			"NOTICE_ICON_COLOR"			: "btn-info",
  			"NOTICE_ICON" 				: "fa-twitter",
  			"NOTICE_TITLE"				: "粉丝",
  			"NOTICE_BADGE"				: "badge-danger",
  			"NOTICE_NUM"				: "11"	
  		}],
  		"NAV_DROPDOWN_BOTTOM_TITLE"	: "查看所有通知"
	}
-->
<script id="tpl_header_nav_notice" type="text/x-handlebars-template">
	<li class="{{NAV_BG_COLOR}}">
		<a data-toggle="dropdown" class="dropdown-toggle" href="#">
			<i class="fa {{NAV_ICON}} fa-lg"></i>
			<span class="badge {{NAV_BADGE_COLOR}}">{{NAV_BADGE_NUM}}</span>
		</a>

		<ul class="pull-right dropdown-navbar dropdown-menu dropdown-caret dropdown-close {{NAV_DROPDOWN_MENU_COLOR}}">
			<li class="dropdown-header">
				<i class="fa {{NAV_DROPDOWN_HEADER_ICON}} fa-lg"></i>
				{{NAV_DROPDOWN_HEADER_TITLE}}
			</li>
			
			{{#common_list NAV_CONTENT}}
			<li>
				<a href="#">
					<div class="clearfix">
						<span class="pull-left">
							<i class="btn btn-xs no-hover {{NOTICE_ICON_COLOR}} fa {{NOTICE_ICON}}"></i>
							<span style="margin-left: 20px;">{{NOTICE_TITLE}}</span>
						</span>
						<span class="pull-right badge {{NOTICE_BADGE}}">+{{NOTICE_NUM}}</span>
					</div>
				</a>
			</li>
			{{/common_list}}

			<li>
				<a href="#">
					{{NAV_DROPDOWN_BOTTOM_TITLE}}
					<i class="fa fa-arrow-right" aria-hidden="true"></i>
				</a>
			</li>
		</ul>
	</li>
</script>

<!--
	{
		"NAV_BG_COLOR" 				: "green",
		"NAV_ICON"     				: "fa-envelope-o fa-fw icon-animated-vertical",
		"NAV_BADGE_COLOR"			: "badge-success",
		"NAV_BADGE_NUM"    			: "5",
		"NAV_DROPDOWN_MENU_COLOR"	: "",
		"NAV_DROPDOWN_HEADER_ICON"  : "fa-envelope-o fa-fw",
		"NAV_DROPDOWN_HEADER_TITLE"	: "消息",
  		"NAV_CONTENT"	:	[{
  			"MSG_IMG_SRC"			 	: "resources/images/avatars/avatar.png",
  			"MSG_IMG_ALT" 				: "Alex's Avatar",
  			"MSG_TITLE_NAME"			: "Alex",
  			"MSG_TITLE_TEXT"			: "不知道写啥 ...",
  			"MSG_TIME_TEXT"				: "1分钟以前"
  		},{
  			"MSG_IMG_SRC"			 	: "resources/images/avatars/avatar3.png",
  			"MSG_IMG_ALT" 				: "Susan's Avatar",
  			"MSG_TITLE_NAME"			: "Susan",
  			"MSG_TITLE_TEXT"			: "不知道翻译..",
  			"MSG_TIME_TEXT"				: "20分钟以前"
  		},{
  			"MSG_IMG_SRC"			 	: "resources/images/avatars/avatar4.png",
  			"MSG_IMG_ALT" 				: "Bob's Avatar",
  			"MSG_TITLE_NAME"			: "Bob",
  			"MSG_TITLE_TEXT"			: "到底是不是英文 ...",
  			"MSG_TIME_TEXT"				: "下午3:15"
  		}],
  		"NAV_DROPDOWN_BOTTOM_TITLE"	: "查看所有消息"
	}
-->
<script id="tpl_header_nav_msg" type="text/x-handlebars-template">
	<li class="{{NAV_BG_COLOR}}">
		<a data-toggle="dropdown" class="dropdown-toggle" href="#">
			<i class="fa {{NAV_ICON}} fa-lg"></i>
			<span class="badge {{NAV_BADGE_COLOR}}">{{NAV_BADGE_NUM}}</span>
		</a>
	
		<ul class="pull-right dropdown-navbar dropdown-menu dropdown-caret dropdown-close {{NAV_DROPDOWN_MENU_COLOR}}">
			<li class="dropdown-header">
				<i class="fa {{NAV_DROPDOWN_HEADER_ICON}} fa-lg"></i>
				{{NAV_DROPDOWN_HEADER_TITLE}}
			</li>
			
			{{#common_list NAV_CONTENT}}
			<li>
				<a href="{{../NAV_HREF}}">
					<img src="{{MSG_IMG_SRC}}" class="msg-photo" alt="{{MSG_IMG_ALT}}" />
					<span class="msg-body">
						<span class="msg-title">
							<span class="blue">{{MSG_TITLE_NAME}}:</span>
							{{MSG_TITLE_TEXT}}
						</span>
	
						<span class="msg-time">
							<i class="fa fa-clock-o fa-lg fa-fw"></i>
							<span>{{MSG_TIME_TEXT}}</span>
						</span>
					</span>
				</a>
			</li>
			{{/common_list}}
			
			<li>
				<a href="{{NAV_HREF}}">
					{{NAV_DROPDOWN_BOTTOM_TITLE}}
					<i class="fa fa-arrow-right"></i>
				</a>
			</li>
		</ul>
	</li>
</script>

<!-- 导航栏用户模板  -->
<script id="tpl_header_nav_user" type="text/x-handlebars-template">
	<li class="{{NAV_BG_COLOR}}">
		<a data-toggle="dropdown" href="#" class="dropdown-toggle">
			<img class="nav-user-photo" src="{{NAV_USER_PHOTO}}" alt="User's Photo" />
			<span class="user-info">
				<small>欢迎光临,</small>
				{{NAV_USER_NAME}}
			</span>
			<i class="fa fa-caret-down fa-lg fa-fw"></i>
		</a>

		<ul class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">
			{{#common_list NAV_CONTENT}}
			{{#if HAS_DIVIDER}}
			<li class="divider"></li>
			{{/if}}
			<li>
				<a href="#" id="user_function_{{USER_FUNCTION_EVENT}}" name="{{USER_FUNCTION_EVENT}}">
					<i class="fa fa-{{USER_FUNCTION_ICON}} fa-lg fa-fw"></i>
					<span style="margin-left:20px;">{{USER_FUNCTION_NAME}}</span>
				</a>
			</li>
			{{/common_list}}
		</ul>
	</li>
</script>
