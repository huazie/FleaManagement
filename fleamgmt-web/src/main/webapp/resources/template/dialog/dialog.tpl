<!-- 
	通用提示弹出框
-->
<script id="tpl_dialog_tips" type="text/x-handlebars-template">
	<div>
		{{#if_eq ICON compare="info"}}
		<div class="clearfix">
			<i class="blue fa fa-info-circle fa-fw"></i>
			<span class="blue">&nbsp;提示</span>
		</div>
		{{/if_eq}}
		
		{{#if_eq ICON compare="warning"}}
		<div class="clearfix">
			<i class="red fa fa-exclamation-circle fa-lg"></i>
			<span class="black">&nbsp;警告</span>
		</div>
		{{/if_eq}}
		
		<div class="space-8"></div>
		
		<div class="clearfix">
            {{#if CONTENT}}
			<span class="center">{{CONTENT}}</span>
			{{else}}
			{{#common_list CONTENTS}}
			<div class="center">{{MESSAGE}}</div>
			{{/common_list}}
			{{/if}}
		</div>
	</div>
</script>

<!-- 
	通用确认弹出框
-->
<script id="tpl_dialog_confirm" type="text/x-handlebars-template">
	<div>
		{{#if_eq ICON compare="info"}}
		<div class="clearfix">
			<i class="blue fa fa-info-circle fa-fw"></i>
			<span class="blue">&nbsp;提示</span>
		</div>
		{{/if_eq}}
		
		{{#if_eq ICON compare="warning"}}
		<div class="clearfix">
			<i class="red fa fa-exclamation-circle fa-lg"></i>
			<span class="black">&nbsp;警告</span>
		</div>
		{{/if_eq}}
		
		{{#if_eq ICON compare="question"}}
		<div class="clearfix">
			<i class="black fa fa-question-circle fa-lg"></i>
			<span class="red">&nbsp;确认</span>
		</div>
		{{/if_eq}}
		
		<div class="space-10"></div>
		
		<div class="clearfix">
		    {{#if CONTENT}}
			<span class="center">{{CONTENT}}</span>
			{{else}}
			{{#common_list CONTENTS}}
			<span class="center">{{MESSAGE}}</span>
			{{/common_list}}
			{{/if}}
		</div>
		
	</div>
</script>

<!--
    菜单栏弹出框模板
-->
<script id="tpl_dialog_menu" type="text/x-handlebars-template">
	{{#common_list this}}
	{{#if HAS_DIVIDER}}
	<div class="hr hr-4"></div>
	{{else}}
	<div class="space-2"></div>
	{{/if}}
	<div>
		<a href="#" id="function_{{FUNCTION_EVENT}}" name="{{FUNCTION_EVENT}}" style="cursor:pointer;">
			<i class="fa fa-{{FUNCTION_ICON}} fa-lg fa-fw"></i>
			<span>{{FUNCTION_NAME}}</span>
			<input name="id" type="hidden" value="{{MENU_ID}}" />
			<input name="code" type="hidden" value="{{MENU_CODE}}" />
            <input name="name" type="hidden" value="{{MENU_NAME}}" />
            <input name="level" type="hidden" value="{{MENU_LEVEL}}" />
            <input name="sort" type="hidden" value="{{MENU_SORT}}" />
		</a>
	</div>
	{{/common_list}}
</script>

