//控制层
app.controller('itemCatController', function ($scope, $controller, itemCatService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        itemCatService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        itemCatService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //查询实体
    $scope.findOne = function (id) {
        itemCatService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    };

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = itemCatService.update($scope.entity); //修改
        } else {

            $scope.entity.parentId=$scope.parentId;//赋予上级id

            serviceObject = itemCatService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.findByParentId($scope.parentId);//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    };


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        itemCatService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    };

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        itemCatService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    $scope.parentId=0;//上级id;

    //根据上级分类的id查询下级分类列表
    $scope.findByParentId = function (parentId) {

        $scope.parentId=parentId;//记住上级id

        itemCatService.findByParentId(parentId).success(
            function (response) {
                $scope.list = response;
            }
        )
    };

    //面包屑
    //设置级别
    $scope.grade = 1;//默认等级设置为1
    //设置级别
    $scope.setGrade = function (value) {
        $scope.grade = value;
    };
    //读取列表
    $scope.selectList = function (p_entity) {
        if ($scope.grade == 1) {//如果当前是一级
            $scope.entity_1 = null;
            $scope.entity_2 = null;
        }
        if ($scope.grade == 2) {//如果当前是2级
            $scope.entity_1 = p_entity;
            $scope.entity_2 = null;
        }
        if ($scope.grade == 3) {//如果当前是3级
            $scope.entity_2 = p_entity;
        }

        $scope.findByParentId(p_entity.id);//查询此级的下级列表

    };
    /*
    $scope.itemCatList = [{id: -1, name: "顶级分类列表"}];
    //向下查询
    $scope.selectdown = function (p_entity) {
        $scope.itemCatList.push(p_entity);
        $scope.findByParentId(p_entity.id);
    };
    //向上回查
    $scope.selectup = function (p_entity) {
        var index = $scope.itemCatList.indexOf(p_entity);
        $scope.itemCatList.splice(index + 1, ($scope.itemCatList.length - index - 1));
        if (p_entity.id == -1) {
            $scope.findByParentId(0);
        } else {
            $scope.findByParentId(p_entity.id);
        }
    }*/
});	
