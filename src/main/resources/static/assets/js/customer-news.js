(function () {
    var app = angular.module('app', []);

    app.controller('newsController', newsController);

    function newsController($http, $scope, $filter, $window, $location, $rootScope) {

        var newsId = $location.url().replace("/", "");

        $scope.news = {};
        $scope.notificationCount = 0;
        if ($window.localStorage.getItem("citizen") && newsId) {
            $scope.citizen = JSON.parse($window.localStorage.getItem("citizen"));
        } else {
            location.href = "/open/customer/login"
        }

        $scope.news = function () {
            $http.get("/open/mobile/newsById/" + newsId).then(function (response) {
                $scope.news = response.data.data;
                $scope.getLinks($scope.news);
            }, function (error) {
                toastr.error("Failed to Load News.", 'Error!');
                location.href = "/open/customer/home";
            });
        };

        $scope.getLinks = function (news) {
            if (news && news.webLink) {
//                var arr = news.webLink.split(/[ ,]+/);
//                if (arr) {
//                    for (var i = 0; i < arr.length; i++) {
                document.getElementById("newsWebLink").appendChild($scope.createWebLinkElement(news.webLink));
//                    }
//                }
            }
        };

        $scope.createWebLinkElement = function (ele) {
            var anchor = document.createElement('a');
            var pattern = /^((http|https|ftp):\/\/)/;
            if (!pattern.test(ele)) {
                ele = "http://" + ele;
            }
            console.log(ele);
            anchor.href = ele;
            anchor.text = "  " + ele;
            anchor.target = "_blank";
            return anchor;
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

        $scope.newComplaint = function () {
            location.href = "/open/customer/new-complaint";
        };

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