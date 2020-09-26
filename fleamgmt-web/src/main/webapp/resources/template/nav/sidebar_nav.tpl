<!-- 菜单模板 -->
<script id="tpl_sidebar_nav" type="text/x-handlebars-template">

	{{#common_list this}}
	{{#unless HAS_SUB_MENU}}
	{{#if IS_SELECT}}
	<li class="active">
	{{else}}
	<li>
	{{/if}}
		<a href="javascript:;" id="menu_{{MENU_CODE}}" fieldset="json">
			<i class="fa fa-{{MENU_ICON}} fa-lg fa-fw"></i>
			<span class="menu-text">{{MENU_NAME}}</span>
			<input type="hidden" name="HAS_SUB_MENU" value="{{HAS_SUB_MENU}}" />
			<input type="hidden" name="MENU_ICON" value="{{MENU_ICON}}" />
			<input type="hidden" name="MENU_CODE" value="{{MENU_CODE}}" />
			<input type="hidden" name="MENU_NAME" value="{{MENU_NAME}}" />
			<input type="hidden" name="MENU_LEVEL" value="{{MENU_LEVEL}}" />
		</a>
	</li>
	{{else}}
	{{#if IS_SELECT}}
	<li class="active">
	{{else}}
	<li >
	{{/if}}
		<a href="javascript:;" class="dropdown-toggle" id="menu_{{MENU_CODE}}" fieldset="json">
			<i class="fa fa-{{MENU_ICON}} fa-lg fa-fw"></i>
			<span class="menu-text">{{MENU_NAME}}</span>
			<b class="arrow fa fa-angle-down" ></b>
			<input type="hidden" name="HAS_SUB_MENU" value="{{HAS_SUB_MENU}}" />
			<input type="hidden" name="MENU_ICON" value="{{MENU_ICON}}" />
			<input type="hidden" name="MENU_CODE" value="{{MENU_CODE}}" />
			<input type="hidden" name="MENU_NAME" value="{{MENU_NAME}}" />
			<input type="hidden" name="MENU_LEVEL" value="{{MENU_LEVEL}}" />
		</a>
		{{#sub_menu_list SUB_MENUS}}
		{{#unless HAS_SUB_MENU}}
		<li>
			<a href="javascript:;" id="menu_{{MENU_CODE}}" fieldset="json">
				<i class="fa fa-{{MENU_ICON}} fa-lg fa-fw"></i>
				<span class="menu-text">{{MENU_NAME}}</span>
				<input type="hidden" name="HAS_SUB_MENU" value="{{HAS_SUB_MENU}}" />
				<input type="hidden" name="MENU_ICON" value="{{MENU_ICON}}" />
				<input type="hidden" name="MENU_CODE" value="{{MENU_CODE}}" />
				<input type="hidden" name="MENU_NAME" value="{{MENU_NAME}}" />
				<input type="hidden" name="MENU_LEVEL" value="{{MENU_LEVEL}}" />
			</a>
		</li>
		{{else}}
		<li>
			<a href="javascript:;" class="dropdown-toggle" id="menu_{{MENU_CODE}}" fieldset="json">
				<i class="fa fa-{{MENU_ICON}} fa-lg fa-fw"></i>
				<span class="menu-text">{{MENU_NAME}}</span>
				<b class="arrow fa fa-angle-down"></b>
				<input type="hidden" name="HAS_SUB_MENU" value="{{HAS_SUB_MENU}}" />
				<input type="hidden" name="MENU_ICON" value="{{MENU_ICON}}" />
				<input type="hidden" name="MENU_CODE" value="{{MENU_CODE}}" />
				<input type="hidden" name="MENU_NAME" value="{{MENU_NAME}}" />
				<input type="hidden" name="MENU_LEVEL" value="{{MENU_LEVEL}}" />
			</a>
			{{#sub_menu_list SUB_MENUS}}
			{{#unless HAS_SUB_MENU}}
			<li>
				<a href="javascript:;" id="menu_{{MENU_CODE}}" fieldset="json">
					<i class="fa fa-{{MENU_ICON}} fa-lg fa-fw"></i>
					<span class="menu-text">{{MENU_NAME}}</span>
					<input type="hidden" name="HAS_SUB_MENU" value="{{HAS_SUB_MENU}}" />
					<input type="hidden" name="MENU_ICON" value="{{MENU_ICON}}" />
					<input type="hidden" name="MENU_CODE" value="{{MENU_CODE}}" />
					<input type="hidden" name="MENU_NAME" value="{{MENU_NAME}}" />
					<input type="hidden" name="MENU_LEVEL" value="{{MENU_LEVEL}}" />
				</a>
			</li>
			{{else}}
			<li>
				<a href="javascript:;" class="dropdown-toggle" id="menu_{{MENU_CODE}}" fieldset="json">
					<i class="fa fa-{{MENU_ICON}} fa-lg fa-fw"></i>
					<span class="menu-text">{{MENU_NAME}}</span>
					<b class="arrow fa fa-angle-down"></b>
					<input type="hidden" name="HAS_SUB_MENU" value="{{HAS_SUB_MENU}}" />
					<input type="hidden" name="MENU_ICON" value="{{MENU_ICON}}" />
					<input type="hidden" name="MENU_CODE" value="{{MENU_CODE}}" />
					<input type="hidden" name="MENU_NAME" value="{{MENU_NAME}}" />
					<input type="hidden" name="MENU_LEVEL" value="{{MENU_LEVEL}}" />
				</a>
				{{#sub_menu_list SUB_MENUS}}
				{{#unless HAS_SUB_MENU}}
				<li>
					<a href="javascript:;" id="menu_{{MENU_CODE}}" fieldset="json">
						<i class="fa fa-{{MENU_ICON}} fa-lg fa-fw"></i>
						<span class="menu-text">{{MENU_NAME}}</span>
						<input type="hidden" name="HAS_SUB_MENU" value="{{HAS_SUB_MENU}}" />
						<input type="hidden" name="MENU_ICON" value="{{MENU_ICON}}" />
						<input type="hidden" name="MENU_CODE" value="{{MENU_CODE}}" />
						<input type="hidden" name="MENU_NAME" value="{{MENU_NAME}}" />
						<input type="hidden" name="MENU_LEVEL" value="{{MENU_LEVEL}}" />
					</a>
				</li>
				{{/unless}}
				{{/sub_menu_list}}
			</li>
			{{/unless}}
			{{/sub_menu_list}}
		<li>
		{{/unless}}
		{{/sub_menu_list}}
	</li>
	{{/unless}}
	{{/common_list}}

</script>

<!--
<li>
	<a href="#">
		<i class="fa fa-calendar fa-lg fa-fw"></i>

		<span class="menu-text">
			日历
			<span class="badge badge-transparent tooltip-error" title="2&nbsp;Important&nbsp;Events">
				<i class="fa fa-warning red bigger-130"></i>
			</span>
		</span>
	</a>
</li>
-->