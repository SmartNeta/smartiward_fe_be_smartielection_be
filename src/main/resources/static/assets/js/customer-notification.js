(function () {
    var app = angular.module('app', []);

    app.controller('notificationController', notificationController);

    function notificationController($http, $scope, $filter, $window, $location, $rootScope) {

        $scope.notifications = [];
        $scope.notificationCount = 0;
        if ($window.localStorage.getItem("citizen")) {
            $scope.citizen = JSON.parse($window.localStorage.getItem("citizen"));
        } else {
            location.href = "/open/customer/login"
        }

        $scope.myNotifications = function () {
            $http.get("/open/mobile/notification/" + $scope.citizen.id).then(function (response) {
                $scope.notificationCount = response.data.count;
                $scope.notifications = response.data.notifications;
                console.log($scope.notifications);
            }, function (error) {
                toastr.error("Failed to Load notification.", 'Error!');
            });
        }

        $scope.markSeen = function (notificatoinId) {
            var data = {
                "notificatoinId": notificatoinId,
                "citizenId": $scope.citizen.id
            }
            $http.post("/open/mobile/notificationSeen", data).then(function (response) {
                toastr.success("Notification(s) marked as seen.", 'Success!');
                $scope.notificationCount = response.data.count;
                $scope.notifications = response.data.notifications;
                console.log($scope.notifications);
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
        $scope.myNotifications();
    }

})();