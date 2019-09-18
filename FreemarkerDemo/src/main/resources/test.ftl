<html>
<head>
    <meta CHARSET="UTF-8">
    <title>freemarker入门小demo</title>

</head>
<body>
<#--我只是一个注释,不会有任何的输出-->
<!--html注释-->
${name},你好.${message}
<br>

<#--assign指令,用于在页面上定义一个变量-->
<#--定义简单类型-->
<#assign linkman='小周'>
联系人:${linkman}
<#--定义对象类型-->
<#assign info={"mobile":"15868686688","address":"上海市徐汇区"}>
电话:${info.mobile} 地址:${info.address}

<#--include指令,用于模板文件的嵌套-->
<#include "head.ftl">


<#--if指令 -->
<#if success==true>
    你已经通过实名认证
<#else>
    你未通过实名认证
</#if>
<br>

<#--list 指令  如果想在循环中得到索引，使用循环变量+_index 就可以得到-->
----商品价目表----<br>
<#list goodsList as goods>
    ${goods_index+1} 商品名称:${goods.name} 价格:${goods.price}<br>
</#list>
<br>

<#--内建函数  语法格式:变量+?+函数名称-->
共${goodsList?size}条数据
<br>

<#--转换 JSON 字符串为对象-->
<#assign text="{'bank':'工商银行','account':'10101920201920212'}" />
<#assign data=text?eval />
开户行：${data.bank} 账号：${data.account}
<br>

<#--日期格式化-->
当前日期:${today?date}<br>
当前时间:${today?time}<br>
当前日期+时间:${today?datetime}<br>
日期格式化:${today?string("yyyy年MM月dd日 HH:mm:ss")}<br>

<#--数字转换成字符串-->
累计积分:${point}<br>
累计积分:${point?c}<br>

<#--判断某变量是否存在:“??-->
<#if aaa??>
    aaa变量存在
<#else>
    aaa变量不存在
</#if>
<br>

<#-- 缺失变量默认值:“!" 当 aaa 为 null 则返回！后边的内容- -->
${aaa!"----aaa没有被赋值"}<br>

<#--逻辑运算符-->

<#--比较运算符-->
<#if point gt 100>
    黄金会员
</#if>

</body>
</html>