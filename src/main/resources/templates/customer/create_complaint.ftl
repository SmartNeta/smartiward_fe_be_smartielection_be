<!DOCTYPE html>
<html ng-app="app">


<#include "header.ftl">

<head>

    <script src="/assets/js/customer-complaints.js" type="text/javascript"></script>

    <title>SmartiWard | New Complaint</title>
    <style>
    	.align-right{
            float: right;
    	}
        #mapComplaint {
            width : 100% !important;
            height: auto !important;
        }
	@media(max-width: 550px){
            .header{
                background: #fff;
                height: 82px;
            }
            .header-logo {
                position: absolute;
    		margin-left: 80px;	
            }
            .navbar-toggle{
                position: absolute;
		left: 0px;
            }
            .document-title {
 		margin: margin: -80px -757px 30px !important;
            }
            footer {
   	 	height: auto !important;
            }
            .footer-top {
               padding: 0 0px;
            }
            .align-right{
            	float: none;
            }		
            .filter-sort {
                float: none;
    		margin-top: 0;
    		display: block;
            }
            .page-title{
                text-align: center;
            }
            #modelRegister {
                width: 91%;
    		background: #fff;
    		margin: auto;
    		left: unset !important;
            }
            .modal-footer {
    		padding: 15px;
    		text-align: none;
    		border-top: 1px solid #e5e5e5;
    		width: max-content;
    		margin: auto;
            }  
	}

        #map {
            height: 450px !important;
        }
        .input-controls {
          margin-top: 10px;
          border: 1px solid transparent;
          border-radius: 2px 0 0 2px;
          box-sizing: border-box;
          -moz-box-sizing: border-box;
          height: 32px;
          outline: none;
          box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
        }
        #searchInput {
          background-color: #fff;
          font-family: Roboto;
          font-size: 15px;
          font-weight: 300;
          text-overflow: ellipsis;
        }
        #searchInput:focus {
          border-color: #4d90fe;
        }
    </style>
</head>


<body class="" ng-controller="complaintsController">

<div class="page-wrapper" >
	<#include "navbar.ftl">

    <div class="main" style="margin-top: 63px; display: block;">
        <div class="main-inner">
            <div class="container">
                <div class="content">
                    
    <div class="document-title" style="background-image: linear-gradient(0deg,#009f8b,#009f8b)!important;  background: linear-gradient(0deg,#009f8b,#009f8b)!important;">
        <h1>Register New Complaint</h1>
    </div><!-- /.document-title -->


<!-- <i class="fa fa-envelope prefix"></i> -->
    <div style=" top: 10%; left: 20%;">
            <div class="form-group"></div>
            <div ng-if="loading" class="loader"></div>
            
            <form ng-submit="registerComplaint()" style="padding: 0px 25px;">
                <div class="modal-body">
                    <div class="md-form form-sm form-group">
                      <label class="bold ">Complaint </label>  <label style="color: red">*</label>
                      <textarea ng-model="form.complaint" type="text" class="form-control form-control-sm" required></textarea>
                    </div>

                    <div class="form-group">
                        <label class="bold "> Department </label>  <label style="color: red">*</label>
                        <select id="departmentId" class="form-control " required>
                            <option value="">Select Department</option>
                            <option ng-repeat="dept in departments" value="{{dept.id}}" >{{dept.name}}</option>
                        </select>
                    </div>


                    <div class="form-group">
                        <label class="bold ">Sub Department </label>  <label style="color: red">*</label>
                        <select id="subDepartmentId" ng-model="form.subDepartment.id" class="form-control" required>
                            <option value="">Select Sub Department</option>
                            <option ng-repeat="subDept in subDepartments" value="{{subDept.id}}" >{{subDept.name}}</option>
                        </select>
                    </div>


                    <div class="form-group">
                        <label class="bold">Complaint Image </label>
                        <input type="file" id="uploadFile" name="complaintImage" style="width: 100%;" accept="image/png, image/jpeg" >
                    </div>

                    <div class="form-group">  
                        <div id="mapComplaint">
                            <div class="row">
                                <div class="col-lg-12 col-sm-12">
                                    <input id="searchInput" class="form-control" type="text" placeholder="Enter a location">
                                </div>
                            </div>
                            
                            <map zoom="8" id="incidentMap"></map>

                            <div class="row">
                                <div class="col-lg-4 col-sm-6">
                                    <input class="form-control" type="text" name="location" id="location" ng-model="mapCenter">
                                </div>
                                <div class="col-lg-4 col-sm-6">
                                    <input class="form-control" type="text" name="lat" id="lat" ng-model="latitude">
                                </div>
                                <div class="col-lg-4 col-sm-6">
                                    <input class="form-control" type="text" name="lng" id="lng" ng-model="longitude">
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-info waves-effect ml-auto" ng-click="clearRegistrationForm()" >
                        Clear <i class="fa fa-close"></i>
                    </button>
                    <button type="submit" class="btn btn-info waves-effect waves-light">Register
                        <i class="fa fa-sign-in ml-1"></i>
                    </button>
                </div>
            </form>

    </div>

<!--
<div class="pager">
    <ul>
        <li><a href="#">Prev</a></li>
        <li><a href="#">5</a></li>
        <li class="active"><a>6</a></li>
        <li><a href="#">7</a></li>
        <li><a href="#">Next</a></li>
    </ul>
</div><!-- /.pagination -->


                </div><!-- /.content -->
            </div><!-- /.container -->
        </div><!-- /.main-inner -->
    </div><!-- /.main -->

<#include "footer.ftl">

</div><!-- /.page-wrapper -->
  
<div id="blurBackground" style="display: none">
    <div id="overlay-back"></div>
    <div id="overlay">
        <div id="dvLoading">
        </div>
    </div>
</div>    


</body>
</html>
