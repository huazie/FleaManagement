-- ============================================================
-- FleaManagement - Ace 组件菜单 + 权限初始化脚本
-- 目标库：fleaauth
-- 说明：
--   1. 本项目前端基于 Ace Admin 模板，存在一批组件展示页面（ace/*.html），
--      但 fleaauth.flea_menu 中并未配置对应菜单，本脚本补齐这些菜单及其权限。
--   2. 菜单层级：1 级「Ace组件」 -> 2 级分类 -> 3 级具体页面
--      （其中「Ace控制台」置于「常规组件」之前）。
--      menu_id 按菜单树先序顺序连续编号（1042~1073），与侧边栏展示顺序一致。
--   3. 每个菜单都需在 flea_function_attr_menu 中登记一条
--      （function_id = menu_id，function_type='MENU'，attr_code='SYSTEM_IN_USE'，
--       attr_value='1001' 表示所属系统《Flea管家》），与既有 42 条菜单保持一致。
--   4. 每个菜单还需生成 1 条「访问《菜单名》菜单」权限（flea_privilege），
--      并用 flea_privilege_rel、flea_privilege_group_rel 分别关联菜单与权限组。
--      权限统一归属权限组 1002《Ace管理》菜单访问（该组已绑定【超级管理员】角色），
--      规则与既有 42 条菜单（归属权限组 1000《菜单访问》）完全一致。
--   5. 末尾更新 flea_id_generator，避免后续框架自增 ID 与已插入数据冲突。
--   6. 字符集：请务必以 utf8 连接执行（mysql ... --default-character-set=utf8）。
--   7. 一次性种子脚本，重复执行前请先清理 menu_id 1042~1073、
--      privilege_id 1051~1082 以及对应的关联数据。
--   8. menu_view 目录约定（3 级页面按分组归入 ace/ 子目录，与菜单分组一一对应）：
--        ace/general/  常规组件（画廊·组件·排版·栅格·日历）
--        ace/table/    表格      ace/form/   表单      ace/ui/     UI元素
--        ace/more/     综合示例  ace/other/  其他（常见问题·404·500）
--      「Ace控制台」为 2 级单页，位于 ace/aceConsole.html。
--      注意：页面内相对路径深度随目录层级变化（根级页面用 ../resources，
--      子目录页面用 ../../resources），移动页面时必须同步改写。
-- ============================================================

SET NAMES utf8;

-- 整个脚本在单个事务内执行：任一步失败自动回滚，避免只写入一半
START TRANSACTION;

-- ------------------------------------------------------------
-- 1) 插入菜单（flea_menu）
--    列顺序：menu_id, menu_code, menu_name, menu_icon, menu_sort,
--            menu_view, menu_level, menu_state, parent_id,
--            create_date, done_date, effective_date, expiry_date, remarks
-- ------------------------------------------------------------
INSERT INTO fleaauth.flea_menu
  (menu_id, menu_code, menu_name, menu_icon, menu_sort, menu_view, menu_level, menu_state, parent_id, create_date, done_date, effective_date, expiry_date, remarks)
VALUES
  -- ===== 1 级：Ace组件 =====
  (1042, 'ace_mgmt',     'Ace组件',  'th-large',       3, NULL,               1, 1, -1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace Admin 模板组件展示菜单'),

  -- ===== 2 级：分类（Ace控制台置于常规组件之前）=====
  (1043, 'ace_console',  'Ace控制台','dashboard',      1, 'ace/aceConsole.html', 2, 1, 1042, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 控制台页面'),
  (1044, 'ace_general',  '常规组件', 'th',             2, NULL,               2, 1, 1042, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 常规组件：画廊/组件/排版/栅格/日历'),
  (1045, 'ace_table',    '表格',     'table',          3, NULL,               2, 1, 1042, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 表格组件'),
  (1046, 'ace_form',     '表单',     'wpforms',        4, NULL,               2, 1, 1042, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 表单组件'),
  (1047, 'ace_ui',       'UI元素',   'cubes',          5, NULL,               2, 1, 1042, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace UI 元素组件'),
  (1048, 'ace_more',     '综合示例', 'files-o',        6, NULL,               2, 1, 1042, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 综合示例页面'),
  (1049, 'ace_other',    '其他',     'ellipsis-h',     7, NULL,               2, 1, 1042, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 其他页面'),

  -- ===== 3 级：常规组件 =====
  (1050, 'ace_gallery',      '画廊',     'picture-o',          1, 'ace/general/gallery.html',        3, 1, 1044, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 画廊'),
  (1051, 'ace_widgets',      '组件',     'th-large',           2, 'ace/general/widgets.html',        3, 1, 1044, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 组件展示'),
  (1052, 'ace_typography',   '排版',     'font',               3, 'ace/general/typography.html',     3, 1, 1044, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 排版示例'),
  (1053, 'ace_grid',         '栅格',     'th',                 4, 'ace/general/grid.html',           3, 1, 1044, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 栅格系统'),
  (1054, 'ace_calendar',     '日历',     'calendar',           5, 'ace/general/calendar.html',       3, 1, 1044, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 日历组件'),

  -- ===== 3 级：表格 =====
  (1055, 'ace_tables',       '普通表格', 'table',              1, 'ace/table/tables.html',   3, 1, 1045, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 普通表格'),
  (1056, 'ace_jqgrid',       '数据表格', 'th',                 2, 'ace/table/jqgrid.html',    3, 1, 1045, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace jqGrid 数据表格'),

  -- ===== 3 级：表单 =====
  (1057, 'ace_form_elements', '表单元素', 'list-alt',          1, 'ace/form/form-elements.html', 3, 1, 1046, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 表单元素'),
  (1058, 'ace_wysiwyg',       '富文本',   'align-justify',      2, 'ace/form/wysiwyg.html',   3, 1, 1046, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 富文本编辑器'),
  (1059, 'ace_form_wizard',   '表单向导', 'magic',              3, 'ace/form/form-wizard.html', 3, 1, 1046, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 表单向导'),
  (1060, 'ace_dropzone',      '文件上传', 'upload',             4, 'ace/form/dropzone.html',   3, 1, 1046, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace Dropzone 文件上传'),

  -- ===== 3 级：UI元素 =====
  (1061, 'ace_buttons',       '按钮',     'square',             1, 'ace/ui/buttons.html',      3, 1, 1047, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 按钮'),
  (1062, 'ace_elements',      '页面元素', 'cubes',              2, 'ace/ui/elements.html',     3, 1, 1047, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 页面元素'),
  (1063, 'ace_jquery_ui',     'JQuery UI','clone',              3, 'ace/ui/jquery-ui.html',    3, 1, 1047, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace JQuery UI'),
  (1064, 'ace_treeview',      '树形视图', 'sitemap',            4, 'ace/ui/treeview.html',     3, 1, 1047, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 树形视图'),
  (1065, 'ace_nestable',      '嵌套列表', 'bars',               5, 'ace/ui/nestable-list.html', 3, 1, 1047, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 嵌套列表'),

  -- ===== 3 级：综合示例 =====
  (1066, 'ace_timeline',      '时间轴',   'clock-o',            1, 'ace/more/timeline.html',   3, 1, 1048, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 时间轴'),
  (1067, 'ace_profile',       '个人资料', 'user',               2, 'ace/more/profile.html',    3, 1, 1048, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 个人资料'),
  (1068, 'ace_pricing',       '价格表',   'money',              3, 'ace/more/pricing.html',    3, 1, 1048, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 价格表'),
  (1069, 'ace_invoice',       '发票',     'file-text',          4, 'ace/more/invoice.html',    3, 1, 1048, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 发票'),
  (1070, 'ace_inbox',        '收件箱',   'inbox',              5, 'ace/more/inbox.html',      3, 1, 1048, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 收件箱'),

  -- ===== 3 级：其他 =====
  (1071, 'ace_faq',          '常见问题', 'question',           1, 'ace/other/faq.html',       3, 1, 1049, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 常见问题'),
  (1072, 'ace_error_404',    '404错误页','exclamation-triangle', 2, 'ace/other/error-404.html', 3, 1, 1049, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 404 错误页'),
  (1073, 'ace_error_500',    '500错误页','exclamation-circle',  3, 'ace/other/error-500.html', 3, 1, 1049, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', 'Ace 500 错误页');

-- ------------------------------------------------------------
-- 2) 插入功能菜单属性（flea_function_attr_menu）
--    与既有菜单保持一致：function_id = menu_id，function_type='MENU'，
--    attr_code='SYSTEM_IN_USE'，attr_value='1001'（所属系统《Flea管家》）
--    列顺序：function_id, function_type, attr_code, attr_value, attr_desc,
--            state, create_date, done_date, effective_date, expiry_date, remarks
-- ------------------------------------------------------------
INSERT INTO fleaauth.flea_function_attr_menu
  (function_id, function_type, attr_code, attr_value, attr_desc, state, create_date, done_date, effective_date, expiry_date, remarks)
VALUES
  (1042, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1043, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1044, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1045, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1046, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1047, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1048, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1049, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1050, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1051, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1052, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1053, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1054, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1055, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1056, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1057, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1058, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1059, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1060, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1061, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1062, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1063, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1064, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1065, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1066, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1067, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1068, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1069, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1070, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1071, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1072, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中'),
  (1073, 'MENU', 'SYSTEM_IN_USE', '1001', '所属系统《Flea管家》', 1, '2026-09-20 20:00:00', NULL, '2026-09-20 20:00:00', '2999-12-31 23:59:59', '《Flea管家》正在使用中');

-- ------------------------------------------------------------
-- 3) 生成菜单权限（flea_privilege / flea_privilege_rel / flea_privilege_group_rel）
--    规则与既有 42 条菜单保持一致：
--      a. 每条菜单 1 条「访问《菜单名》菜单」权限，归属权限组 1002《Ace管理》菜单访问；
--      b. flea_privilege_rel : privilege -> 菜单（rel_type = PRIVILEGE_REL_MENU）；
--      c. flea_privilege_group_rel : 权限组 -> privilege（rel_type = PRIVILEGE_GROUP_REL_PRIVILEGE）。
--    ID 分配（紧接现值，1042~1050 已被元素/操作权限占用，不能与 menu_id 复用）：
--      privilege_id            1050 + 序号 -> 1051 ~ 1082
--      privilege_rel_id          50 + 序号 ->   51 ~   82
--      privilege_group_rel_id    53 + 序号 ->   54 ~   85
-- ------------------------------------------------------------

-- 3.1 确保【《Ace管理》菜单访问】权限组存在（已存在则仅校正名称）
INSERT INTO fleaauth.flea_privilege_group
  (privilege_group_id, privilege_group_name, privilege_group_desc, privilege_group_state, is_main, function_type, create_date, done_date, remarks)
VALUES
  (1002, '《Ace管理》菜单访问', '与【《Ace管理》菜单访问】相关的权限归属的权限组', 1, 0, 'MENU',
   '2026-09-20 20:00:00', NULL, '该权限组包含了【《Ace管理》菜单访问】相关的权限')
ON DUPLICATE KEY UPDATE privilege_group_name = VALUES(privilege_group_name);

-- 3.2 按菜单生成「访问《菜单名》菜单」权限
INSERT INTO fleaauth.flea_privilege
  (privilege_id, privilege_name, privilege_desc, group_id, privilege_state, create_date, done_date, remarks)
SELECT 1050 + (SELECT COUNT(*) FROM fleaauth.flea_menu x WHERE x.menu_id BETWEEN 1042 AND m.menu_id),
       CONCAT('访问《', m.menu_name, '》菜单'),
       CONCAT('拥有可以访问《', m.menu_name, '》菜单的权限'),
       1002, 1, '2026-09-20 20:00:00', NULL,
       CONCAT('【访问《', m.menu_name, '》菜单】权限对应【', m.menu_name, '】菜单，新增菜单时自动生成')
FROM fleaauth.flea_menu m
WHERE m.menu_id BETWEEN 1042 AND 1073;

-- 3.3 权限关联菜单
INSERT INTO fleaauth.flea_privilege_rel
  (privilege_rel_id, privilege_id, rel_id, rel_type, rel_state, create_date, done_date, remarks)
SELECT 50 + (SELECT COUNT(*) FROM fleaauth.flea_menu x WHERE x.menu_id BETWEEN 1042 AND m.menu_id),
       p.privilege_id, m.menu_id, 'PRIVILEGE_REL_MENU', 1, '2026-09-20 20:00:00', NULL,
       CONCAT('【', m.menu_name, '】菜单绑定【访问《', m.menu_name, '》菜单】权限，新增菜单时自动生成')
FROM fleaauth.flea_privilege p
JOIN fleaauth.flea_menu m ON p.privilege_name = CONCAT('访问《', m.menu_name, '》菜单')
WHERE p.group_id = 1002 AND m.menu_id BETWEEN 1042 AND 1073;

-- 3.4 权限挂到权限组 1002
INSERT INTO fleaauth.flea_privilege_group_rel
  (privilege_group_rel_id, privilege_group_id, rel_id, rel_type, rel_state, create_date, done_date, remarks)
SELECT 53 + (SELECT COUNT(*) FROM fleaauth.flea_menu x WHERE x.menu_id BETWEEN 1042 AND m.menu_id),
       1002, p.privilege_id, 'PRIVILEGE_GROUP_REL_PRIVILEGE', 1, '2026-09-20 20:00:00', NULL,
       CONCAT('【《Ace管理》菜单访问】权限组关联【访问《', m.menu_name, '》菜单】权限')
FROM fleaauth.flea_privilege p
JOIN fleaauth.flea_menu m ON p.privilege_name = CONCAT('访问《', m.menu_name, '》菜单')
WHERE p.group_id = 1002 AND m.menu_id BETWEEN 1042 AND 1073;

-- ------------------------------------------------------------
-- 4) 更新 ID 生成器，避免后续框架自增与已插入数据冲突
--    pk_flea_menu                : 1041 -> 1073（新增 32 条）
--    pk_flea_function_attr_menu  :   42 ->   74（新增 32 条）
--    pk_flea_privilege           : 1050 -> 1082（新增 32 条）
--    pk_flea_privilege_rel       :   50 ->   82（新增 32 条）
--    pk_flea_privilege_group_rel :   53 ->   85（新增 32 条）
-- ------------------------------------------------------------
UPDATE fleaauth.flea_id_generator SET id_generator_value = 1073 WHERE id_generator_key = 'pk_flea_menu';
UPDATE fleaauth.flea_id_generator SET id_generator_value = 74   WHERE id_generator_key = 'pk_flea_function_attr_menu';
UPDATE fleaauth.flea_id_generator SET id_generator_value = 1082 WHERE id_generator_key = 'pk_flea_privilege';
UPDATE fleaauth.flea_id_generator SET id_generator_value = 82   WHERE id_generator_key = 'pk_flea_privilege_rel';
UPDATE fleaauth.flea_id_generator SET id_generator_value = 85   WHERE id_generator_key = 'pk_flea_privilege_group_rel';

COMMIT;

-- 校验：以上五项均应为 32
SELECT 'MENU'                AS tbl, COUNT(*) AS cnt FROM fleaauth.flea_menu                 WHERE menu_id BETWEEN 1042 AND 1073
UNION ALL SELECT 'FUNCTION_ATTR',       COUNT(*) FROM fleaauth.flea_function_attr_menu       WHERE function_id BETWEEN 1042 AND 1073
UNION ALL SELECT 'PRIVILEGE',           COUNT(*) FROM fleaauth.flea_privilege                WHERE privilege_id BETWEEN 1051 AND 1082
UNION ALL SELECT 'PRIVILEGE_REL',       COUNT(*) FROM fleaauth.flea_privilege_rel            WHERE privilege_rel_id BETWEEN 51 AND 82
UNION ALL SELECT 'PRIVILEGE_GROUP_REL', COUNT(*) FROM fleaauth.flea_privilege_group_rel      WHERE privilege_group_rel_id BETWEEN 54 AND 85;
