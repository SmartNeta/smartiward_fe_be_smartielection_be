(function () {
    var app = angular.module('app', []);

    app.controller('loginController', customerController);

    function customerController($http, $scope, $filter, $window, $location) {


        $scope.form = {
            "voterId": "",
            "mobile": "",
            "otp": ""
        };

        $scope.otpFlag = 1;
        if ($window.localStorage.getItem("citizen")) {
            $scope.citizen = JSON.parse($window.localStorage.getItem("citizen"));
            $scope.otpFlag = 3;
            location.href = "/open/customer/home";
        }

        $scope.generateOTP = function () {
            $http.post("/open/mobile/generateOTP", $scope.form).then(function (response) {
                console.log(response);
                if (response.data.msg == 'success') {
                    $scope.otpFlag = 2;
                    toastr.success("OTP generated Successfully.", 'Success!');
                } else {
                    toastr.error(response.data.msg, 'Error!');
                }
            }, function (error) {
                toastr.error("Failed..", 'Error!');
            });
        };
        
        $scope.resendOTP = function () {
            $http.post("/open/mobile/generateOTP", $scope.form).then(function (response) {
                if (response.data.msg == 'success') {
                    $scope.otpFlag = 2;
                    toastr.success("OTP Resend Successfully.", 'Success!');
                } else {
                    toastr.error(response.data.msg, 'Error!');
                }
            }, function (error) {
                toastr.error("Failed..", 'Error!');
            });             
        };

        $scope.loginWithOTP = function () {
            $http.post("/open/mobile/verifyOTP", $scope.form).then(function (response) {
                if (response.data.msg == 'success') {
                    $scope.citizen = response.data.citizen;
                    $window.localStorage.setItem("citizen", JSON.stringify($scope.citizen));
                    $scope.otpFlag = 3;
                    toastr.success("OTP verified successfully.", 'Success!');
                    setTimeout(function () {
                        location.href = "/open/customer/home"
                    }, 2000);
                } else {
                    toastr.error(response.data.msg, 'Error!');
                }
            }, function (error) {
                toastr.error(error.data.msg, 'Error!');
                $scope.otpFlag = false;
            });
        };
    }

})();