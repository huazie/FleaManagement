/**
 * Web开发工具集（Huazie）
 * （1）依赖 jQuery第三方库  ，详细请至官网了解：http://jquery.com/
 *
 * @author huazie
 * @since 1.0.0
 *
 */
var Huazie = {
    /**
     * @type {String}
     * @property version
     */
    VERSION: '1.0.0',
    /**
     *
     * @method toString
     * @return {String} 'Huazie'
     */
    toString: function () {
        return 'Huazie';
    },
    /**
     * console.log方法(兼容IE)
     *
     * @method log
     * @param {String} text
     */
    log: function (text) {
        window.console && console.log(text);
    }
};

/**
 * 通过 HTTP 请求加载远程数据，底层依赖jQuery的AJAX实现。
 * 当前接口实现了对jQuery AJAX接口的进一步封装。
 *
 * @namespace Huazie
 * @class ajax
 */
Huazie.ajax = {
    /**
     * Current object name
     *
     * @method toString
     * @return {String} 'Huazie.ajax'
     */
    toString: function () {
        return "Huazie.ajax";
    },
    /**
     * Request Mode
     *
     * @type {Object}
     * @namespace Huazie.ajax
     * @class reqMode
     */
    reqMode: {
        GET: "GET",	// GET 请求方式
        POST: "POST"	// POST 请求方式
    },
    /**
     * Date type of Request
     *
     * @type {Object}
     * @namespace Huazie.ajax
     * @class dataType
     */
    dataType: {
        TEXT: "text",	// 文本类型
        JSON: 'json', 	// json类型
        HTML: 'html'	// html类型
    },
    /**
     * 超时,默认超时5000ms
     *
     * @type {Number}
     * @property DEFAULT_TIME_OUT
     */
    DEFAULT_TIME_OUT: 10000,
    /**
     * 以 "GET" 请求方式返回 "JSON"(text) 数据类型,默认为异步请求方式
     *
     * @method getJson
     * @param {String} url HTTP(GET)请求地址
     * @param {Object} data json对象参数
     * @param {Function} callback(data, status) GET请求成功回调函数
     */
    getJson: function (url, data, callback) {
        if (arguments.length === 2) {
            callback = data;
            data = ''; // 设置请求参数为空
        }
        var reqMode = this.reqMode.GET;
        var dataType = this.dataType.TEXT;
        this.ajax(url, reqMode, data, dataType, callback, false);
    },
    /**
     * 以 "GET" 请求方式返回 "JSON"(text) 数据类型
     * 采用同步阻塞的方式调用ajax
     *
     * @method getJsonSync
     * @param {String} url HTTP(GET)请求地址
     * @param {Object} data json对象参数
     * @param {Function} callback(data, status) GET请求成功回调函数
     */
    getJsonSync: function (url, data, callback) {
        if (arguments.length === 2) {
            callback = data;
            data = ''; // 设置请求参数为空
        }
        var reqMode = this.reqMode.GET;
        var dataType = this.dataType.TEXT;
        this.ajax(url, reqMode, data, dataType, callback, true);
    },
    /**
     * 以 "POST" 请求方式返回 "JSON"(text) 数据类型,默认为异步请求方式
     *
     * @method postJson
     * @param {String} url HTTP(POST)请求地址
     * @param {Object} data json对象参数
     * @param {Function} callback(data, status)  POST请求成功回调函数
     */
    postJson: function (url, data, callback) {
        var reqMode = this.reqMode.POST;
        var dataType = this.dataType.TEXT;
        this.ajax(url, reqMode, data, dataType, callback, false);
    },
    /**
     * 以 "POST" 请求方式返回 "JSON"(text) 数据类型
     * 采用同步阻塞的方式调用ajax
     *
     * @method postJson
     * @param {String} url HTTP(POST)请求地址
     * @param {Object} data json对象参数
     * @param {Function} callback(data, status)  POST请求成功回调函数
     */
    postJsonSync: function (url, data, callback) {
        var reqMode = this.reqMode.POST;
        var dataType = this.dataType.TEXT;
        this.ajax(url, reqMode, data, dataType, callback, true);
    },
    /**
     * 以"GET" 请求方式返回 "hmtl" 数据类型,默认为异步请求方式
     *
     * @method getHtml
     * @param {String} url HTTP(GET)请求地址
     * @param {Object} data json对象参数,一般为空
     * @param {Function} callback(data, status)  GET请求成功回调函数
     */
    getHtml: function (url, data, callback) {
        if (arguments.length === 2) {
            callback = data;
            data = ''; // 设置请求参数为空
        }
        var reqMode = this.reqMode.GET;
        var dataType = this.dataType.HTML;
        this.ajax(url, reqMode, data, dataType, callback, false);
    },
    /**
     * 采用同步阻塞的方式调用ajax,
     * 以 "GET" 请求方式返回 "hmtl" 数据类型
     *
     * @method getHtmlSync
     * @param {String} url HTTP(GET)请求地址
     * @param {Object} data json对象参数，一般为空
     * @param {Function} callback(data, status) GET请求成功回调函数
     */
    getHtmlSync: function (url, data, callback) {
        if (arguments.length === 2) {
            callback = data;
            data = ''; // 设置请求参数为空
        }
        var reqMode = this.reqMode.GET;
        var dataType = this.dataType.HTML;
        this.ajax(url, reqMode, data, dataType, callback, true);
    },
    /**
     * 基于jQuery ajax的封装
     *
     * @method ajax
     * @param {String} url HTTP(POST/GET) Request Path
     * @param {String} reqMode POST/GET
     * @param {Object} data json对象参数
     * @param {String} dataType 返回的数据类型
     * @param {Function} callback  请求成功回调函数
     * @param {boolean} sync (true: 异步,false:同步)
     */
    ajax: function (url, reqMode, data, dataType, callback, sync) {
        var _data = data;
        var _self = Huazie.ajax;
        var _async = !sync;
        var _cache = (dataType === _self.dataType.HTML);//如果是html数据类型，就采用缓存方式加载
        $.ajax({
            url: url,
            type: reqMode,
            data: _data,
            cache: _cache,
            dataType: dataType,
            async: _async,
            timeout: _self.DEFAULT_TIME_OUT,
            beforeSend: function (xhr) { // 参数为XMLHttpRequest
                xhr.overrideMimeType("text/plain; charset=utf-8");
            },
            success: function (data) {
                if (!data) {
                    return;
                }
                if (_cache) {
                    callback(data, true);
                    return;
                }
                try {
                    data = eval('(' + data + ')');
                } catch (e) {
                    // 提示JSON数据格式化的错误信息
                }
                callback(data, true);
            },
            error: function () {
                var err = {};
                err['retCode'] = "N";
                err['retMess'] = "服务器开小差了,请您稍后重新尝试!!!";
                callback(err, false);
            }
        });
    }

};

/**
 * 用于加载tpl模板文件的工具类
 *
 * @namespace Huazie
 * @class tpl
 */
Huazie.tpl = {
    /**
     * Current object name
     *
     * @method toString
     * @return {String} 'Huazie.tpl'
     */
    toString: function () {
        return "Huazie.tpl";
    },
    /**
     * tpl文件是否已经加载过
     *
     * @type {Object}
     */
    isLoaded: {
        // "../xxx.tpl" : true,
        // "../yyy.tpl" : true
    },
    /**
     * 加载外部的tpl文件
     *
     * @method loadTpl
     * @param {String} tplPath (Path of outer tpl file)
     * @param {Function} callback 回调函数
     */
    loadTpl: function (tplPath, callback) {
        var _self = this;
        if (!this.isLoaded[tplPath]) { // 没有被加载过
            Huazie.ajax.getHtml(tplPath, function (htmlData, status) {
                if (status) {
                    $('body').append(htmlData);
                    _self.isLoaded[tplPath] = true;
                    if (callback) {
                        callback();
                    }
                }
            });
        } else {
            if (callback) {
                callback();
            }
        }
    },
    /**
     * 获取加载过数据的模板
     *
     * @method getTemp
     * @param {String} temp 模板对象
     * @param {Object} data 模板的json数据
     */
    getTemp: function (temp, data) {
        var source = $(temp).html();
        var template = Handlebars.compile(source);
        return template(data);
    },
    /**
     * 对Handlebars进行封装，为模板加载数据
     *
     * @method loadTemp
     * @param {Object} obj Dom对象
     * @param {String} temp 模板对象
     * @param {Object} data 模板的json数据
     */
    loadTemp: function (obj, temp, data) {
        var source = $(temp).html();
        var template = Handlebars.compile(source);
        obj.html(template(data));
    },
    /**
     * 对Handlebars进行封装，为模板加载数据，并追加到obj中

     * @method appendTemp
     * @param {Object} obj Dom对象
     * @param {String} temp 模板对象
     * @param {Object} data 模板的json数据
     */
    appendTemp: function (obj, temp, data) {
        var source = $(temp).html();
        var template = Handlebars.compile(source);
        var oldHtml = obj.html();
        obj.html(oldHtml + template(data));
    }

};

/**
 * 浏览器，URL等相关操作
 *
 * @namespace Huazie
 * @class browser
 */
Huazie.browser = {

    /**
     * Current object name
     *
     * @method toString
     * @return {String} 'Huazie.validate'
     */
    toString: function () {
        return "Huazie.browser";
    },
    /**
     * 浏览器UA
     *
     * @name Huazie.browser.ua
     * @return {String} 浏览器UA
     */
    ua: navigator.userAgent.toLowerCase(),
    /**
     * ie
     *
     * @name Huazie.browser.ie
     * @return {Boolean} true/false
     */
    ie: /msie/.test(navigator.userAgent.toLowerCase()),
    /**
     * ie6
     *
     * @name Huazie.browser.ie6
     * @return {Boolean} true/false
     */
    ie6: /msie 6/.test(navigator.userAgent.toLowerCase()),
    /**
     * ie7
     *
     * @name Huazie.browser.ie7
     * @return {Boolean} true/false
     */
    ie7: /msie 7/.test(navigator.userAgent.toLowerCase()),
    /**
     * ie8
     *
     * @name Huazie.browser.ie8
     * @return {Boolean} true/false
     */
    ie8: /msie 8/.test(navigator.userAgent.toLowerCase()),
    /**
     * ie9
     *
     * @name Huazie.browser.ie9
     * @return {Boolean} true/false
     */
    ie9: /msie 9/.test(navigator.userAgent.toLowerCase()),
    /**
     * firefox
     *
     * @name Huazie.browser.firefox
     * @return {Boolean} true/false
     */
    firefox: /firefox\/(\d+\.\d+)/i.test(navigator.userAgent.toLowerCase()),
    /**
     * chrome
     *
     * @name Huazie.browser.chrome
     * @return {Boolean} true/false
     */
    chrome: /chrome\/(\d+\.\d+)/i.test(navigator.userAgent.toLowerCase()),
    /**
     * opera
     *
     * @name Huazie.browser.opera
     * @return {Boolean} true/false
     */
    opera: /opera/.test(navigator.userAgent.toLowerCase()),
    // safari: /safari/.test(navigator.userAgent.toLowerCase()),
    /**
     * webkit
     *
     * @name Huazie.browser.webkit
     * @return {Boolean} true/false
     */
    webkit: /webkit/.test(navigator.userAgent.toLowerCase()),
    /**
     * 判断是不是业务请求
     *
     * @param {String} url 业务请求url
     * @return {Boolean} true:是 /false
     */
    isBusiRequest: function (url) {
        var request = url || window.location.href;
        var busiReqPrefix = ReqUrlMap.getReqUrlPrefix();
        return request.contains(busiReqPrefix);
    },
    /**
     * 判断是不是页面跳转
     *
     * @param {String} url 页面跳转请求url
     * @return {Boolean} true:是 /false
     */
    isPageRequest: function (url) {
        var request = url || window.location.href;
        var pageReqPrefix = ReqUrlMap.getPageReqUrlPrefix();
        return request.contains(pageReqPrefix);
    },
    /**
     * 获取Frame参数值
     *
     * @param {String} name 参数的键
     * @returns
     */
    getFrameParameter: function (name) {
        if (!window.frameElement) {
            return "index";
        }
        var url = window.frameElement.src;
        return this.getParameter(name, url);
    },
    /**
     * 获取URL地址栏参数值
     *
     * @method getParameter
     * @param {String} name 参数名
     * @param {String} url [optional,default=当前URL]URL地址
     * @return {String} 参数值
     */
    getParameter: function (name, url) {
        var reqUrl = url || window.location.href;
        if (reqUrl.length === 0) {
            return null;
        }
        if (reqUrl.indexOf("?") === -1) {
            return null;
        }
        reqUrl = unescape(reqUrl).substring(reqUrl.indexOf("?") + 1);
        if (reqUrl.length === 0) {
            return null;
        }
        var params = reqUrl.split('&');
        for (var i = 0; i < params.length; i++) {
            var parts = params[i].split('=', 2);
            if (parts[0] === name) {
                if (parts.length < 2 || typeof (parts[1]) === "undefined" || parts[1] === "null")
                    return '';
                return parts[1];
            }
        }
        return null;
    }

};

/**
 * 常用提示文本
 *
 * @namespace Huazie
 * @class msg
 */
Huazie.msg = {
    /**
     * Current object name
     *
     * @method toString
     * @return {String} 'Huazie.msg'
     */
    toString: function () {
        return "Huazie.msg";
    },
    /**
     * 按钮显示的通用文本
     *
     * @param icon 按钮小图标
     * @param mess 按钮文本
     * @return {String}
     */
    btnText: function (icon, mess) {
        return "<i class=\"fa fa-" + icon + " fa-lg\"></i>" + mess;
    },
    /**
     * tbody中显示的加载文本

     * @param colspan 收缩列数
     * @param mess 加载提示文本
     * @return {String}
     */
    tbodyProgressText: function (colspan, mess) {
        return "<tr><td colspan='" + colspan + "' align='center'> <i class='fa fa-spinner fa-pulse fa-lg'></i><span class='black bigger-110'> " + mess + "</span></td></tr>";
    }


};

/**
 * 常用正则表达式验证
 *
 * @namespace Huazie
 * @class validate
 */
Huazie.validate = {

    /**
     * Current object name
     *
     * @method toString
     * @return {String} 'Huazie.validate'
     */
    toString: function () {
        return "Huazie.validate";
    },
    /**
     * 正则集
     *
     * @type {Object}
     * @namespace Huazie.validate
     * @class regExp
     */
    regExp: {
        num: "^([+-]?)\\d*\\.?\\d+$",		//数字
        pNum: "^\\d+(\\.\\d+)?$",			//正数
        nNum: "^(\\-?)\\d+(\\.\\d+)?$",	//负数
        integer: "^-?[1-9]\\d*$",				//整数
        pInteger: "^[1-9]\\d*$",				//正整数
        nInteger: "^-[1-9]\\d*$",				//负整数
        nnInteger: "^[1-9]\\d*|0$",				//非负整数（正整数 + 0）
        npInteger: "^-[1-9]\\d*|0$",			//非正整数（负整数 + 0）
        float: "^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$",	//浮点数
        pFloat: "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$", 	//正浮点数
        nFloat: "^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$", 	//负浮点数
        nnFloat: "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0$", 		//非负浮点数（正浮点数 + 0）
        npFloat: "^(-([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*))|0?\\.0+|0$", 	//非正浮点数（负浮点数 + 0）
        email: /^\w+((-\w)|\.\w+)*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/, //邮箱账号
        password: /^(?=.*[a-zA-Z]+)(?=.*[0-9]+)[a-zA-Z0-9]+$/, 		//6~16位的字母和数字的组合
        phone: /^(13[0-9]|14[7|5]|15[0-35-9]|18[0-25-9])\d{8}$/, 	//手机号码
        url: "^http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$"	 	//url
    },
    /**
     * 正则表达式开始校验
     *
     * @method test
     * @param {String} type 验证类型
     * @param {String} value 验证值
     */
    test: function (type, value) {
        var _exp = this.regExp[type];
        if (!_exp) {
            alert("The expression " + type + " is not in regExp");
            return false;
        }
        var exp;
        if (typeof _exp == "string") {
            exp = new RegExp(_exp);
        } else {
            exp = _exp;
        }
        return exp.test(value);
    }

};

/**
 * 常用Dialog弹出
 *
 * @namespace Huazie
 * @class dialog
 */
Huazie.dialog = {
    /**
     * Current object name
     *
     * @method toString
     * @return {String} 'Huazie.dialog'
     */
    toString: function () {
        return "Huazie.dialog";
    },
    /**
     * 提示信息
     *
     * @param icon "info" 通知  "warning" 警告
     * @param mess 展示内容
     * @param time 显示时长，以秒为单位
     */
    tips: function (icon, mess, time) {
        Huazie.tpl.loadTpl(TplUrlMap.get("dialog"), function () {
            var data = {
                "ICON": icon,
                "CONTENT": mess
            };
            var d = dialog({
                fixed: true,
                padding: 10,
                content: Huazie.tpl.getTemp("#tpl_dialog_tips", data)
            });
            d.showModal();
            setTimeout(function () {
                d.close().remove();
            }, time * 1000);
        });
    },
    /**
     * 通用的确认取消弹出框
     *
     * @param icon "info" 通知  "warning" 警告
     * @param mess 展示内容
     * @param confirmCallback 确认按钮回调事件
     * @param cancelCallback 取消按钮回调事件
     */
    confirm: function (icon, mess, confirmCallback, cancelCallback) {
        Huazie.tpl.loadTpl(TplUrlMap.get("dialog"), function () {
            var data = {
                "ICON": icon,
                "CONTENT": mess
            };
            var d = dialog({
                fixed: true,
                padding: 15,
                content: Huazie.tpl.getTemp("#tpl_dialog_confirm", data),
                okValue: '确定',
                ok: function () {
                    if (confirmCallback) {
                        confirmCallback();
                    }
                },
                cancelValue: '取消',
                cancel: function () {
                    if (cancelCallback) {
                        cancelCallback();
                    }
                }
            });
            d.showModal();
        });
    },
    /**
     * 右击弹出气泡浮层
     */
    rightClickBubble: function (data, obj, callback) {
        Huazie.tpl.loadTpl(TplUrlMap.get("dialog"), function () {
            var d = dialog({
                padding: 2,
                content: Huazie.tpl.getTemp("#tpl_dialog_menu", data),
                quickClose: true
            });
            d.show(obj);
            if (callback) {
                callback(d);
            }
        });
    }


};

/**
 * 表单处理工具
 *
 * @namespace Huazie
 * @class form
 */
Huazie.form = {
    /**
     * 显示当前对象名称路径。
     *
     * @method toString
     * @return {String} 'Huazie.form'
     */
    toString: function () {
        return 'Huazie.form';
    },
    /**
     * 常用键盘码对象。
     *
     * @type {Object}
     * @namespace fis.form
     * @class keycode
     */
    keycode: {
        /**
         * 全屏F11(122)
         *
         * @type {Number}
         * @property F11
         */
        F11: 122,
        /**
         * 退出Esc(27)
         *
         * @type {Number}
         * @property ESC
         */
        ESC: 27,
        /**
         * 回车Enter(13)
         *
         * @type {Number}
         * @property ENTER
         */
        ENTER: 13,
        /**
         * 上一页Page Up(33)
         *
         * @type {Number}
         * @property PAGEUP
         */
        PAGEUP: 33,
        /**
         * 下一页Page Down(34)
         *
         * @type {Number}
         * @property PAGEDOWN
         */
        PAGEDOWN: 34,
        /**
         * 页尾end(35)
         *
         * @type {Number}
         * @property END
         */
        END: 35,
        /**
         * 页首home(36)
         *
         * @type {Number}
         * @property HOME
         */
        HOME: 36,
        /**
         * 左箭头left(37)
         *
         * @type {Number}
         * @property LEFT
         */
        LEFT: 37,
        /**
         * 向上箭头up(38)
         *
         * @type {Number}
         * @property UP
         */
        UP: 38,
        /**
         * 右前头(39)
         *
         * @type {Number}
         * @property RIGHT
         */
        RIGHT: 39,
        /**
         * 向下箭头down(40)
         *
         * @type {Number}
         * @property DOWN
         */
        DOWN: 40
    },
    /**
     * 绑定键盘事件到元素，当焦点在元素上并触发键盘事件时响应该函数。
     *
     * @method _bindKey
     * @param {Number} fis.form.keycode 键盘码
     * @param {Object} element 被绑定元素的jQuery对象
     * @param {Function} callback 回调函数，参数为绑定的元素对象element和事件e
     */
    _bindKey: function (keycode, element, callback) {
        element.keydown(function (e) {
            if (e.keyCode === keycode) {
                if (typeof callback == 'function')
                    callback(element, e);
            }
        });
    },
    /**
     * 在element区域内响应Enter键盘事件。<br>
     * 实际处理中应该将提交按键(type="submit")放在element区域外,避免重复提交。
     *
     * @method bindEnterKey
     * @param {Object} element 被绑定元素的jQuery对象
     * @param {Function} callback 回调函数，参数为绑定的元素对象element和事件e
     * @return {Object} fis.form
     */
    bindEnterKey: function (element, callback) {
        this._bindKey(this.keycode.ENTER, element, callback);
        return this;
    },
    /**
     * 在element区域内响应Esc键盘事件。
     *
     * @method bindEscKey
     * @param {Object} element 被绑定元素的jQuery对象
     * @param {Function} callback 回调函数，参数为绑定的元素对象element和事件e
     * @return {Object} fis.form
     */
    bindEscKey: function (element, callback) {
        this._bindKey(this.keycode.ESC, element, callback);
        return this;
    },
    /**
     * 在element区域内响应F11键盘事件。
     *
     * @method bindF11Key
     * @param {Object} element 被绑定元素的jQuery对象
     * @param {Function} callback 回调函数，参数为绑定的元素对象element和事件e
     * @return {Object} fis.form
     */
    bindF11Key: function (element, callback) {
        this._bindKey(this.keycode.F11, element, callback);
        return this;
    },
    /**
     * 在element区域内响应Page Down键盘事件。
     *
     * @method bindPageDownKey
     * @param {Object} element 被绑定元素的jQuery对象
     * @param {Function} callback 回调函数，参数为绑定的元素对象element和事件e
     * @return {Object} fis.form
     */
    bindPageDownKey: function (element, callback) {
        this._bindKey(this.keycode.PAGEDOWN, element, callback);
        return this;
    },
    /**
     * 在element区域内响应Page Up键盘事件。
     *
     * @method bindPageUpKey
     * @param {Object} element 被绑定元素的jQuery对象
     * @param {Function} callback 回调函数，参数为绑定的元素对象element和事件e
     * @return {Object} fis.form
     */
    bindPageUpKey: function (element, callback) {
        this._bindKey(this.keycode.PAGEUP, element, callback);
        return this;
    },
    /**
     * 在element区域内响应Left键盘事件。
     *
     * @method bindLeftKey
     * @param {Object} element 被绑定元素的jQuery对象
     * @param {Function} callback 回调函数，参数为绑定的元素对象element和事件e
     * @return {Object} fis.form
     */
    bindLeftKey: function (element, callback) {
        this._bindKey(this.keycode.LEFT, element, callback);
        return this;
    },
    /**
     * 在element区域内响应Right键盘事件。
     *
     * @method bindRightKey
     * @param {Object} element 被绑定元素的jQuery对象
     * @param {Function} callback 回调函数，参数为绑定的元素对象element和事件e
     * @return {Object} fis.form
     */
    bindRightKey: function (element, callback) {
        this._bindKey(this.keycode.RIGHT, element, callback);
        return this;
    },
    /**
     * 在element区域内响应Up键盘事件。
     *
     * @method bindUpKey
     * @param {Object} element 被绑定元素的jQuery对象
     * @param {Function} callback 回调函数，参数为绑定的元素对象element和事件e
     * @return {Object} fis.form
     */
    bindUpKey: function (element, callback) {
        this._bindKey(this.keycode.UP, element, callback);
        return this;
    },
    /**
     * 在element区域内响应Down键盘事件。
     *
     * @method bindDownKey
     * @param {Object} element 被绑定元素的jQuery对象
     * @param {Function} callback 回调函数，参数为绑定的元素对象element和事件e
     * @return {Object} fis.form
     */
    bindDownKey: function (element, callback) {
        this._bindKey(this.keycode.DOWN, element, callback);
        return this;
    },
    /**
     * 获取单选框值,如果有表单就在表单内查询,否则在全文查询
     *
     * @method getRadioValue
     * @param {String}name radio名称
     * @param {Object} frm [optional,default=document] jQuery表单（或其它容器对象）对象
     * @return {Object} radio jQuery对象
     */
    getRadioValue: function (name, frm) {
        if (frm && frm.find)
            return frm.find('input[name="' + name + '"]:checked').val();
        return $('input[name="' + name + '"]:checked').val();
    },
    /**
     * 设置单选框值,如果有表单就在表单内查询,否则在全文查询。
     *
     * @method setRadioValue
     * @param {String} name radio名称
     * @param {String} value radio表单value值
     * @param {Object} frm [optional,default=document] jQuery表单（或其它容器对象）对象
     * @return {Object} radio jQuery对象
     */
    setRadioValue: function (name, value, frm) {
        if (frm && frm.find)
            return frm
                .find('input[name="' + name + '"][value="' + value + '"]')
                .attr('checked', true);
        return $('input[name="' + name + '"][value="' + value + '"]').attr(
            'checked', true);
    },
    /**
     * 设置select下拉框的值
     *
     * @method setRadioValue
     * @param {String} selectId 下拉框id号
     * @param {String/Number} value select表单value值
     * @param {Object} frm [optional,default=document] jQuery表单（或其它容器对象）对象
     * @return {Object} select jQuery对象
     */
    setSelectValue: function (selectId, value, frm) {
        if (frm && frm.find)
            return frm.find('#' + selectId + ' option[value="' + value + '"]')
                .attr('selected', true);
        return $('#' + selectId + ' option[value="' + value + '"]').attr(
            'selected', true);
    },
    /**
     * 将object转换为select的列表模式，key为option的value，值为option的文本。
     *
     * @method object2Options
     * @param {Object}objects key-map对象
     * @return {String} html
     */
    object2Options: function (objects) {
        if (!$.isPlainObject(objects)) {
            return '';
        }
        var html = [];
        for (var i in objects) {
            html.push('<option value="' + i + '">' + objects[i] + '</option>');
        }
        return html.join('');
    },
    /**
     * 禁用/启用输入控件。
     *
     * @method formDisable
     * @param {Object} frmObj iQuery表单对象（或其它任何包装容器，如：div）
     * @param {Boolean} disabled true-禁用;false-启用
     * @return {Object} fis.form
     */
    formDisable: function (frmObj, disabled) {
        frmObj.find('input,select,textarea').attr('disabled', disabled);
        return this;
    },
    /**
     * 将输入控件集合序列化成对象， 名称或编号作为键，value属性作为值。
     *
     * @method _serializeInputs
     * @param {Array} inputs input/select/textarea的对象集合
     * @return {Object} json 对象 {key:value,...}
     */
    _serializeInputs: function (inputs) {
        var json = {};
        if (!inputs) {
            return json;
        }
        for (var i = inputs.length - 1; i >= 0; i--) {
            var input = $(inputs[i]), type = input.attr('type');
            if (type) {
                type = type.toLowerCase();
            }
            var tagName = input.get(0).tagName, id = input.attr('id'), name = input
                .attr('name'), value = null;

            // 判断输入框是否已经序列化过 || 是否跳过
            if (input.hasClass('_isSerialized') || (input.hasClass('skip_over')) || (typeof id == 'undefined' && typeof name == 'undefined')) {
                continue;
            }

            // input输入标签
            if (tagName === 'INPUT' && type) {
                switch (type) {
                    case 'checkbox': {
                        value = input.is(':checked');
                    }
                        break;
                    case 'radio': {
                        if (input.is(':checked')) {
                            value = input.attr('value');
                        } else {
                            continue;
                        }
                    }
                        break;
                    default: {
                        // 如果是placeholder则不抓取
                        value = (input.val() != input.attr('defaultchars')) ? input.val() : '';
                    }
                }
            } else {
                // 非input输入标签，如：select,textarea
                value = input.val();
            }

            json[name || id] = $.trim(value);
            // 清除序列化标记
            input.removeClass('_isSerialized');
        }
        return json;
    },
    /**
     * 在分组中查找子层的 fieldset (如：fieldset="user")的数据域，获取对应属性，并递归调用生成Array和Object。
     *
     * @method _serializeField
     * @param {Array} field 一个分组容器
     * @return {Object} json 对象 {key:value,...}
     */
    _serializeField: function (field) {
        var json = {};
        if (!field) {
            return json;
        }

        var key = field.attr('fieldset');
        if ((!key) || field.hasClass('skip_flag')) {
            return;
        }
        switch (key) {
            case 'arrayItem' :
            case 'object':
            case 'json' : {
                // 序列化input
                var inputs = field.find('input[type!=button][type!=reset][type!=submit],select,textarea').filter(function (index) {
                    if ($(this).parents('[fieldset]:first').is(field))
                        return true;
                });
                json = this._serializeInputs(inputs);
                // 添加序列化标记
                //inputs.addClass('_isSerialized');
                // 递归序列化子对象、子数组
                var childFields = field.find('[fieldset]').filter(function (index) {
                    if ($(this).parents('[fieldset]:first').is(field) && !$(this).hasClass('skip_flag'))
                        return true;
                });
                for (var i = childFields.length - 1; i >= 0; i--) {
                    var childField = $(childFields[i]);
                    if (childField.attr('name')) {
                        json[childField.attr('name')] = this._serializeField(childField);
                    } else {
                        json[i] = this._serializeField(childField);
                    }
                }
            }
                break;
            case 'array' : {
                json = [];
                var arrayItems = field.find('[fieldset=arrayItem]').filter(function (index) {
                    if ($(this).parents('[fieldset]:first').is(field) && !$(this).hasClass('skip_flag'))
                        return true;
                });
                for (var i = arrayItems.length - 1; i >= 0; i--) {
                    var arrayItem = $(arrayItems[i]);
                    json[i] = this._serializeField(arrayItem);
                }
            }
                break;
        }
        return json;
    },
    /**
     * 序列化表单值,结果以key/value形式返回key为表单对象名称(name||id),value为其值。<br>
     * HTML格式：<br>
     * 1).表单容器：通常是一个form表单（如果不存在就以body为父容器），里面包含输入标签和子容器;<br>
     * 2).子容器（也可以没有）：必须包括属性fieldset="XXXX" div标签，里面包含输入标签和子容器。<br>
     * 序列化后将生成以XXX为主键的json对象.如果子容器存在嵌套则以fieldset为主键生成不同分组的json对象。<br>
     * 3).输入标签：输入标签为input类型标签（包括：'checkbox','color','date','datetime','datetime-local',<br>
     * 'email','file','hidden','month','number','password','radio','range
     * ','reset','search','submit',<br>
     * 'tel','text','time ','url','week'）。
     * 而'button','reset','submit','image'会被过虑掉。
     *
     * @method serialize
     * @param {Object} frm jQuery表单对象
     * @return {Object} json对象，可包含多结构
     */
    serialize: function (frm) {
        var json = {};
        frm = frm || $('[fieldset=json]');
        if (!frm) {
            return json;
        }
        // 为skip_flag区域的input增加跳过标记
        frm.find('skip_flag input[type!=button][type!=reset][type!=submit],skip_flag select,skip_flag textarea').addClass('skip_over');

        json = this._serializeField(frm);

        return json;
    },
    /**
     * 获取合法的输入标签。
     *
     * @method _filterInputs
     * @param {Object} container jQuery对象，标签容器
     * @return {Array} inputs jQuery对象数组
     */
    _filterInputs: function (container) {
        return $(container
            .find('input[type!=button][type!=reset][type!=submit][type!=image][type!=file],select,textarea'));
    },
    /**
     * 查找符合条件的输入标签。
     *
     * @method _findInputs
     * @param {Array} inputs jQuery输入标签数组
     * @param {String} key 查询关键字
     * @return {Array} inputs jQuery对象数组
     */
    _findInputs: function (inputs, key) {
        return $(inputs.filter('input[name=' + key + '],input[id=' + key
            + '],textarea[name=' + key + '],textarea[id=' + key
            + '],select[name=' + key + '],select[id=' + key + ']'));
    }
};

/**
 * 常用正则表达式验证
 *
 * @namespace Huazie
 * @class data
 */
Huazie.data = {

    /**
     * Current object name
     *
     * @method toString
     * @return {String} 'Huazie.data'
     */
    toString: function () {
        return "Huazie.data";
    },
    /**
     *  安全类型转换
     *
     * @param obj 要转化的对象
     * @param defaultValue 默认值
     *
     */
    convertToInt: function (obj, defaultValue) {
        if (!obj) {
            return defaultValue;
        }
        return parseInt(obj);
    }

};
