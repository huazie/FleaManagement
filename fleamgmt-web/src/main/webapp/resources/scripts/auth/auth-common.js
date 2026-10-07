/**
 * @Description auth-common.js 授权管理模块公共脚本
 *              提供四类页面形态的公共引擎，供 auth/function/**、auth/role/**、
 *              auth/privilege/**、auth/user/** 各模块复用，避免同一套逻辑散落多份：
 *              <ul>
 *                  <li>FormPage   —— 左侧列表树 + 右侧表单（菜单、操作、元素、资源等增改页）；</li>
 *                  <li>WizardPage —— FuelUX 分步向导（用户注册、用户组新增等字段较多的录入页）；</li>
 *                  <li>GridPage   —— jqGrid 明细表格 + 右侧编辑面板（用户变更、用户组变更）；</li>
 *                  <li>AuthPage   —— 主体表格 + 标签页分维度 + 左右穿梭（用户授权、用户组授权）。</li>
 *              </ul>
 *
 * @author huazie
 * @version v1.1.3
 * @date 2026年9月27日
 */
define(function (require, exports, module) {

    /* ==================== 通用工具 ==================== */

    /**
     * 窄屏断点，与 Bootstrap 3 的 xs 保持一致。
     * <p> 小于等于该宽度视为手机视口：表格裁剪次要列、由页面级搜索框替代列筛选行。
     */
    var MOBILE_MAX = 767;

    /**
     * 数值列名约定：主体编号（xxxId）与计数（xxxCount）。
     * <p> jqGrid 的 sorttype 缺省为 "text"，即按字典序比较——编号 10000 会排在 1001 之前。
     * 这两类列一律按数值排序，列上可用 sorttype 显式覆盖（如日期列写 "date"）。
     */
    var NUMERIC_COLUMN = /(Id|Count)$/;

    /**
     * 当前是否处于窄屏（手机）视口
     *
     * @return true-窄屏
     */
    function isMobile() {
        return window.innerWidth <= MOBILE_MAX;
    }

    /**
     * 视口变化监听（去抖）。
     * <p> 手机横竖屏切换、桌面端拖拽窗口都会触发，用于重新裁剪表格列。
     *
     * @param handler 回调
     */
    function onViewportChange(handler) {
        var timer = null;
        $(window).on("resize.authViewport orientationchange.authViewport", function () {
            if (timer) {
                clearTimeout(timer);
            }
            timer = setTimeout(handler, 200);
        });
    }

    /**
     * 状态值文案与徽章样式。
     * <p> 与 flea_user / flea_account / flea_user_group 的 state 字段语义保持一致：
     * 1-正常 2-禁用 3-待审核。
     */
    var STATE_MAP = {
        1: {cls: "label-success", text: "正常"},
        2: {cls: "label-danger", text: "禁用"},
        3: {cls: "label-warning", text: "待审核"}
    };

    /**
     * 剔除富文本标签，仅保留纯文本
     *
     * @param value 待处理的值
     * @return 纯文本
     */
    function stripTags(value) {
        return (value === undefined || value === null) ? value : String(value).replace(/<[^>]*>/g, "");
    }

    /**
     * HTML 转义，避免接口数据中的特殊字符破坏页面结构
     *
     * @param value 待处理的值
     * @return 转义后的文本
     */
    function escapeHtml(value) {
        if (value === undefined || value === null) {
            return "";
        }
        return String(value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;");
    }

    /**
     * 渲染状态徽章
     *
     * @param state 状态值
     * @return 徽章 HTML
     */
    function stateBadge(state) {
        var item = STATE_MAP[state] || {cls: "label-default", text: "未知"};
        return '<span class="label ' + item.cls + '">' + item.text + '</span>';
    }

    /**
     * 统一的 GET 请求受理。
     * <p> 约定：返回体未带 retCode 时视为成功（部分接口以 treeList 直接返回），
     * 带 retCode 时仅 Y 视为成功，失败统一给出提示。
     *
     * @param url       请求地址（已解析的地址）
     * @param params    请求参数
     * @param onSuccess 成功回调 function(result)
     */
    function request(url, params, onSuccess) {
        Huazie.ajax.getJson(url, params || {}, function (data, status) {
            var result = data || {};
            if (!status || (result.retCode && result.retCode !== "Y")) {
                Huazie.dialog.tips("warning", result.retMess || "亲，数据加载失败，请稍后重试！", 2);
                return;
            }
            if (onSuccess) {
                onSuccess(result);
            }
        });
    }

    /**
     * 加载下拉框数据源。
     * <p> 数据取自列表接口的 treeList（Fuelux 树扁平节点）或 rows（明细行），
     * 统一以「id / userGroupId / roleGroupId」作为值，「name / xxxName」作为展示文案。
     *
     * @param item          下拉配置 {id: 控件编号, url: 接口键, label: 中文名}
     * @param selectedValue 回显值（可选）
     */
    function loadSelect(item, selectedValue) {

        request(ReqUrlMap.get(item.url), {}, function (result) {

            var options = ['<option value="">请选择' + (item.label || "") + '</option>'];
            var list = result.treeList || result.rows || [];

            // 名称可能重名（例如库里存在两个「系统用户」），先统计同名项，
            // 仅对重名项追加编号，既保证可区分，又不会让正常下拉被编号淹没。
            var texts = [];
            var textCount = {};

            for (var i = 0; i < list.length; i++) {
                var text = escapeHtml(stripTags(list[i].name || list[i][item.textKey] || ""));
                texts.push(text);
                textCount[text] = (textCount[text] || 0) + 1;
            }

            for (var j = 0; j < list.length; j++) {
                var row = list[j];
                var value = (row.id !== undefined && row.id !== null) ? row.id : row[item.valueKey];
                var label = texts[j];
                if (textCount[label] > 1) {
                    label += "（编号 " + value + "）";
                }
                options.push('<option value="' + value + '">' + label + '</option>');
            }

            var $select = $("#" + item.id);
            $select.html(options.join(""));

            if (selectedValue !== undefined && selectedValue !== null && selectedValue !== "") {
                $select.val(String(selectedValue));
            }

        });

    }

    /**
     * 批量必填校验
     *
     * @param data     表单数据
     * @param required 必填配置 [[字段, 中文名], ...]
     * @return true-校验通过; false-存在未填写项
     */
    function validateRequired(data, required) {
        if (!required) {
            return true;
        }
        for (var i = 0; i < required.length; i++) {
            var key = required[i][0];
            if (data[key] === undefined || data[key] === null) {
                continue;
            }
            if (!checkRequired(data[key], required[i][1])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 读取树节点（或列表项）携带的信息。
     * <p> fuelux 树会把节点数据渲染成一组 hidden 域，字段固定为
     * id / code / name / level / count / type / sort。
     *
     * <p> 注意：fuelux 树仅在 folder 节点写入 name / count 隐藏域，item 节点只写
     * id / code / level，其余字段通过 {@code $(el).data(nodeMap)} 挂在节点上；
     * 因此这里统一采用「隐藏域优先，节点 data 兜底」的取值方式。
     *
     * @param $node 树节点 jQuery 对象
     * @return 节点信息对象
     */
    function readNode($node) {
        var nodeData = $node.data() || {};
        return {
            id: $node.find("input[name=id]").val() || nodeData.id,
            code: $node.find("input[name=code]").val() || nodeData.code,
            name: $node.find("input[name=name]").val() || stripTags(nodeData.name),
            level: $node.find("input[name=level]").val() || nodeData.level,
            count: $node.find("input[name=count]").val() || nodeData.count,
            sort: $node.find("input[name=sort]").val() || nodeData.sort,
            type: $node.find("input[name=type]").val() || nodeData.type
        };
    }

    /**
     * 绑定气泡菜单的点击事件。
     * <p> 说明：通用气泡模板（dialog.tpl 中的 tpl_dialog_menu）以 FUNCTION_EVENT 作为
     * 事件名，并把节点信息写入 hidden 域，模板内字段沿用 MENU_* 命名（历史约定，
     * 非菜单类页面同样复用该模板，此处统一在 readNode 中转换为节点信息对象）。
     *
     * @param dialog   气泡浮层对象
     * @param node     节点信息对象
     * @param callback 事件回调 function(eventName, node, dialog)
     */
    function bindMenuEvent(dialog, node, callback) {
        $("a[id^='function_']").off("click").on("click", function () {
            var eventName = $(this).attr("name");
            dialog.close();
            if (callback) {
                callback(eventName, node, dialog);
            }
        });
    }

    /**
     * 加载左侧树（列表）数据并初始化 Ace 树控件
     *
     * @param opts 配置对象
     *        treeId      树容器编号（不含 #）
     *        url         数据请求地址
     *        dataKey     响应体中树数据的键，缺省 treeList（菜单树为 menuList）
     *        buildMenu   右击气泡菜单构造方法 function(node)，返回 undefined 表示不弹气泡
     *        onMenuEvent 气泡菜单点击回调 function(eventName, node, dialog)
     *        onLoaded    树初始化完成回调 function(tree)
     */
    function loadTree(opts) {
        var dataKey = opts.dataKey || "treeList";

        Huazie.ajax.getJson(opts.url, function (data, status) {
            var result = data;
            if (!status) {
                Huazie.dialog.tips("warning", (result && result.retMess) || "亲，数据加载失败，请稍后重试！", 2);
                return;
            }

            // 未返回 retCode 的接口（如菜单树）视为成功，仅当明确返回非 Y 时才中断
            if (result.retCode && result.retCode !== "Y") {
                Huazie.dialog.tips("warning", result.retMess || "亲，数据加载失败，请稍后重试！", 2);
                return;
            }

            var treeDataSource = new DataSourceTree({data: result[dataKey] || []});
            var tree = $("#" + opts.treeId);

            tree.ace_tree({
                dataSource: treeDataSource,
                loadingHTML: '<div class="tree-loading"><i class="fa fa-refresh blue"></i></div>',
                'open-icon': 'fa-folder-open',
                'close-icon': 'fa-folder',
                'selectable': false,
                'selected-icon': null,
                'unselected-icon': null
            }, function () {
                // 右击节点，弹出该节点可执行的功能气泡
                tree.rightClick(function (obj) {
                    var node = readNode($(obj));
                    if (!node.code) {
                        return;
                    }
                    var menuData = opts.buildMenu ? opts.buildMenu(node) : undefined;
                    if (!menuData) {
                        return;
                    }
                    Huazie.dialog.rightClickBubble(menuData, obj, function (dialog) {
                        bindMenuEvent(dialog, node, opts.onMenuEvent);
                    });
                });
                if (opts.onLoaded) {
                    opts.onLoaded(tree);
                }
            });
        });
    }

    /**
     * 表单回填：按控件 name（缺省取 id）从数据对象中取同名属性写入
     *
     * @param formId 表单容器编号（不含 #）
     * @param data   数据对象
     */
    function fillForm(formId, data) {
        if (!data) {
            return;
        }
        $("#" + formId).find("input,select,textarea").each(function () {
            var $element = $(this);
            var key = $element.attr("name") || $element.attr("id");
            if (!key || data[key] === undefined || data[key] === null) {
                return;
            }
            if ($element.is("select")) {
                $element.find("option[value='" + data[key] + "']").prop("selected", true);
            } else {
                $element.val(data[key]);
            }
        });
    }

    /**
     * 表单重置：清空输入控件，下拉框回到首项
     *
     * @param formId 表单容器编号（不含 #）
     */
    function resetForm(formId) {
        var $form = $("#" + formId);
        $form.find("input,textarea").val("");
        $form.find("select").each(function () {
            $(this).find("option:first").prop("selected", true);
        });
    }

    /**
     * 表单启用或禁用。
     * <p> 带 skip_over 样式的控件（纯展示用的占位输入框）保持原状，不参与切换，
     * 与 Huazie.form.serialize 跳过 skip_over 控件的约定保持一致。
     *
     * @param formId   表单容器编号（不含 #）
     * @param disabled true-禁用; false-启用
     */
    function setFormDisabled(formId, disabled) {
        $("#" + formId).find("input,select,textarea").each(function () {
            var $element = $(this);
            if ($element.hasClass("skip_over")) {
                return;
            }
            $element.attr("disabled", disabled);
        });
    }

    /**
     * 必填校验
     *
     * @param value 待校验的值
     * @param label 字段中文名
     * @return true-已填写; false-未填写（同时给出提示）
     */
    function checkRequired(value, label) {
        if (value === undefined || value === null || $.trim(value) === "") {
            Huazie.dialog.tips("warning", "亲，请填写" + label + "哟！", 1.5);
            return false;
        }
        return true;
    }

    /**
     * 提交表单（通用受理）。
     * <p> 校验通过后统一以 POST 方式提交，并按返回码给出提示；提交成功时回调 onSuccess。
     *
     * @param opts 配置对象
     *        url       提交地址
     *        data      提交数据
     *        onSuccess 提交成功回调 function()
     */
    function submitForm(opts) {
        Huazie.ajax.postJson(opts.url, opts.data, function (data, status) {
            var result = data;
            if (status && result.retCode === "Y") {
                Huazie.dialog.tips("info", result.retMess, 1);
                if (opts.onSuccess) {
                    opts.onSuccess();
                }
            } else if (result && result.retMess) {
                Huazie.dialog.tips("warning", result.retMess, 2);
            } else {
                Huazie.dialog.tips("warning", "亲，操作失败，请稍后重试！", 2);
            }
        });
    }

    /* ==================== 引擎一：列表树 + 表单 ==================== */

    /**
     * 增改类页面引擎（左侧列表 + 右侧表单）
     */
    var FormPage = {

        moduleType: null,

        conf: null,

        /**
         * 初始化
         *
         * @param moduleType 模块类型
         * @param conf       模块配置
         */
        init: function (moduleType, conf) {

            FormPage.moduleType = moduleType;
            FormPage.conf = conf;

            // 加载表单下拉数据源（如用户组、角色组、权限组选择）
            FormPage.initSelects(conf);

            loadTree({
                treeId: "tree_" + moduleType,
                url: ReqUrlMap.get(conf.listUrl),
                buildMenu: function (node) {

                    // 新增页左侧列表仅作参照，不提供气泡菜单
                    if (!conf.queryUrl) {
                        return undefined;
                    }

                    return [{
                        "HAS_DIVIDER": false,
                        "FUNCTION_ICON": "refresh",
                        "FUNCTION_NAME": "变更",
                        "FUNCTION_EVENT": "change",
                        "MENU_ID": node.id,
                        "MENU_CODE": node.code,
                        "MENU_NAME": node.name,
                        "MENU_LEVEL": node.level
                    }];
                },
                onMenuEvent: function (eventName, node) {
                    if (eventName === "change") {
                        FormPage.loadDetail(node, conf);
                    }
                },
                onLoaded: function () {
                    FormPage.bindEvent(conf);
                }
            });

        },

        /**
         * 加载表单下拉数据源。
         * <p> 配置形如 {@code selects: [{id: "groupId", url: "authUserGroupList", label: "用户组"}]}，
         * 数据取自各模块的列表接口（返回 treeList），首项为「请选择」。
         *
         * @param conf 模块配置
         */
        initSelects: function (conf) {
            if (!conf.selects) {
                return;
            }
            for (var i = 0; i < conf.selects.length; i++) {
                loadSelect(conf.selects[i]);
            }
        },

        /**
         * 绑定提交、重置事件
         *
         * @param conf 模块配置
         */
        bindEvent: function (conf) {

            $("#submit").off("click").on("click", function () {
                FormPage.submit(conf);
            });

            $("#reset").off("click").on("click", function () {
                FormPage.reset(conf);
            });

        },

        /**
         * 加载明细并回填表单（变更页）
         *
         * @param node 列表节点
         * @param conf 模块配置
         */
        loadDetail: function (node, conf) {

            var params = {};
            params[conf.queryKey] = node.id;

            request(ReqUrlMap.get(conf.queryUrl), params, function (result) {
                fillForm(conf.formId, result.data || {});
                // 表单启用
                setFormDisabled(conf.formId, false);

                Huazie.dialog.tips("info", "亲，【" + stripTags(node.name) + "】信息已加载，请修改后提交！", 2);
            });

        },

        /**
         * 重置表单
         *
         * @param conf 模块配置
         */
        reset: function (conf) {

            resetForm(conf.formId);

            if (conf.queryUrl) {
                // 清空后重新禁用，等待下一次选择
                setFormDisabled(conf.formId, true);
            }

        },

        /**
         * 提交表单
         *
         * @param conf 模块配置
         */
        submit: function (conf) {

            var data = Huazie.form.serialize($("#" + conf.formId));

            // 变更页需先选择待变更的数据
            if (conf.queryUrl && !data[conf.queryKey]) {
                Huazie.dialog.tips("warning", [{"MESSAGE": "亲，请先从左侧列表中选择要变更的数据哟！"}, {"MESSAGE": "提示：【右击或长按列表项】"}], 2);
                return;
            }

            if (!validateRequired(data, conf.required)) {
                return;
            }

            submitForm({
                url: ReqUrlMap.get(conf.submitUrl),
                data: data,
                onSuccess: function () {
                    FormPage.reset(conf);
                    setTimeout(function () {
                        // 重新加载左侧列表
                        FormPage.init(FormPage.moduleType, conf);
                    }, 1000);
                }
            });

        }

    };

    /* ==================== 引擎二：分步向导 ==================== */

    /**
     * 录入类页面引擎（FuelUX 分步向导）。
     * <p> 相较平铺表单，向导把「主体信息 → 属性归属 → 生效设置」拆分为若干步骤，
     * 每步只处理少量字段，并在最后一步给出填写摘要供提交前确认。
     */
    var WizardPage = {

        conf: null,

        /**
         * 初始化
         *
         * @param moduleType 模块类型
         * @param conf       模块配置
         */
        init: function (moduleType, conf) {

            WizardPage.conf = conf;

            // 下拉数据源（如用户组、角色组）
            if (conf.selects) {
                for (var i = 0; i < conf.selects.length; i++) {
                    loadSelect(conf.selects[i]);
                }
            }

            var $wizard = $("#" + conf.wizardId);

            $wizard.ace_wizard().on("change", function (e, info) {
                // 仅校验当前所在步骤的字段，未通过则阻止前进
                if (!WizardPage.validatePane(conf)) {
                    return false;
                }
                WizardPage.renderSummary(conf);
            }).on("finished", function () {
                WizardPage.submit(conf);
            });

            if (conf.onReady) {
                conf.onReady(conf);
            }

        },

        /**
         * 取当前激活的步骤内容容器
         *
         * @param conf 模块配置
         * @return jQuery 对象
         */
        currentPane: function (conf) {
            return $("#" + conf.stepContainerId).find(".step-pane.active");
        },

        /**
         * 校验当前步骤内的字段（必填 + 一致性）
         *
         * @param conf 模块配置
         * @return true-校验通过; false-未通过
         */
        validatePane: function (conf) {

            var $pane = WizardPage.currentPane(conf);

            // 必填项：仅校验出现在当前步骤中的字段
            if (conf.required) {
                for (var i = 0; i < conf.required.length; i++) {
                    var $field = $pane.find("#" + conf.required[i][0]);
                    if ($field.length > 0 && !checkRequired($field.val(), conf.required[i][1])) {
                        return false;
                    }
                }
            }

            // 一致性校验（如两次输入的密码是否一致）
            if (conf.equalTo) {
                for (var j = 0; j < conf.equalTo.length; j++) {
                    var $target = $pane.find("#" + conf.equalTo[j][0]);
                    if ($target.length > 0 && $target.val() !== $pane.find("#" + conf.equalTo[j][1]).val()) {
                        Huazie.dialog.tips("warning", conf.equalTo[j][2] || "亲，两次填写的内容不一致哟！", 2);
                        return false;
                    }
                }
            }

            return true;

        },

        /**
         * 渲染填写摘要（提交前确认用）
         *
         * @param conf 模块配置
         */
        renderSummary: function (conf) {

            if (!conf.summaryId) {
                return;
            }

            var data = Huazie.form.serialize($("#" + conf.formId));
            var fields = conf.summaryFields || [];
            var rows = [];

            for (var i = 0; i < fields.length; i++) {
                var key = fields[i][0];
                var value = data[key];
                var $field = $("#" + conf.formId).find("#" + key);

                if ($field.is("select")) {
                    // 下拉项展示选中文本（如用户组名称），避免摘要里出现裸编号
                    value = (value === undefined || value === null || value === "")
                        ? null : $.trim($field.find("option:selected").text());
                } else if (fields[i][2] && value !== undefined && value !== null && fields[i][2][value] !== undefined) {
                    // 值映射（如状态码 1 → 正常）
                    value = fields[i][2][value];
                }
                rows.push('<tr><th class="text-right" style="width:130px;">' + fields[i][1] + '</th><td>' +
                    (escapeHtml(value) || '<span class="text-muted">未填写</span>') + '</td></tr>');
            }

            $("#" + conf.summaryId).html('<table class="table table-bordered table-striped">' + rows.join("") + '</table>');

        },

        /**
         * 提交
         *
         * @param conf 模块配置
         */
        submit: function (conf) {

            var data = Huazie.form.serialize($("#" + conf.formId));

            if (!validateRequired(data, conf.required)) {
                return;
            }

            if (conf.beforeSubmit) {
                data = conf.beforeSubmit(data);
            }

            submitForm({
                url: ReqUrlMap.get(conf.submitUrl),
                data: data,
                onSuccess: function () {
                    WizardPage.reset(conf);
                }
            });

        },

        /**
         * 重置表单并回到第一步
         *
         * @param conf 模块配置
         */
        reset: function (conf) {

            resetForm(conf.formId);

            $("#" + conf.stepContainerId).find(".step-pane").removeClass("active").first().addClass("active");
            $("#" + conf.wizardId).find("li").removeClass("active complete").first().addClass("active");

            if (conf.summaryId) {
                $("#" + conf.summaryId).html('<div class="text-muted">亲，请在左侧各步骤中填写信息哟！</div>');
            }

        }

    };

    /* ==================== 引擎三：明细表格 + 编辑面板 ==================== */

    /**
     * 列表类页面引擎（jqGrid 明细表格 + 右侧编辑面板）。
     * <p> 相较仅展示名称的列表树，表格可直接呈现状态、归属、有效期等字段，
     * 并支持按列即时筛选与分页；选中行即加载明细到右侧编辑面板。
     */
    var GridPage = {

        conf: null,

        /** 是否正处于「自动选中首行」阶段（此阶段静默加载，不弹提示） */
        autoSelecting: false,

        /** 各表格的完整数据行，key 为表格编号（窄屏关键字搜索在本地过滤它） */
        allRows: {},

        /** 是否已绑定过视口变化监听，避免重复绑定 */
        viewportBound: false,

        /**
         * 初始化
         *
         * @param moduleType 模块类型
         * @param conf       模块配置
         */
        init: function (moduleType, conf) {

            GridPage.conf = conf;

            if (conf.selects) {
                for (var i = 0; i < conf.selects.length; i++) {
                    loadSelect(conf.selects[i]);
                }
            }

            // 编辑面板默认禁用，数据加载完成后自动选中首行启用
            setFormDisabled(conf.formId, true);
            GridPage.toggleTip(conf, 0);

            GridPage.bindFormEvent(conf);
            GridPage.load(conf);

        },

        /**
         * 切换「空数据」提示的显隐。
         * <p> 表格有数据时无需再提示用户「先去选一行」，避免进入页面即被提示打扰。
         *
         * @param conf  模块配置
         * @param count 表格行数
         */
        toggleTip: function (conf, count) {

            if (!conf.tipId) {
                return;
            }

            if (count > 0) {
                $("#" + conf.tipId).hide();
            } else {
                $("#" + conf.tipId).show();
            }

        },

        /**
         * 绑定编辑面板的提交与重置
         *
         * @param conf 模块配置
         */
        bindFormEvent: function (conf) {

            $("#" + conf.submitId).off("click").on("click", function () {
                GridPage.submit(conf);
            });

            $("#" + conf.resetId).off("click").on("click", function () {
                resetForm(conf.formId);
                setFormDisabled(conf.formId, true);
            });

        },

        /**
         * 加载明细数据并渲染表格
         *
         * @param conf 模块配置
         */
        load: function (conf) {

            request(ReqUrlMap.get(conf.dataUrl), {}, function (result) {
                GridPage.renderSummary(conf, result.summary || {});
                GridPage.renderGrid(conf, result.rows || []);
            });

        },

        /**
         * 渲染顶部统计卡片
         *
         * @param conf    模块配置
         * @param summary 汇总统计
         */
        renderSummary: function (conf, summary) {

            var map = conf.summaryIds;
            if (!map) {
                return;
            }

            for (var key in map) {
                if (map.hasOwnProperty(key)) {
                    $("#" + map[key]).text(summary[key] === undefined ? 0 : summary[key]);
                }
            }

        },

        /**
         * 构造 jqGrid 列定义。
         * <p> 列上可通过 formatter 指定渲染方式：state-状态徽章；text-转义文本；action-操作链接。
         *
         * @param conf 模块配置
         * @return {colNames: [], colModel: []}
         */
        buildColModel: function (conf) {

            var colNames = [];
            var colModel = [];

            for (var i = 0; i < conf.columns.length; i++) {

                var col = conf.columns[i];
                colNames.push(col.label);

                var model = {
                    name: col.name,
                    index: col.name,
                    width: col.width || 100,
                    sortable: col.sortable !== false,
                    align: col.align || "left",
                    // mobile 为 false 的列属次要信息：窄屏隐藏，避免整行被压成一团（真机实测 375px 下 10 列仅 318px）
                    hidden: col.hidden === true || (col.mobile === false && isMobile()),
                    // search 为 false 的列（如主键、状态徽章）不生成筛选控件，避免整行出现无意义空档
                    search: col.search !== false
                };

                // 数值列按数值排序，否则 jqGrid 缺省的 "text" 会把 10000 排在 1001 之前
                var sorttype = col.sorttype || (NUMERIC_COLUMN.test(col.name) ? "int" : "");

                if (sorttype) {
                    model.sorttype = sorttype;
                }

                // 取值有限的列改用下拉筛选（如状态），比空白输入框语义清晰，也补齐了原本的空档
                if (col.stype === "select") {
                    model.stype = "select";
                    model.search = true;
                    model.searchoptions = {sopt: ["eq"], value: col.options || ""};
                }

                if (col.formatter === "state") {
                    model.formatter = function (cellvalue) {
                        return stateBadge(cellvalue);
                    };
                } else if (col.formatter === "action") {
                    model.sortable = false;
                    model.resize = false;
                    model.search = false;
                    model.formatter = function (cellvalue, options, rowObject) {
                        return '<a href="javascript:;" class="grid-action" data-row-id="' + rowObject[conf.rowKey] +
                            '"><i class="fa fa-pencil blue"></i> 编辑</a>';
                    };
                } else if (col.formatter === "text") {
                    model.formatter = function (cellvalue) {
                        return escapeHtml(cellvalue);
                    };
                }

                colModel.push(model);

            }

            return {colNames: colNames, colModel: colModel};

        },

        /**
         * 统一修饰表头筛选行。
         * <p> jqGrid 自带的筛选行有两个观感问题：控件比列窄一圈、且无任何文字提示，
         * 整行看起来像"没对齐的空白框"。这里按列名补占位提示、去掉 th 内边距让控件撑满整列，
         * 并加一条淡色下边线，使其成为一条明确的独立筛选带。
         *
         * @param $grid 表格 jQuery 对象
         */
        decorateFilterRow: function ($grid) {

            var $box = $grid.closest(".ui-jqgrid");
            var $row = $box.find("tr.ui-search-toolbar");

            if ($row.length === 0) {
                return;
            }

            // 表头列名，用于生成占位提示（两行的 th 数量、顺序一一对应）
            var labels = [];
            $box.find("tr.ui-jqgrid-labels").children("th").each(function () {
                labels.push(stripTags($(this).text()).replace(/\s/g, ""));
            });

            $row.children("th").each(function (index) {

                var $cell = $(this);
                // jqGrid 会把筛选控件再包一层，这里按后代查找而非直接子节点
                var $item = $cell.find("input, select").first();

                // 无筛选控件的列（主键、操作列）只统一内边距，保持与其它列同基线
                $cell.css({padding: "3px 0", "border-bottom": "1px solid #e3e9ef"});

                if ($item.length === 0 || !labels[index]) {
                    return;
                }

                // 空白框看不出筛哪一列，用列名作占位提示
                if ($item.is("input")) {
                    $item.attr("placeholder", labels[index]);
                }

                // 下拉默认不能锁定首个选项，补一个"全部"作为未筛选状态
                if ($item.is("select") && $item.find("option[value='']").length === 0) {
                    $item.prepend('<option value="">全部</option>').val("");
                }

                $item.css({
                    width: "100%",
                    height: "24px",
                    margin: "0",
                    padding: $item.is("select") ? "0 2px" : "0 6px",
                    "box-sizing": "border-box",
                    "border-radius": "0",
                    "font-size": "12px"
                });

            });

        },

        /**
         * 渲染 jqGrid 表格
         *
         * @param conf 模块配置
         * @param rows 明细行集合
         */
        renderGrid: function (conf, rows) {

            // jqGrid 以行的 id 字段作为行标识，这里复用业务主键
            for (var i = 0; i < rows.length; i++) {
                rows[i].id = rows[i][conf.rowKey];
            }

            // 留底全量数据，供窄屏关键字搜索在本地过滤
            GridPage.allRows[conf.gridId] = rows;

            var cm = GridPage.buildColModel(conf);
            var $grid = $("#" + conf.gridId);

            $grid.jqGrid({
                data: rows,
                datatype: "local",
                colNames: cm.colNames,
                colModel: cm.colModel,
                height: conf.height || 300,
                rowNum: conf.rowNum || 10,
                rowList: [10, 20, 50],
                pager: "#" + conf.pagerId,
                viewrecords: true,
                sortname: conf.columns[0].name,
                sortorder: "asc",
                autowidth: true,
                altRows: true,
                // 本地搜索忽略大小写：列筛选行与窄屏关键字搜索都走 jqGrid 本地搜索，
                // 缺此参数时 "huazie" 匹配不到 "Huazie"（实测筛成 0 行）
                ignoreCase: true,
                // 表格首列已是业务编号，再叠加行序号会造成「1 | 1000」双编号，默认不再显示
                rownumbers: conf.rownumbers === true,
                emptyrecords: "暂无数据",
                // caption 由外层 Ace 卡片头承担，此处不再重复渲染标题条
                caption: conf.caption || "",
                onSelectRow: function (rowId) {
                    // 自动选中首行时静默加载明细，避免进入页面就弹提示
                    GridPage.loadDetail(conf, rowId, GridPage.autoSelecting);
                },
                loadComplete: function () {
                    // 表格每次重绘后重新绑定操作列的点击
                    GridPage.bindRowAction(conf, $grid);
                }
            });

            // 顶部按列即时筛选
            // ⚠️ jqGrid 的两种触发方式互斥（见 filterToolbar 源码）：
            //    autosearch=true + searchOnEnter=true  → 只在回车时筛选；
            //    autosearch=true + searchOnEnter=false → 输入停顿 500ms 自动筛选。
            //    这里要的是「即时筛选」，故必须给 false。
            // ⚠️ defaultSearch 必须给运算符字符串：给 true 时 jqGrid 会把 sopt[0] 置为 true，
            //    运算符在合法列表中找不到，任何输入都会筛出 0 行（文本筛选完全失效）
            $grid.jqGrid("filterToolbar", {
                defaultSearch: "cn",
                stringResult: true,
                searchOnEnter: false,
                autosearch: true,
                ignoreCase: true
            });

            // 筛选控件与列严格对齐，并补上列名占位提示
            GridPage.decorateFilterRow($grid);

            // 分页栏仅保留刷新
            $grid.jqGrid("navGrid", "#" + conf.pagerId, {
                edit: false, add: false, del: false, search: false, refresh: true, view: false
            });

            // 有数据时默认选中首行：进入页面即为可用状态，不必先手动点行
            GridPage.toggleTip(conf, rows.length);

            if (rows.length > 0) {
                GridPage.autoSelecting = true;
                $grid.jqGrid("setSelection", rows[0].id, true);
                GridPage.autoSelecting = false;
            }

            // 窄屏适配：裁剪次要列 + 补一个替代列筛选行的关键字搜索框
            GridPage.applyMobileColumns(conf);
            GridPage.attachMobileSearch(conf);
            GridPage.bindViewport();

        },

        /**
         * 按当前视口裁剪「仅在窄屏隐藏」的列。
         * <p> 手机视口下表格宽度被压缩，列数过多会糊成一团（实测 375px 仅 318px 可容纳 10 列），
         * 故列上可标 mobile:false 声明其为次要信息；桌面端恢复显示。
         * <p> 配置里主动 hidden:true 的列不参与切换，避免被误显示。
         *
         * @param conf 模块配置
         */
        applyMobileColumns: function (conf) {

            var mobile = isMobile();

            for (var i = 0; i < conf.columns.length; i++) {

                var col = conf.columns[i];

                if (col.mobile === false && col.hidden !== true) {
                    $("#" + conf.gridId).jqGrid(mobile ? "hideCol" : "showCol", col.name);
                }

            }

        },

        /**
         * 窄屏关键字搜索框。
         * <p> 列筛选行在窄屏被 CSS 隐藏（控件仅 26px 宽，无法输入），改由本搜索框对全部列
         * 做包含匹配；桌面端由 CSS 隐藏，与列筛选行互不干扰。
         * <p> ⚠️ 监听 input 而非 keyup：手机输入法（中文 IME）组合输入期间不会派发带最终取值的
         * keyup，只有 input 事件携带合成后的文本；且部分输入路径下 keyup 早于 value 写入，
         * 处理器读到空串会把 search 参数置回 false，表格随即恢复全量——表现为「搜索框有值但
         * 表格不过滤」。input 同时覆盖粘贴、清空、语音输入等无按键场景。
         *
         * @param conf 模块配置
         */
        attachMobileSearch: function (conf) {

            if (conf.mobileSearch === false) {
                return;
            }

            var boxId = conf.gridId + "_msearch";

            if ($("#" + boxId).length > 0) {
                return;
            }

            $("#gbox_" + conf.gridId).before(
                '<div class="grid-mobile-search">' +
                '<div class="input-icon">' +
                '<i class="fa fa-search grey"></i>' +
                '<input type="text" class="form-control input-sm" id="' + boxId + '" placeholder="输入关键字搜索" />' +
                '</div>' +
                '</div>'
            );

            // 去抖：IME 组合期间 input 会连发，避免逐字重绘表格并反复重载明细
            var timer = null;

            $("#" + boxId).on("input", function () {

                var keyword = $.trim($(this).val());

                if (timer) {
                    clearTimeout(timer);
                }

                timer = setTimeout(function () {
                    GridPage.applySearch(conf, $("#" + conf.gridId), keyword);
                }, 200);

            });

            // 输入框失焦时兜底执行一次，覆盖「输入后未停顿即点走」的场景
            $("#" + boxId).on("change", function () {
                if (timer) {
                    clearTimeout(timer);
                }
                GridPage.applySearch(conf, $("#" + conf.gridId), $.trim($(this).val()));
            });

        },

        /**
         * 按关键字对表格做本地过滤（跨全部列，包含匹配）。
         * <p> 交给 jqGrid 的本地搜索完成过滤：直接 setGridParam({data: 过滤后数据}) 时
         * 表格渲染与内部索引不同步（实测行数不刷新），故 data 恒为全量，过滤条件走 filters
         * （OR + cn 覆盖全部列）；大小写敏感性由表格的 ignoreCase 参数决定。
         *
         * @param conf    模块配置
         * @param $grid   表格 jQuery 对象
         * @param keyword 关键字（已 trim，未做大小写转换）
         */
        applySearch: function (conf, $grid, keyword) {

            var allRows = GridPage.allRows[conf.gridId] || [];
            var matched = [];

            if (keyword === "") {
                matched = allRows;
            } else {
                var lower = keyword.toLowerCase();
                matched = $.grep(allRows, function (row) {
                    for (var i = 0; i < conf.columns.length; i++) {
                        var value = row[conf.columns[i].name];
                        if (value !== undefined && value !== null &&
                            String(value).toLowerCase().indexOf(lower) >= 0) {
                            return true;
                        }
                    }
                    return false;
                });
            }

            $grid.jqGrid("setGridParam", {
                data: allRows,
                page: 1,
                search: keyword !== "",
                postData: keyword === "" ? {} : {
                    filters: JSON.stringify({
                        groupOp: "OR",
                        rules: $.map(conf.columns, function (col) {
                            return {field: col.name, op: "cn", data: keyword};
                        })
                    })
                }
            }).trigger("reloadGrid", [{page: 1}]);

            // 过滤结果默认选中首行，右侧明细跟随切换
            if (matched.length > 0) {
                GridPage.autoSelecting = true;
                $grid.jqGrid("setSelection", matched[0].id, true);
                GridPage.autoSelecting = false;
            }

        },

        /**
         * 视口变化时重新裁剪表格列（手机横竖屏切换、桌面拖拽窗口）
         */
        bindViewport: function () {

            if (GridPage.viewportBound) {
                return;
            }

            GridPage.viewportBound = true;

            onViewportChange(function () {
                if (GridPage.conf) {
                    GridPage.applyMobileColumns(GridPage.conf);
                }
            });

        },

        /**
         * 绑定操作列「编辑」的点击
         *
         * @param conf  模块配置
         * @param $grid 表格 jQuery 对象
         */
        bindRowAction: function (conf, $grid) {

            $grid.find(".grid-action").off("click").on("click", function (e) {
                e.stopPropagation();
                var rowId = $(this).attr("data-row-id");
                $grid.jqGrid("setSelection", rowId, true);
            });

        },

        /**
         * 加载选中行的明细并回填编辑面板
         *
         * @param conf   模块配置
         * @param rowId  行标识（业务主键）
         * @param silent 是否静默加载（自动选中首行时不弹提示）
         */
        loadDetail: function (conf, rowId, silent) {

            var params = {};
            params[conf.queryKey] = rowId;

            request(ReqUrlMap.get(conf.queryUrl), params, function (result) {

                var data = result.data || {};

                fillForm(conf.formId, data);
                setFormDisabled(conf.formId, false);

                // 页面自定义回填（如只读的账号、下拉回显）
                if (conf.onDetailLoaded) {
                    conf.onDetailLoaded(data);
                }

                if (!silent) {
                    Huazie.dialog.tips("info", "亲，明细已加载，请修改后提交！", 1.5);
                }

            });

        },

        /**
         * 提交编辑面板
         *
         * @param conf 模块配置
         */
        submit: function (conf) {

            var data = Huazie.form.serialize($("#" + conf.formId));

            if (!data[conf.queryKey]) {
                Huazie.dialog.tips("warning", "亲，请先从表格中选择要变更的数据哟！", 2);
                return;
            }

            if (!validateRequired(data, conf.required)) {
                return;
            }

            submitForm({
                url: ReqUrlMap.get(conf.submitUrl),
                data: data,
                onSuccess: function () {
                    GridPage.reload(conf);
                }
            });

        },

        /**
         * 重新加载表格与编辑面板
         *
         * @param conf 模块配置
         */
        reload: function (conf) {

            var $grid = $("#" + conf.gridId);

            // 先卸载再重建，避免 jqGrid 重复初始化
            try {
                $grid.jqGrid("GridUnload");
            } catch (e) {
                $grid.empty();
            }

            resetForm(conf.formId);
            setFormDisabled(conf.formId, true);

            GridPage.load(conf);

        }

    };

    /* ==================== 引擎四：主体授权（标签页 + 穿梭） ==================== */

    /**
     * 授权类页面引擎（主体表格 + 标签页分维度 + 左右穿梭）。
     * <p> 相较把「新增」与「已授权」混在一个勾选列表里，这里按关联类型分标签页，
     * 每个标签页内以左右穿梭的方式选择，并支持关键字过滤、全选与单条撤回，
     * 标签页上的徽章实时反映该类授权数量。
     */
    var AuthPage = {

        conf: null,

        ownerId: null,

        ownerName: null,

        candidates: null,

        selected: null,

        /**
         * 初始化
         *
         * @param moduleType 模块类型
         * @param conf       模块配置
         */
        init: function (moduleType, conf) {

            AuthPage.conf = conf;
            AuthPage.ownerId = null;
            AuthPage.ownerName = null;
            AuthPage.candidates = {};
            AuthPage.selected = {};

            AuthPage.renderTabs(conf);
            AuthPage.bindTabs(conf);
            AuthPage.loadOwners(conf);

            // 未选择授权主体前，授权面板整体隐藏
            $("#" + conf.authPanelId).hide();

        },

        /**
         * 渲染标签页骨架（每个关联类型一个标签页 + 一个穿梭面板）
         *
         * @param conf 模块配置
         */
        renderTabs: function (conf) {

            var nav = [];
            var panes = [];

            for (var i = 0; i < conf.relTypes.length; i++) {
                var type = conf.relTypes[i][0];
                var label = conf.relTypes[i][1];
                nav.push('<li' + (i === 0 ? ' class="active"' : '') + '>' +
                    '<a href="#pane_' + type + '" data-toggle="tab" data-rel-type="' + type + '">' +
                    label + ' <span class="badge badge-info" id="badge_' + type + '">0</span></a></li>');
                panes.push('<div class="tab-pane' + (i === 0 ? ' active' : '') + '" id="pane_' + type + '">' +
                    AuthPage.paneHtml(type) + '</div>');
            }

            $("#" + conf.tabsId + " > ul").html(nav.join(""));
            $("#" + conf.tabsId + " > .tab-content").html(panes.join(""));

        },

        /**
         * 构造单个标签页的穿梭面板
         *
         * @param type 关联类型
         * @return 面板 HTML
         */
        paneHtml: function (type) {

            return '<div class="shuttle-toolbar">' +
                '<input type="text" class="form-control input-sm shuttle-search" id="search_' + type + '" placeholder="输入关键字过滤可授权数据..." />' +
                '<div class="btn-group btn-group-xs">' +
                '<button type="button" class="btn btn-default shuttle-select-all" data-rel-type="' + type + '"><i class="fa fa-check-square-o"></i> 全选</button>' +
                '<button type="button" class="btn btn-default shuttle-clear" data-rel-type="' + type + '"><i class="fa fa-square-o"></i> 清空本次</button>' +
                '</div>' +
                '</div>' +
                '<div class="row">' +
                '<div class="col-sm-6">' +
                '<div class="shuttle-panel-title"><i class="fa fa-list-alt blue"></i> 可授权数据</div>' +
                '<div class="shuttle-list" id="candidate_' + type + '"></div>' +
                '</div>' +
                '<div class="col-sm-6">' +
                '<div class="shuttle-panel-title"><i class="fa fa-check-square-o green"></i> 授权清单</div>' +
                '<div class="shuttle-selected-block"><span class="blue">已授权（不可在此撤销）：</span>' +
                '<div class="selected-list" id="exist_' + type + '"></div></div>' +
                '<div class="space-4"></div>' +
                '<div class="shuttle-selected-block"><span class="green">本次待新增：</span>' +
                '<div class="selected-list" id="new_' + type + '"></div></div>' +
                '</div>' +
                '</div>' +
                '<div class="space-4"></div>' +
                '<div class="row-fluid wizard-actions shuttle-actions">' +
                '<button type="button" class="btn btn-info shuttle-save" data-rel-type="' + type + '">' +
                '<i class="fa fa-check bigger-110"></i> 保存本类授权</button>' +
                '<button type="button" class="btn btn-inverse shuttle-reset" data-rel-type="' + type + '">' +
                '<i class="fa fa-undo bigger-110"></i> 清空本次选择</button>' +
                '</div>';

        },

        /**
         * 绑定标签页与穿梭框的交互
         *
         * @param conf 模块配置
         */
        bindTabs: function (conf) {

            // 切换标签页时按需加载该维度的候选数据（首次进入才请求）
            $("#" + conf.tabsId + " a[data-toggle=tab]").off("shown.bs.tab").on("shown.bs.tab", function () {
                AuthPage.loadTab(conf, $(this).attr("data-rel-type"));
            });

            // 关键字过滤：同样监听 input 而非 keyup，否则手机输入法（中文 IME）不触发
            $("#" + conf.tabsId).on("input", ".shuttle-search", function () {
                AuthPage.filterCandidates($(this));
            });

            // 失焦兜底，覆盖「输入后未停顿即移开焦点」
            $("#" + conf.tabsId).on("change", ".shuttle-search", function () {
                AuthPage.filterCandidates($(this));
            });

            // 全选（仅作用于当前可见且未禁用的项）
            $("#" + conf.tabsId).on("click", ".shuttle-select-all", function () {
                AuthPage.selectAll($(this).attr("data-rel-type"), true);
            });

            // 清空本次未提交的勾选
            $("#" + conf.tabsId).on("click", ".shuttle-clear, .shuttle-reset", function () {
                AuthPage.selectAll($(this).attr("data-rel-type"), false);
            });

            // 候选变化时同步授权清单
            $("#" + conf.tabsId).on("change", ".shuttle-checkbox", function () {
                AuthPage.renderSelected($(this).attr("data-rel-type"));
            });

            // 提交某类授权
            $("#" + conf.tabsId).on("click", ".shuttle-save", function () {
                AuthPage.submit(conf, $(this).attr("data-rel-type"));
            });

        },

        /**
         * 加载授权主体列表（表格，支持按列筛选）
         *
         * @param conf 模块配置
         */
        loadOwners: function (conf) {

            request(ReqUrlMap.get(conf.ownerListUrl), {}, function (result) {

                var rows = result.treeList || result.rows || [];
                var $grid = $("#" + conf.ownerGridId);

                for (var i = 0; i < rows.length; i++) {
                    rows[i].id = rows[i].id;
                    rows[i].displayName = stripTags(rows[i].name);
                }

                // 留底全量数据，供窄屏关键字搜索在本地过滤
                GridPage.allRows[conf.ownerGridId] = rows;

                $grid.jqGrid({
                    data: rows,
                    datatype: "local",
                    colNames: [conf.ownerLabel + "编号", conf.ownerLabel + "名称"],
                    colModel: [
                        {name: "id", index: "id", width: 90, sorttype: "int"},
                        {name: "displayName", index: "displayName", width: 260}
                    ],
                    height: conf.ownerHeight || 180,
                    rowNum: conf.ownerRowNum || 8,
                    rowList: [8, 16, 32],
                    pager: "#" + conf.ownerPagerId,
                    viewrecords: true,
                    altRows: true,
                    autowidth: true,
                    // 主体表同样走本地搜索，忽略大小写（与明细表一致）
                    ignoreCase: true,
                    // 已有「主体编号」列，不再重复渲染行序号；标题由外层卡片头承担
                    rownumbers: false,
                    onSelectRow: function (rowId) {
                        AuthPage.selectOwner(conf, rowId, $(this).jqGrid("getCell", rowId, "displayName"));
                    }
                });

                $grid.jqGrid("filterToolbar", {
                    defaultSearch: "cn",
                    stringResult: true,
                    searchOnEnter: false,
                    autosearch: true,
                    ignoreCase: true
                });

                GridPage.decorateFilterRow($grid);

                $grid.jqGrid("navGrid", "#" + conf.ownerPagerId, {
                    edit: false, add: false, del: false, search: false, refresh: true, view: false
                });

                // 窄屏：主体表同样补关键字搜索（列筛选行在窄屏被 CSS 隐藏）
                GridPage.attachMobileSearch({
                    gridId: conf.ownerGridId,
                    columns: [{name: "id"}, {name: "displayName"}]
                });

                // 默认选中首个授权主体，进入页面即可看到授权维度，无需先手动点行
                if (rows.length > 0) {
                    $grid.jqGrid("setSelection", rows[0].id, true);
                }

            });

        },

        /**
         * 选中授权主体
         *
         * @param conf  模块配置
         * @param id    主体编号
         * @param name  主体名称
         */
        selectOwner: function (conf, id, name) {

            AuthPage.ownerId = id;
            AuthPage.ownerName = name;

            // 主体已切换，候选与已授权的缓存全部失效
            AuthPage.candidates = {};
            AuthPage.selected = {};

            $("#" + conf.ownerLabelId).text(name + "（编号：" + id + "）");
            $("#" + conf.authPanelId).show();

            AuthPage.loadTab(conf, AuthPage.activeRelType(conf));

        },

        /**
         * 取当前激活的关联类型
         *
         * @param conf 模块配置
         * @return 关联类型
         */
        activeRelType: function (conf) {
            return $("#" + conf.tabsId + " li.active a").attr("data-rel-type");
        },

        /**
         * 按关联类型加载候选与已授权数据
         *
         * @param conf    模块配置
         * @param relType 关联类型
         */
        loadTab: function (conf, relType) {

            if (!relType || !AuthPage.ownerId || AuthPage.candidates[relType]) {
                return;
            }

            var params = {relType: relType};
            params[conf.ownerKey] = AuthPage.ownerId;

            request(ReqUrlMap.get(conf.authUrl), params, function (result) {

                AuthPage.candidates[relType] = result.candidates || [];
                AuthPage.selected[relType] = result.selected || [];

                AuthPage.renderCandidates(relType);
                AuthPage.renderBadges(conf, result.relTypeCounts || {});

            });

        },

        /**
         * 渲染各标签页上的授权数量徽章
         *
         * @param conf  模块配置
         * @param counts 各关联类型的授权数量
         */
        renderBadges: function (conf, counts) {

            for (var i = 0; i < conf.relTypes.length; i++) {
                var type = conf.relTypes[i][0];
                var count = counts[type];
                // 已加载过的维度以本地数据为准，避免徽章与清单不一致
                if (AuthPage.selected[type]) {
                    count = AuthPage.selected[type].length;
                }
                $("#badge_" + type).text(count === undefined ? 0 : count);
            }

        },

        /**
         * 渲染候选列表（已授权项默认勾选且不可取消）
         *
         * @param relType 关联类型
         */
        renderCandidates: function (relType) {

            var candidates = AuthPage.candidates[relType] || [];
            var selected = AuthPage.selected[relType] || [];

            var selectedArray = [];
            for (var i = 0; i < selected.length; i++) {
                selectedArray.push(String(selected[i]));
            }

            var html = [];
            for (var j = 0; j < candidates.length; j++) {
                var item = candidates[j];
                var checked = $.inArray(String(item.id), selectedArray) >= 0;
                html.push('<label class="shuttle-item" data-name="' + escapeHtml(stripTags(item.name)) + '">' +
                    '<input type="checkbox" class="shuttle-checkbox" data-rel-type="' + relType + '" value="' + item.id + '"' +
                    (checked ? ' checked="checked" disabled="disabled"' : '') + ' />' +
                    '<span class="lbl">' + escapeHtml(stripTags(item.name)) + '</span>' +
                    (checked ? '<span class="label label-info pull-right">已授权</span>' : '') +
                    '</label>');
            }

            if (html.length === 0) {
                html.push('<div class="text-muted">亲，暂无可授权的数据哟！</div>');
            }

            $("#candidate_" + relType).html(html.join(""));

            AuthPage.renderSelected(relType);

        },

        /**
         * 渲染授权清单（区分「已授权」与「本次待新增」）
         *
         * @param relType 关联类型
         */
        renderSelected: function (relType) {

            var existHtml = [];
            var newHtml = [];

            $("#candidate_" + relType).find(".shuttle-checkbox").each(function () {
                var $checkbox = $(this);
                var name = escapeHtml($checkbox.closest(".shuttle-item").attr("data-name"));

                if ($checkbox.prop("disabled")) {
                    // 已授权的数据
                    existHtml.push('<span class="label label-info">' + name + '</span>');
                } else if ($checkbox.prop("checked")) {
                    // 本次待新增的数据，点击可取消
                    newHtml.push('<span class="label label-success shuttle-remove" data-rel-type="' + relType +
                        '" data-value="' + $checkbox.val() + '" title="点击取消选择">' + name + '</span>');
                }
            });

            $("#exist_" + relType).html(existHtml.length > 0 ? existHtml.join("&nbsp;") : '<span class="text-muted">暂无</span>');
            $("#new_" + relType).html(newHtml.length > 0 ? newHtml.join("&nbsp;") : '<span class="text-muted">暂无</span>');

            // 点击清单中的标签即取消该项勾选
            $("#new_" + relType).find(".shuttle-remove").off("click").on("click", function () {
                var type = $(this).attr("data-rel-type");
                var value = $(this).attr("data-value");
                $("#candidate_" + type).find(".shuttle-checkbox[value='" + value + "']").prop("checked", false);
                AuthPage.renderSelected(type);
            });

        },

        /**
         * 候选列表关键字过滤
         *
         * @param $input 搜索输入框
         */
        filterCandidates: function ($input) {

            var keyword = $.trim($input.val()).toLowerCase();
            var relType = $input.attr("id").replace("search_", "");

            $("#candidate_" + relType).find(".shuttle-item").each(function () {
                var $item = $(this);
                // 忽略大小写匹配，与表格本地搜索（ignoreCase）保持一致
                var name = String($item.attr("data-name") || "").toLowerCase();
                $item.toggle(keyword === "" || name.indexOf(keyword) >= 0);
            });

        },

        /**
         * 全选或清空当前可见的未授权项
         *
         * @param relType 关联类型
         * @param checked true-全选; false-清空
         */
        selectAll: function (relType, checked) {

            $("#candidate_" + relType).find(".shuttle-item:visible").find(".shuttle-checkbox:not(:disabled)")
                .prop("checked", checked);

            AuthPage.renderSelected(relType);

        },

        /**
         * 提交某类授权（仅提交本次新增的关联）
         *
         * @param conf    模块配置
         * @param relType 关联类型
         */
        submit: function (conf, relType) {

            if (!AuthPage.ownerId) {
                Huazie.dialog.tips("warning", "亲，请先在上方列表中选择要授权的" + conf.ownerLabel + "哟！", 2);
                return;
            }

            var relIds = [];
            $("#candidate_" + relType).find(".shuttle-checkbox:not(:disabled)").each(function () {
                if ($(this).prop("checked")) {
                    relIds.push($(this).val());
                }
            });

            if (relIds.length === 0) {
                Huazie.dialog.tips("warning", "亲，请先从可授权数据中选择要授权的数据哟！", 2);
                return;
            }

            submitForm({
                url: ReqUrlMap.get(conf.submitUrl),
                data: {
                    ownerId: AuthPage.ownerId,
                    relType: relType,
                    // 逗号拼接,由 Spring 的 String->List<Long> 转换器解析
                    // (jQuery 默认会把数组序列化为 relIds[]=1&relIds[]=2,Spring 绑定时空 [] 会抛 NumberFormatException)
                    relIds: relIds.join(",")
                },
                onSuccess: function () {
                    // 已授权缓存失效，重新拉取该维度数据
                    AuthPage.candidates[relType] = null;
                    setTimeout(function () {
                        AuthPage.loadTab(conf, relType);
                    }, 800);
                }
            });

        }

    };

    /**
     * 模块页面初始化入口。
     * <p> 各业务模块脚本只需注册 URL 与配置，页面引擎集中在此处，
     * 避免用户、角色、权限三个模块各写一份增改/授权逻辑。
     *
     * @param moduleType 模块类型
     * @param conf       模块配置（authConf-授权类；gridConf-表格类；wizardConf-向导类；formConf-增改类）
     */
    function initModule(moduleType, conf) {

        if (conf.authConf && conf.authConf[moduleType]) {
            AuthPage.init(moduleType, conf.authConf[moduleType]);
            return;
        }

        if (conf.gridConf && conf.gridConf[moduleType]) {
            GridPage.init(moduleType, conf.gridConf[moduleType]);
            return;
        }

        if (conf.wizardConf && conf.wizardConf[moduleType]) {
            WizardPage.init(moduleType, conf.wizardConf[moduleType]);
            return;
        }

        if (conf.formConf && conf.formConf[moduleType]) {
            FormPage.init(moduleType, conf.formConf[moduleType]);
        }

    }

    exports.loadTree = loadTree;
    exports.readNode = readNode;
    exports.fillForm = fillForm;
    exports.resetForm = resetForm;
    exports.setFormDisabled = setFormDisabled;
    exports.checkRequired = checkRequired;
    exports.submitForm = submitForm;
    exports.initModule = initModule;

});
