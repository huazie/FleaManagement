<!--
    通用树模板
    id    : 菜单编号
    code  : 菜单编码
    name  : 菜单名称
    level : 菜单层级
    count : 子菜单数目
    type  : folder【有子菜单的菜单】 item【叶子菜单】
-->
<script id="tpl_common_tree" type="text/x-handlebars-template">
	<div class="tree-folder" style="display:none;">
		<div class="tree-folder-header">
			<i class="fa {{close-icon}}"></i>
			<div class="tree-folder-name"></div>
			<input name="id" type="hidden" value="" />
			<input name="code" type="hidden" value="" />
			<input name="name" type="hidden" value="" />
			<input name="level" type="hidden" value="" />
			<input name="count" type="hidden" value="" />
			<input name="type" type="hidden" value="folder" />
		</div>				
		<div class="tree-folder-content">
		</div>				
		<div class="tree-loader" style="display:none"></div>			
	</div>			
	<div class="tree-item" style="display:none;">
		{{#if unselected-icon}}
		<i class="fa {{unselected-icon}}"></i>
		{{/if}}
		<div class="tree-item-name"></div>
        <input name="id" type="hidden" value="" />
        <input name="code" type="hidden" value="" />
        <input name="level" type="hidden" value="" />
		<input name="type" type="hidden" value="item" />
	</div>
</script>
