(function () {
    var app = angular.module('app', []);

    app.controller('complaintsController', complaintsController);

    function complaintsController($http, $scope, $filter, $window, $location, $rootScope) {

        $scope.complaints = [];
        $scope.allComplaints = [];
        $scope.departments = [];
        $scope.subDepartments = [];
        $scope.notificationCount = 0;
        $scope.searchKey = "All";
        $scope.loading = true;
        if ($window.localStorage.getItem("citizen")) {
            $scope.citizen = JSON.parse($window.localStorage.getItem("citizen"));
        } else {
            location.href = "/open/customer/login"
        }

        $scope.clearFormData = function () {
            $scope.form = {
                "citizen": $scope.citizen,
                "stateAssembly" : $scope.citizen.booth.ward.assemblyConstituency.parliamentaryConstituency.district.stateAssembly,
                "complaint": "",
                "subDepartment": {
                    "id": ""
                },
                "image": "",
                "compliantSource": 'Website'
            };
            $('#departmentId').prop('selectedIndex', 0);
        }

        $scope.filter = function (searchKey) {
            $scope.complaints = [];
            if (searchKey == "All") {
                $scope.complaints = $scope.allComplaints;
            } else {
                for (var index = 0; index < $scope.allComplaints.length; index++) {
                    if (searchKey == $scope.allComplaints[index].status) {
                        $scope.complaints.push($scope.allComplaints[index]);
                    }
                }
            }
        }

        $scope.myComplaints = function () {
            $http.get("/open/mobile/complaintByCitizen/" + $scope.citizen.id).then(function (response) {
                $scope.allComplaints = response.data.data;
                $scope.complaints = $scope.allComplaints;
                $scope.searchKey = "All";
                $scope.loading = false;
            }, function (error) {
                toastr.error("Failed to Load Complaints.", 'Error!');
                $scope.loading = false;
            });
        }

        $scope.myNotifications = function () {
            $http.get("/open/mobile/notification/" + $scope.citizen.id).then(function (response) {
                $scope.notificationCount = response.data.count;
                console.log($scope.notificationCount);
            }, function (error) {
                toastr.error("Failed to Load notification.", 'Error!');
            });
        }

        $scope.getDepartments = function () {
            $scope.loading = true;
            $http.get('/open/mobile/departnemt').then(function (response) {
                $scope.departments = response.data.data;
                $scope.loading = false;
            }, function (error) {
                $scope.loading = false;
                toastr.error("Failed to load Departments.", 'Error!');
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

        $scope.openRegisterPopup = function () {
            $("#modelRegister").show();
            $scope.clearFormData();
            document.getElementById("blurBackground").style.display = "block";
        }

        $scope.closeRegisterPopup = function () {
            $("#modelRegister").hide();
            document.getElementById("blurBackground").style.display = "none";
        }

        $scope.getSubdepartments = function (id) {
            $scope.subDepartments = [];
            $scope.form.subDepartment.id = null;
            $scope.loading = true;
            $http.get('/open/mobile/subDepartnemt/' + id).then(function (response) {
                $scope.subDepartments = response.data.data;
                $scope.loading = false;
            }, function (error) {
                $scope.loading = false;
                toastr.error("Failed to load Sub Departments.", 'Error!');
            });
        }

        $scope.registerComplaint = function () {
            $scope.loading = true;
            if ($scope.image) {
                var formData = new FormData();
                formData.append("file", $scope.image);
                $http.post("/open/mobile/upload-image", formData, {
                    transformRequest: angular.identity,
                    headers: {'Content-Type': undefined}
                }).then(function (response) {
                    $scope.form.image = response.data.data;
                    $scope.saveCompalint();
                }, function (error) {
                    $scope.loading = false;
                });
            } else {
                $scope.saveCompalint();
            }
        }

        $scope.saveCompalint = function () {
            $scope.form.subDepartment.id = parseInt($scope.form.subDepartment.id);
            $http.post('/open/mobile/complaint', $scope.form).then(function (response) {
                if (response.data) {
                    $scope.closeRegisterPopup();
                    $scope.myComplaints();
                    toastr.success("Complaint Registered Successfully.", 'Success!');
                }
            }, function (error) {
                $scope.loading = false;
                toastr.error("Failed to Registered Complaint.", 'Error!');
            });
        }

        $('#departmentId').on('change', function (e) {
            $scope.getSubdepartments(this.value ? this.value : 0);
        });

        $('#uploadFile').on('change', function (e) {
            $scope.image = $('input[name=complaintImage]')[0].files[0];
        });

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
        $scope.myComplaints();
        $scope.myNotifications();
        $scope.getDepartments();
        $scope.clearFormData();
    }

})();