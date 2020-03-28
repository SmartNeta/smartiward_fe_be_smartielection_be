(function () {
    var app = angular.module('app', []);

    app.controller('homeController', homeController);

    function homeController($http, $scope, $filter, $window, $location, $rootScope) {

        $scope.newses = [];
        $scope.notificationCount = 0;
        if ($window.localStorage.getItem("citizen")) {
            $scope.citizen = JSON.parse($window.localStorage.getItem("citizen"));
        } else {
            location.href = "/open/customer/login"
        }

        $scope.news = function () {
            $http.get("/open/mobile/news/" + $scope.citizen.booth.ward.assemblyConstituency.parliamentaryConstituency.district.stateAssembly.id).then(function (response) {
                $scope.newses = response.data.data;
            }, function (error) {
                toastr.error("Failed to Load News.", 'Error!');
            });
        };

        $scope.createWebLinkElement = function (ele) {
            if (ele) {
                var pattern = /^((http|https|ftp):\/\/)/;
                if (!pattern.test(ele)) {
                    ele = "http://" + ele;
                }
                return ele;
            } else {
                return "";
            }
        };

        $scope.myNotifications = function () {
            $http.get("/open/mobile/notification/" + $scope.citizen.id).then(function (response) {
                $scope.notificationCount = response.data.count;
                console.log($scope.notificationCount);
            }, function (error) {
                toastr.error("Failed to Load notification.", 'Error!');
            });
        }

        $scope.logout = function () {
            $http.post("/open/mobile/logoutCitizen/" + $scope.citizen.voterId).then(function (response) {
                $scope.logoutResult = response.data.count;
                $window.localStorage.removeItem("citizen")
                location.href = "/open/customer/login"
            }, function (error) {
                $window.localStorage.removeItem("citizen")
                location.href = "/open/customer/login"
            });
        }

        $scope.notification = function () {
            location.href = "/open/customer/notification"
        }

        $scope.home = function () {
            location.href = "/open/customer/home"
        }

        $scope.contactUs = function () {
            location.href = "/open/customer/contact-us"
        }

        $scope.complaints = function () {
            location.href = "/open/customer/complaints"
        }

        $scope.showNews = function (newsId) {
            location.href = "/open/customer/news#" + newsId
        }

        $scope.applicationSetting = {};
        $scope.getApplicationSetting = function () {
            $http.get("/open/mobile/getApplicationSettings/").then(function (response) {
                $scope.applicationSetting = response.data.data;
                $scope.tokens = $scope.applicationSetting.footer.split('Smart Neta');
            }, function (error) {
                toastr.error("Failed to Load Application setting", 'Error!');
            });
        }
        $scope.getApplicationSetting();
        $scope.news();
        $scope.myNotifications();
    }

})();