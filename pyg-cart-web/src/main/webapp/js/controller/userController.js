//控制层
app.controller('userController', function ($scope, $controller, $location, userService) {
    //注册
    $scope.regist = function () {
        if ($scope.entity.password != $scope.password) {
            $scope.entity.password = "";
            $scope.password = "";//清空变量
            alert("两次输入的密码不一致,请重新输入");
            return;
        }//密码验证一致
        userService.add($scope.entity, $scope.smsCode).success(
            function (response) {
                alert(response.message);

                var url = $location.search()["service"];
                if (url != undefined) {
                    //跳回到登录之前的页面
                    window.location.href = "http://localhost:9100/cas/login?service=" + url;
                } else {
                    window.location.href = "http://localhost:9100/cas/login";
                }
            }
        );
    };

    var sec = 60; //倒计时秒
    var flag = true;
    //发送验证码
    $scope.sendCode = function () {
        if ($scope.entity.phone == null || $scope.entity.phone == "") {
            alert("请输入手机号!!!");
            return;
        }
        //验证码60秒倒计时
        if(!flag){
            return;
        }
        userService.sendCode($scope.entity.phone).success(
            function (response) {
                alert(response.message)
            });
        var clock=setInterval(function () {//设置了一个循环定时器
            flag=false;
            if (sec>0){
                $scope.smsMsg=sec+"秒后重新发送";
                $scope.$digest();//强制刷新视图
                sec--;
            }else {
                $scope.smsMsg="重新发送验证码";
                flag=true;
                sec=60;
                $scope.$digest();//强制刷新视图
                clearInterval(clock);
            }
        },1000);//一秒钟执行一次;
    };

});
