<!--
    快捷侧边栏模板
-->
<script id="tpl_sidebar_shortcuts" type="text/x-handlebars-template">
	<div class="sidebar-shortcuts-large" id="sidebar-shortcuts-large">
	{{#common_list this}}
		<button class="btn {{BTN_CLASS}}">
			<i class="fa fa-{{BTN_ICON}}"></i>
		</button>
	{{/common_list}}
	</div>

	<div class="sidebar-shortcuts-mini" id="sidebar-shortcuts-mini">
	{{#common_list this}}
		<span class="btn {{BTN_CLASS}}"></span>
	{{/common_list}}
	</div>
</script>
