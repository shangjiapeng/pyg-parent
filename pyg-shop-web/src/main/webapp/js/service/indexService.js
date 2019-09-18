//服务层
app.service('indexService',function ($http) {
   this.loginName=function () {
       return $http.get('../login/username.do');
   }
});