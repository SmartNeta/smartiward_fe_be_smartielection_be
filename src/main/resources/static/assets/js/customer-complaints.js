(function () {
    var app = angular.module('app', ['ngMap']);

    app.controller('complaintsController', complaintsController);

    function complaintsController($http, $scope, $filter, $window, $location, $rootScope, NgMap, GeoCoder) {
        $scope.latitude = "";
        $scope.longitude = "";
        $scope.mapCenter = "";

        $scope.complaintsList = [];
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
                "stateAssembly": $scope.citizen.booth.ward.assemblyConstituency.parliamentaryConstituency.district.stateAssembly,
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
            $scope.complaintsList = [];
            if (searchKey == "All") {
                $scope.complaintsList = $scope.allComplaints;
            } else {
                for (var index = 0; index < $scope.allComplaints.length; index++) {
                    if (searchKey == $scope.allComplaints[index].status) {
                        $scope.complaintsList.push($scope.allComplaints[index]);
                    }
                }
            }
        }

        $scope.myComplaints = function () {
            $http.get("/open/mobile/complaintByCitizen/" + $scope.citizen.id).then(function (response) {
                $scope.allComplaints = response.data.data;
                $scope.complaintsList = $scope.allComplaints;
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
            location.href = "/open/customer/complaints";
        }

        $scope.newComplaint = function () {
            location.href = "/open/customer/new-complaint";
        };

        $scope.openRegisterPopup = function () {
            location.href = "/open/customer/new-complaint";
            return;
//            $("#modelRegister").show();
//            $scope.clearFormData();
//            document.getElementById("blurBackground").style.display = "block";
        }

        $scope.closeRegisterPopup = function () {
            $("#modelRegister").hide();
            document.getElementById("blurBackground").style.display = "none";
        }

        $scope.clearRegistrationForm = function () {
            $scope.clearFormData();
        };

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
            $scope.form['latitude'] = $scope.latitude;
            $scope.form['longitude'] = $scope.longitude;
            $http.post('/open/mobile/complaint', $scope.form).then(function (response) {
                if (response.data) {
                    toastr.success("Complaint Registered Successfully.", 'Success!');
                    location.href = "/open/customer/complaints"
                }
            }, function (error) {
                $scope.loading = false;
                toastr.error("Failed to Registered Complaint.", 'Error!');
            });
        };

        $('#departmentId').on('change', function (e) {
            $scope.getSubdepartments(this.value ? this.value : 0);
        });
        $('#uploadFile').on('change', function (event) {
//            $scope.image = $('input[name=complaintImage]')[0].files[0];
            var fileSize = $('input[name=complaintImage]')[0].files[0].size;
            if (fileSize > 1024000) {
                compress(event);
            } else {
                $scope.image = $('input[name=complaintImage]')[0].files[0];
            }
        });

        $scope.applicationSetting = {};
        $scope.getApplicationSetting = function () {
            $http.get("/open/mobile/getApplicationSettings/").then(function (response) {
                $scope.applicationSetting = response.data.data;
                $scope.tokens = $scope.applicationSetting.footer.split('Smart Neta');
            }, function (error) {
                toastr.error("Failed to Load Application setting", 'Error!');
            });
        };

        function compress(e) {

            if (!e.target.files[0]) {
                return false;
            }

            const fileName = e.target.files[0].name;
            const reader = new FileReader();
            reader.readAsDataURL(e.target.files[0]);
            reader.onload = event => {
                const img = new Image();
                img.src = event.target.result;
                img.onload = () => {
                    const elem = document.createElement('canvas');
                    elem.width = (img.width * 30) / 100;
                    elem.height = (img.height * 30) / 100;
                    const ctx = elem.getContext('2d');

                    // img.width and img.height will contain the original dimensions
                    ctx.drawImage(img, 0, 0, elem.width, elem.height);
                    ctx.canvas.toBlob((blob) => {
                        const file = new File([blob], fileName, {
                            type: 'image/jpeg',
                            lastModified: Date.now()
                        });
                        $scope.image = file;
                    }, 'image/jpeg', 1);
                },
                        reader.onerror = error => console.log(error);
            };
        }

        NgMap.getMap().then(function (map) {
            $scope.incidentMap = map;
        });

//AIzaSyD-5e8hV-uCuE1pEgTyMhk_gJJPK2f3F5A

        $scope.initializeMap = function () {
            if (document.getElementById('incidentMap')) {
                console.log($scope.latitude, ' , ', $scope.longitude);
                $scope.geopos = {lat: $scope.latitude, lng: $scope.longitude};
                var latlng = new google.maps.LatLng($scope.latitude, $scope.longitude);

                var marker = new google.maps.Marker({
                    map: $scope.incidentMap,
                    position: latlng,
                    draggable: true,
                    anchorPoint: new google.maps.Point(0, -29)
                });
                var input = document.getElementById('searchInput');
//            $scope.incidentMap.controls[google.maps.ControlPosition.TOP_LEFT].push(input);
                var geocoder = new google.maps.Geocoder();
                var autocomplete = new google.maps.places.Autocomplete(input);
                autocomplete.bindTo('bounds', $scope.incidentMap);
                var infowindow = new google.maps.InfoWindow();
                autocomplete.addListener('place_changed', function () {
                    infowindow.close();
                    marker.setVisible(false);
                    var place = autocomplete.getPlace();
                    if (!place.geometry) {
                        console.log("Autocomplete's returned place contains no geometry");
                        return;
                    }

                    // If the place has a geometry, then present it on a $scope.incidentMap.
                    if (place.geometry.viewport) {
                        $scope.incidentMap.fitBounds(place.geometry.viewport);
                    } else {
                        $scope.incidentMap.setCenter(place.geometry.location);
                        $scope.incidentMap.setZoom(17);
                    }

                    marker.setPosition(place.geometry.location);
                    marker.setVisible(true);

                    bindDataToForm(place.formatted_address, place.geometry.location.lat(), place.geometry.location.lng());
                    infowindow.setContent(place.formatted_address);
                    infowindow.open($scope.incidentMap, marker);

                });
                $scope.incidentMap.panTo(latlng);
                // this function will work on marker move event into map 
                google.maps.event.addListener(marker, 'dragend', function () {
                    geocoder.geocode({'latLng': marker.getPosition()}, function (results, status) {
                        if (status == google.maps.GeocoderStatus.OK) {
                            if (results[0]) {
                                $scope.incidentMap.panTo(marker.getPosition());
                                $scope.bindDataToForm(results[0].formatted_address, marker.getPosition().lat(), marker.getPosition().lng());
                                infowindow.setContent(results[0].formatted_address);
                                infowindow.open($scope.incidentMap, marker);
                            }
                        }
                    });
                });
            }
        };

        $scope.bindDataToForm = function (address, lat, lng) {
            document.getElementById('location').value = address;
            document.getElementById('lat').value = lat;
            document.getElementById('lng').value = lng;
            $scope.latitude = lat;
            $scope.longitude = lng;
        }

        $scope.getCurrentLocation = function () {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition($scope.showPosition);
            } else {
                console.log("Geolocation is not supported by this browser.");
                $scope.latitude = parseFloat("12.960859145742285");
                $scope.longitude = parseFloat("77.61406791874997");
                google.maps.event.addDomListener(window, 'load', $scope.initializeMap);
            }
        }

        $scope.showPosition = function (position) {
            $scope.latitude = position.coords.latitude;
            $scope.longitude = position.coords.longitude;
            google.maps.event.addDomListener(window, 'load', $scope.initializeMap);
        };

        $scope.getApplicationSetting();
        $scope.myComplaints();
        $scope.myNotifications();
        $scope.getDepartments();
        $scope.clearFormData();
        $scope.getCurrentLocation();

    }

})();