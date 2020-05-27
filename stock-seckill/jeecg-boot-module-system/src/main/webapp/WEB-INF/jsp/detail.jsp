<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="comment/tag.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${stockGood.title}详情页</title>
    <!-- 静态包含-->
    <%@include file="comment/head.jsp" %>

</head>
<body>
<div class="container">
    <div class="pane1 pane1-default text-center">
        <div class="pannel-heading">
            <h1>${stockGood.title}</h1>
            <h5>${stockGood.description}</h5>
        </div>
        <div class="pane1-body">
            <h2 class="text-dialog">
                <!-- 显示time图标-->
                <span class="glyphicon glyphicon-time"></span>
                <!-- 展示倒计时-->
                <span class="glyphicon" id="seckill-box"></span>
            </h2>
        </div>
    </div>
</div>

<!-- 弹出层 登陆验证-->
<div id="checkUserModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
<%--            <div class="modal-header">--%>
<%--                <h3 class="modal-litter text-center">--%>
<%--                    <span class="glyphicon glyphicon-userName"></span>用户账号：--%>
<%--                </h3>--%>
<%--                <br>--%>
<%--                <h3 class="modal-litter text-center">--%>
<%--                    <span class="glyphicon glyphicon-password"></span>密码：--%>
<%--                </h3>--%>
<%--            </div>--%>

            <div class="modal-body">
                <div class="row">
                    <div class="col-xs-8 col-xs-offset-2">
                        <label for="userName">用户账号</label>
                        <input type="text" name="userName" id="userName" placeholder="填写用户账号" class="form-control">
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-8 col-xs-offset-2">
                        <label for="password">密码</label>
                        <input type="text" name="password" id="password" placeholder="填写密码" class="form-control">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <!-- 验证信息-->
                <span id="checkUserMessage" class="glyphicon"></span>
                <button type="button" id="checkUserBtn" class="btn btn-success">
                    提交
                </button>
                <div class="formoperate" id="prompt"
                    style="text-align: center;color: red;padding-top: 15px">
                </div>
            </div>
        </div>
    </div>
</div>
</body>

<!-- jQuery文件 务必在bootstrap.min.js 之前引入 -->
<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>

<!-- JQuery cookie操作插件-->
<script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.js"></script>

<!-- JQuery countDown倒计时插件-->
<script src="https://cdn.bootcss.com/jquery.countdown/2.2.0/jquery.countdown.js"></script>

<!-- 交互逻辑-->
<script src="${pageContext.request.contextPath}/script/seckill.js" type="text/javascript"></script>

<script type="text/javascript">
    $(function (){
        //使用EL表达式
        seckill.detail.init({
            //js函数中传参，会自动科学计数需要转成字符串
            stockGoodId: "${stockGood.id}" ,
            startTime: ${stockGood.startTime.time},
            endTime: ${stockGood.endTime.time}
        });
    });
</script>

</html>
