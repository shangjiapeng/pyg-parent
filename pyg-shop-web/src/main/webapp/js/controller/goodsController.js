//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承


    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //查询实体
    $scope.findOne = function () {
        //TODO location :angularjs的特有的方法,可以从静态页面中获取参数值,传递参数是,需要在?前面加上#
        var id = $location.search()["id"];//从参数集合中查找id的值
        //如果id为空,则不需要继续查询
        if (id != null) {
            goodsService.findOne(id).success(
                function (response) {
                    $scope.entity = response;
                    //TODO 将商品的描述信息添加到kindEditor 中
                    editor.html($scope.entity.goodsDesc.introduction);
                    //商品图片, 页面中需要展示的是json数据,而不是字符串
                    $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                    //扩展属性,页面中需要展示的是json数据,而不是字符串
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                    //规格选择
                    $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems)
                    //转换sku中的规格对象
                    for (var i = 0; i < $scope.entity.itemList.length; i++) {
                        $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec)
                    }
                }
            );
        }
    };

    //保存
    $scope.save = function () {
        $scope.entity.goodsDesc.introduction = editor.html();//处理富文本编辑器
        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有id
            serviceObject = goodsService.update($scope.entity);//修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    alert("保存成功");
                   /* $scope.entity = {};//添加成功之后清空页面数据,方便添加下一个商品
                    editor.html("");//清空富文本编辑器中的内容*/
                   location.href="goods.html";
                }else {
                    alert(response.message);
                }
            }
        );

    };

 /*   //增加
    // 在商家这边没有修改商品信息的功能
    $scope.add = function () {
        $scope.entity.goodsDesc.introduction = editor.html();
        goodsService.add($scope.entity).success(
            function (response) {
                if (response.success) {
                    alert("添加成功");
                    $scope.entity = {};//添加成功之后清空页面数据,方便添加下一个商品
                    editor.html("");//清空富文本编辑器中的内容
                } else {
                    alert(response.message);
                }
            }
        );
    };*/


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
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
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //上传图片
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {
                    $scope.image_entity.url = response.message;
                } else {
                    alert(response.message);
                }
            }
        );
    };

    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};//定义页面实体类的结构
    //将当前上传的图片的实体存入到图片的列表
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity)

    };

    //移除图片
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    };

    //查询1级商品分类列表
    $scope.selectItemCatList_1 = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCatList_1 = response;
            }
        );
    };

    //查询2级商品分类列表
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCatList_2 = response;
            }
        );
    });

    //查询3级商品分类列表
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCatList_3 = response;
            }
        );
    });

    //读取模板的id
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;
            }
        );
    });

    //读取模板的id之后,再读取品牌列表,扩展属性,规格列表
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate = response;//拿到模板对象
                //把所得的模板数据中的(brandIds),json数据转换成字符串的形式
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);//品牌列表的类型转换
                //拿到扩展属性
                //和修改功能共用页面之后,需要判断$location中是否有id的值,,如果有就是修改操作,如果没有就是新增操作
                if ($location.search()["id"] == null) {
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                }
            }
        );
        //读取规格数据
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList = response;
            }
        );
    });

    //点击勾选框之后,向商品详情信息中添加属性信息
    $scope.updateSpecAttribute = function ($event, name, value) {
        var objectByKey = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', name);
        if (objectByKey != null) {
            if ($event.target.checked) {//勾选操作
                objectByKey.attributeValue.push(value);
            } else {//取消勾选操作
                objectByKey.attributeValue.splice(objectByKey.attributeValue.indexOf(value), 1);//移除选项
                //如果所有的选项都取消了,
                if (objectByKey.attributeValue.length == 0) {
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(objectByKey), 1)//移除选项
                }
            }
        } else {//没有找到
            $scope.entity.goodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]});
        }
    };

    //创建sku列表
    $scope.createItemList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 999, status: '0', isDefault: '0'}];//列表的初始化
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue)
        }
    };
//难理解 !!!!
    addColumn = function (list, columnName, columnValues) {
        var newList = [];
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (j = 0; j < columnValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                newRow.spec[columnName] = columnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    };

    //审核状态值
    $scope.status = ['未审核', '已审核', '审核未通过', '已关闭'];
    //商品的分类名称
    $scope.catNameList = [];
    //页面加载时读取所有的商品的分类信息
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (data) {
                for (var i = 0; i < data.length; i++) {
                    //以分类的id作为数组的下标,以分类名称作为值,方面页面的读取
                    $scope.catNameList[data[i].id] = data[i].name;
                }
            }
        );
    };

    //修改商品时,判断规格选项是否应勾选
    $scope.checkAttributeValue = function (specName, optionName) {
        var specList = $scope.entity.goodsDesc.specificationItems ;
        //调用baseController的searchObjectByKey()方法
        var object = $scope.searchObjectByKey(specList, 'attributeName', specName);
        if (object != null) {
            if (object.attributeValue.indexOf(optionName) >= 0) {//如果能够查询到规格选项
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    };

    //$scope.auditStatusList=['未审核', '已审核', '审核未通过', '已关闭'];
    //添加提交审核功能,修改状态为auditStatus=1
    $scope.submitAudit=function () {
        goodsService.updateStatus($scope.selectIds,"1").success(
            function (response) {
                console.log($scope.selectIds);
                $scope.selectIds=[];
                if(response.success){
                    $scope.reloadList()
                }else {
                    alert(response.message);
                }
            }
        );
    }

});	
