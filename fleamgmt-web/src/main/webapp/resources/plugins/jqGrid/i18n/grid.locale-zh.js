;(function($){
/**
 * jqGrid 中文语言包
 * 依据 grid.locale-en.js 结构翻译，供 FleaManagement 授权管理列表页使用
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
**/
$.jgrid = $.jgrid || {};
$.extend($.jgrid,{
	defaults : {
		recordtext: "第 {0} - {1} 条，共 {2} 条",
		emptyrecords: "暂无数据",
		loadtext: "加载中...",
		pgtext : "第 {0} 页 / 共 {1} 页"
	},
	search : {
		caption: "查询...",
		Find: "查找",
		Reset: "重置",
		odata: [{ oper:'eq', text:'等于'},{ oper:'ne', text:'不等于'},{ oper:'lt', text:'小于'},{ oper:'le', text:'小于或等于'},{ oper:'gt', text:'大于'},{ oper:'ge', text:'大于或等于'},{ oper:'bw', text:'开始于'},{ oper:'bn', text:'不开始于'},{ oper:'in', text:'属于'},{ oper:'ni', text:'不属于'},{ oper:'ew', text:'结束于'},{ oper:'en', text:'不结束于'},{ oper:'cn', text:'包含'},{ oper:'nc', text:'不包含'}],
		groupOps: [	{ op: "AND", text: "所有" },	{ op: "OR",  text: "任一" }	]
	},
	edit : {
		addCaption: "新增记录",
		editCaption: "编辑记录",
		bSubmit: "提交",
		bCancel: "取消",
		bClose: "关闭",
		saveData: "数据已变更，是否保存？",
		bYes : "是",
		bNo : "否",
		bExit : "取消",
		msg: {
			required:"此字段必填",
			number:"请输入有效的数字",
			minValue:"值必须大于或等于 ",
			maxValue:"值必须小于或等于 ",
			email: "不是有效的邮箱地址",
			integer: "请输入有效的整数",
			date: "请输入有效的日期",
			url: "不是有效的网址（需以 'http://' 或 'https://' 开头）",
			nodefined : " 未定义！",
			novalue : " 需要返回值！",
			customarray : "自定义函数应返回数组！",
			customfcheck : "自定义校验场景下必须提供自定义函数！"
		}
	},
	view : {
		caption: "查看记录",
		bClose: "关闭"
	},
	del : {
		caption: "删除",
		msg: "确认删除选中的记录？",
		bSubmit: "删除",
		bCancel: "取消"
	},
	nav : {
		edittext: "",
		edittitle: "编辑选中行",
		addtext:"",
		addtitle: "新增行",
		deltext: "",
		deltitle: "删除选中行",
		searchtext: "",
		searchtitle: "查找记录",
		refreshtext: "",
		refreshtitle: "刷新表格",
		alertcap: "提示",
		alerttext: "请先选择一行",
		viewtext: "",
		viewtitle: "查看选中行"
	},
	col : {
		caption: "选择列",
		bSubmit: "确定",
		bCancel: "取消"
	},
	errors : {
		errcap : "错误",
		nourl : "未设置请求地址",
		norecords: "没有可处理的数据",
		model : "colNames 与 colModel 长度不一致！"
	},
	formatter : {
		integer : {thousandsSeparator: ",", defaultValue: '0'},
		number : {decimalSeparator:".", thousandsSeparator: ",", decimalPlaces: 2, defaultValue: '0.00'},
		currency : {decimalSeparator:".", thousandsSeparator: ",", decimalPlaces: 2, prefix: "", suffix:"", defaultValue: '0.00'},
		date : {
			dayNames:   [
				"日", "一", "二", "三", "四", "五", "六",
				"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
			],
			monthNames: [
				"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月",
				"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"
			],
			AmPm : ["上午","下午","上午","下午"],
			S: function (j) {return j < 11 || j > 13 ? ['st', 'nd', 'rd', 'th'][Math.min((j - 1) % 10, 3)] : 'th';},
			srcformat: 'Y-m-d',
			newformat: 'Y-m-d',
			parseRe : /[Tt\\\/:_;.,\t\s-]/,
			masks : {
				ISO8601Long:"Y-m-d H:i:s",
				ISO8601Short:"Y-m-d",
				ShortDate: "Y-m-d",
				LongDate: "Y年n月j日",
				FullDateTime: "Y年n月j日 H:i:s",
				MonthDay: "n月j日",
				ShortTime: "H:i",
				LongTime: "H:i:s",
				SortableDateTime: "Y-m-d\\TH:i:s",
				UniversalSortableDateTime: "Y-m-d H:i:sO",
				YearMonth: "Y年n月"
			},
			reformatAfterEdit : false
		},
		baseLinkUrl: '',
		showAction: '',
		target: '',
		checkbox : {disabled:true},
		idName : 'id'
	}
});
})(jQuery);
