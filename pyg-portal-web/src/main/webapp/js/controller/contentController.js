//控制层
app.controller('contentController', function ($scope, contentService,) {

    $scope.contentList = [];//广告列表
    //根据分配id查询广告
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId] = response;
            }
        );
    };
//搜索产地参数,到搜索页
    $scope.search = function () {
        location.href = "http://localhost:9104/search.html#?keywords=" + $scope.keywords;

    }

});
