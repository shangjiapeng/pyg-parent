//控制层
app.controller('itemController', function ($scope,$http) {

    $scope.specificationItems = {};//存储用户选择的规则
    //数量+-操作
    $scope.addNum = function (x) {
        $scope.num = $scope.num + x;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    };

    //规格点击操作
    $scope.selectSpecification = function (key, value) {
        $scope.specificationItems[key] = value;
    };

    //判断某规格选项是否被用户选中 
    $scope.isSelected = function (key, value) {
        if ($scope.specificationItems[key] == value) {
            return true;
        } else {
            return false;
        }
    };

    //加载默认 SKU
    $scope.loadSku = function () {
        $scope.sku = skuList[0];
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
    };

    //匹配两个对象
    matchObject = function (map1, map2) {
        for (var k in map1) {
            if (map1[k] != map2[k]) {
                return false;
            }
        }
        for (var k in map2) {
            if (map2[k] != map1[k]) {
                return false;
            }
        }
        return true;
    };

    //根据规格查询 SKU
    searchSku = function () {
        for (var i = 0; i < skuList.length; i++) {
            if (matchObject(skuList[i].spec, $scope.specificationItems)) {
                $scope.sku = skuList[i];
                return;
            }
        }
        $scope.sku = {id: 0, title: '--------', price: 0};//如果没有匹配的
    };

    //用户选择规格
    $scope.selectSpecification = function (name, value) {
        $scope.specificationItems[name] = value;
        searchSku();//读取 sku
    };

    //添加商品到购物车(跨域时需要加上这一句:{'withCredentials':true})
    $scope.addToCart = function () {
        // alert('skuid:' + $scope.sku.id);
        $http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='
            +$scope.sku.id+'&num='+$scope.num,{'withCredentials':true}).success(
            function (response) {
                if(response.success){//跳转到购物车
                    location.href='http://localhost:9107/cart.html'
                }else {
                    alert(response.message);
                }
            }
        );
    };



});








