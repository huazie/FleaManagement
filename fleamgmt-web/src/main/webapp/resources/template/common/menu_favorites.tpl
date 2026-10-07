<!--
	菜单收藏夹模板(大图标+文字上下展示)
-->
<style>
	/* 收藏夹项:大图标在上,文字在下,横向排列自动换行 */
	.menuFavorites .menu-fav-item {
		display: inline-block;
		width: 100px;
		margin: 4px 10px 10px 0;
		padding: 8px 0 6px 0;
		text-align: center;
		text-decoration: none;
		vertical-align: top;
		border-radius: 4px;
	}
	.menuFavorites .menu-fav-item > i {
		display: block;
		font-size: 30px;
		line-height: 40px;
		margin-bottom: 2px;
		color: #478fca;
	}
	.menuFavorites .menu-fav-item > span {
		display: block;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		padding: 0 4px;
		font-size: 13px;
		color: #393939;
	}
	.menuFavorites .menu-fav-item:hover {
		background: #f1f6f9;
	}
	.menuFavorites .menu-fav-item:hover > i {
		color: #2a6496;
	}
</style>
<script id="tpl_menu_favorites" type="text/x-handlebars-template">
	{{#common_list menuFavoritesList}}
	<a id="favorites_{{MENU_CODE}}" class="menu-fav-item" href="javascript:;" name="{{MENU_CODE}}">
		<i class="fa fa-{{MENU_ICON}}"></i>
		<span>{{MENU_NAME}}</span>
	</a>
	{{/common_list}}
</script>
