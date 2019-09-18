//购物车控制层
app.controller('cartController', function ($scope, cartService) {
    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;
                $scope.totalValue = cartService.sum($scope.cartList);//求合计数
            }
        );
    };

    //商品数量加减
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(
            function (response) {
                if (response.success) {
                    $scope.findCartList();//刷新列表
                } else {
                    alert(response.message);//弹出错误提示
                }
            }
        );
    };

    //获取当前登录账号的收货地址列表
    $scope.findAddressList = function () {
        cartService.findAddressList().success(
            function (response) {
                $scope.addressList = response;
                //设置默认地址
                for (var i = 0; i < $scope.addressList.length; i++) {
                    if ($scope.addressList[i].isDefault == "1") {
                        $scope.address = $scope.addressList[i];
                        break;
                    }
                }
            }
        );
    };

    //选择地址
    $scope.selectAddress = function (address) {
        $scope.address = address;
    };

    //判断是否是当前选中的地址
    $scope.isSelectedAddress = function (address) {
        if (address == $scope.address) {
            return true;
        } else {
            return false;
        }
    };

    //添加收货地址
    $scope.addAddress = function () {
        cartService.addAddress($scope.newAddress).success(
            function (response) {
                if (response.success) {
                    $scope.findAddressList();//刷新列表
                } else {
                    alert(response.message);//弹出错误提示
                }
            }
        );
    };

    //选择支付的方式
    $scope.order = {paymentType: '1'};//订单对象
    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    };

    //提交购物车订单
    $scope.submitOrder = function () {
        //提交订单时,要携带收货人姓名电话,和收货人的地址
        $scope.order.receiver = $scope.address.contact;//姓名
        $scope.order.receiverMobile = $scope.address.mobile;//电话
        $scope.order.receiverAreaName = $scope.address.address;//地址
        cartService.submitOrder($scope.order).success(
            function (response) {
                if (response.success) {
                    //订单提交成功之后,页面跳转
                    if ($scope.order.paymentType == "1") {//如果是微信支付,跳转到支付界面
                        location.href = "pay.html"
                    } else {//如果是货到付款,跳转到提示界面
                        location.href = "paysuccess.html";
                    }
                }else {//如果提交失败,弹窗提示
                    alert(response.message);
                    location.href="payfail.html";
                }
            }
        );
    };


});