app.controller('brandController', function ($scope, $controller, brandService) {
    $controller('baseController', {$scope: $scope});//控制器的继承

    //读取列表数据绑定到表单中
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
                $scope.list = response;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        brandService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //根据id查询实体
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    };

    //保存
    $scope.save = function () {
        var svaeObject;//服务层方法的名称
        if ($scope.entity.id != null) {//如果有id
            svaeObject = brandService.update($scope.entity);//修改
        } else {
            svaeObject = brandService.add($scope.entity);//增加
        }
        svaeObject.success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//重新加载b
                } else {
                    alert(response.message);
                }
            }
        );

    };

    //批量删除
    $scope.delete = function () {
        //获取选中的复选框
        brandService.delete($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }
            }
        );
    };

    $scope.searchEntity = {};//定义搜索对象

    //条件搜索
    $scope.search = function (page, rows) {
        brandService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.paginationConf.totalItems = response.total;//总记录数
                $scope.list = response.rows;//给列表变量赋值
            }
        );
    }
});