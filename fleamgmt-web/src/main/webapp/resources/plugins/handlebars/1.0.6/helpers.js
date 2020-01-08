/**
 * 导航栏 自定义导航栏
 */
Handlebars.registerHelper('nav_list', function(context, options) {
  	var ret = "<ul>";

  	for(var i=0; i < context.length; i++) {
  		if(i == 0){
  			ret += "<li class='current'>" + options.fn(context[i]) + "</li>";
  		}else{
  			ret += "<li>" + options.fn(context[i]) + "</li>";
  		}
  	}

  	ret += "<li class='sideline' style='left: 0px; width: 36px;'></li>" 

  	return ret + "</ul>";
});

/**
 * 子菜单
 */
Handlebars.registerHelper('sub_menu_list', function(context, options) {
  	var ret = "<ul class='submenu'>";

  	for(var i = 0; i < context.length; i++){
		ret +=  options.fn(context[i]);
	}

  	return ret + "</ul>";
});

/**
 * 普通list
 */
Handlebars.registerHelper('common_list', function(context, options){
	var ret = "";

	for(var i = 0; i < context.length; i++){
		ret +=  options.fn(context[i]);
	}
	return ret;
});

/**
 * 注册一个判断是否相等的Helper,判断p1是否等于p2
 */
Handlebars.registerHelper("if_eq",function(context, options){
	if (context == options.hash.compare)
		return options.fn(this);//满足添加继续执行
	return options.inverse(this);//不满足条件执行{{else}}部分
});

/**
 * If Greater Than
 * if_gt this compare=that
 */
Handlebars.registerHelper('if_gt', function(context, options) {
  if (context > options.hash.compare)
    return options.fn(this);
  return options.inverse(this);
});

/**
 * If Less Than
 * if_lt this compare=that
 */
Handlebars.registerHelper('if_lt', function(context, options) {
  if (context < options.hash.compare)
    return options.fn(this);
  return options.inverse(this);
});

