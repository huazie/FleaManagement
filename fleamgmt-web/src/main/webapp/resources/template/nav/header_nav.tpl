<!--
    头部导航栏【进度条】模板
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
    头部导航栏【通知】模板
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
    头部导航栏【消息】模板
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

<!--
    导航栏用户模板
-->
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
