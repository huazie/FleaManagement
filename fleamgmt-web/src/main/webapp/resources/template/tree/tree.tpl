<!--

-->
<script id="tpl_common_tree" type="text/x-handlebars-template">
	<div class="tree-folder" style="display:none;">
		<div class="tree-folder-header">
			<i class="fa {{close-icon}}"></i>
			<div class="tree-folder-name"></div>
			<input name="code" type="hidden" value="" />
			<input name="type" type="hidden" value="folder" />
		</div>				
		<div class="tree-folder-content">
		</div>				
		<div class="tree-loader" style="display:none"></div>			
	</div>			
	<div class="tree-item" style="display:none;">
		{{#if unselected-icon}}
		<i class="fa fa-{{unselected-icon}}"></i>
		{{/if}}
		<div class="tree-item-name"></div>
		<input name="code" type="hidden" value="" />
		<input name="type" type="hidden" value="item" />
	</div>
	
</script>
