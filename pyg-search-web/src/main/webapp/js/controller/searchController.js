app.controller('searchController', function ($scope,$location, searchService) {
    //定义搜索对象的结构
    $scope.searchMap = {
        'keywords': '', 'category': '', 'brand': '', 'spec': {},
        'price': '', 'pageNo': 1, 'pageSize': 20,'sortField':'','sortValue':''
    };
    //加载从首页传过来的搜索关键字
    $scope.loadKeywords=function () {
        $scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search();
    };

    //添加搜索项
    $scope.addSearchItem = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {//如果点击的是分类或者是品牌
            $scope.searchMap[key] = value;
        } else {//如果点击的是规格
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//点击之后进行查询'
    };

    //移除复合搜索条件
    $scope.removeSearchItem = function (key) {
        if (key == "category" || key == "brand" || key == 'price') {//如果是分类或品牌
            $scope.searchMap[key] = "";
        } else {//否则是规格
            delete $scope.searchMap.spec[key];//移除此属性
        }
        $scope.search();//点击之后进行查询
    };

    //构建分页标签(totalPages 为总页数)
    buildPageLabel = function () {
        $scope.pageLabel = [];//新增分页栏属性
        var maxPageNo = $scope.resultMap.totalPages;//得到最后页码
        var firstPage = 1;//开始页码
        var lastPage = maxPageNo;//截止页码
        $scope.firstDot = true;//前面有省略号
        $scope.lastDot = true;//后面有省略号
        if ($scope.resultMap.totalPages > 5) {  //如果总页数大于 5 页,显示部分页码
            if ($scope.searchMap.pageNo <= 3) {//如果当前页小于等于 3
                lastPage = 5; //前 5 页
                $scope.firstDot = false;//前面没有省略号
            } else if ($scope.searchMap.pageNo >= lastPage - 2) {//如果当前页大于等于最大页码 - 2
                firstPage = maxPageNo - 4;   //后 5 页
                $scope.lastDot = false;//后边省略号
            } else { //显示当前页为中心的 5 页
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {
            $scope.firstDot = false;//前面没有省略号
            $scope.lastDot = false;//后边省略号
        }
        //循环产生页码标签
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    };

    //搜索
    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;//搜索返回的结果

                buildPageLabel();//调用
            }
        );
    };

    //根据页码查询
    $scope.queryByPage = function (pageNo) {
        //页码验证
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = pageNo;

        $scope.search();
    };

    //判断当前页为第一页
    $scope.isFirstPage=function(){
        if($scope.searchMap.pageNo==1){
            return true;
        }else{
            return false;
        }
    };
    //判断当前页是否未最后一页
    $scope.isLastPage=function(){
        if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else{
            return false;
        }
    };

     //排序查询
    $scope.sortSearch=function (sortField,sortValue) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sortValue=sortValue;
        $scope.search();
    };

    //判断关键字是不是品牌
    $scope.keywordsIsBrand=function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){//如果包含
                return true;
            }

        }
        return false;
    };

});
