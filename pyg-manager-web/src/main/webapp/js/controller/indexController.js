//控制层
app.controller('indexController',function ($scope ,indexService) {
    $scope.showLoginName=function () {
        indexService.loginName().success(
            function (response) {
                $scope.loginName=response.loginName;
            }
        );
    }
});