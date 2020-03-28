<!DOCTYPE html>
<html ng-app="app">


<#include "header.ftl">

<head>
	<script src="/assets/js/customer-complaints.js" type="text/javascript"></script>
    <title>SmartiWard | Complaints</title>
    <style>
    	.align-right{
            float: right;
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
 		margin: -58px -750px 0px -750px;
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
	</style>
</head>


<body class="" ng-controller="complaintsController">

<div class="page-wrapper">
	<#include "navbar.ftl">

    <div class="main" style="margin-top: 63px; display: block;">
        <div class="main-inner">
            <div class="container">
                <div class="content">
                    
    <div class="document-title" style="background-image: linear-gradient(0deg,#f29828,#f29828)!important;  background: linear-gradient(0deg,#f29828,#f29828)!important;">
        <h1>My Complaints</h1>
    </div><!-- /.document-title -->

    <form class="filter">
        <div class="row">
            <div class="col-sm-12 align-right">
                <button type="button" ng-click="openRegisterPopup()" class="btn btn-primary"><i class="fa fa-plus-circle" aria-hidden="true"></i>Register New Complaint</button>
            </div><!-- /.col-* -->
        </div><!-- /.row -->
    </form>


<!-- <i class="fa fa-envelope prefix"></i> -->
    <div id="modelRegister"  class="modal-container" style=" top: 10%; left: 20%; display: none;">
            <div class="modal-header">
              <p class="heading lead" style="margin-top: 10px;">Register New Complaint</p>
            </div>

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
                        <input type="file" id="uploadFile" name="complaintImage" style="width: 100%;">
                    </div>

                </div>
            
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-info waves-effect ml-auto" ng-click="closeRegisterPopup()" >Close <i class="fa fa-close"></i></button>
                    <button type="submit" class="btn btn-info waves-effect waves-light">Register
                        <i class="fa fa-sign-in ml-1"></i>
                    </button>
                </div>
            </form>

    </div>

<h2 class="page-title">
    {{complaints.length}} complaint(s) you have registered
    <form method="get" action="?" class="filter-sort">
                
        <div class="form-group" style="font-size: initial;">
        <label>Filter By Status : </label>
            <select title="Status" ng-model="searchKey" ng-change="filter(searchKey)">
                <option name="All">All</option>
                <option name="Assigned">Assigned</option>
                <option name="Unassigned">Unassigned</option>
                <option name="Inprogress">Inprogress</option>
                <option name="Resolved">Resolved</option>
                <option name="Ignore">Ignore</option>
                <option name="Out Of Scope">Out Of Scope</option>
                <option name="Under Review">Under Review</option>
            </select>
        </div><!-- /.form-group -->
    </form>
    
</h2>

<div class="cards-row" ng-if="complaints.length > 0">
    

        <div class="card-row" ng-repeat="complaint in complaints">
            <div class="card-row-inner">
                <div class="card-row-image" ng-if="complaint.image" ng-repeat="image in complaint.images" style="background-image:url('/open/mobile/download-image/{{image}}')">
                    <div class="card-row-label"><a>Image</a></div>
                </div>

                <div class="card-row-image" ng-if="!complaint.image" style="background-image:url('/assets/img/tmp/imgnotfound.jpg')">
                </div>

                <div class="card-row-body">
                    <h2 class="card-row-title"><a>Complaint &#35;{{complaint.incidentId}}</a></h2>
                    <div class="card-row-content"><p>{{complaint.complaint}}</p></div>
                </div>

                <div class="card-row-properties">
                    <dl>
                            <dd>Department</dd><dt>{{complaint.subDepartment.department.name}}</dt>
                            <dd>Sub department</dd><dt>{{complaint.subDepartment.name}}</dt>
                            <dd>Status</dd><dt>{{complaint.status}}</dt>
                            <dd>Registered on</dd><dt>{{complaint.createdDate | date : "dd.MM.y"}}</dt>
                            <dd>Tentative Date Of Completion</dd><dt ng-if="complaint.tentativeDateOfCompletion">{{complaint.tentativeDateOfCompletion | date : "dd.MM.y"}}</dt><dt ng-if="!complaint.tentativeDateOfCompletion">N/A</dt>
                            <dd>Registered from</dd><dt>{{complaint.compliantSource}}</dt>
                    </dl>
                </div>
            </div>
        </div>


    
</div><!-- /.cards-row -->

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
