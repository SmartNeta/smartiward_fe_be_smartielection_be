<!DOCTYPE html>
<html ng-app="app">

<#include "header.ftl">

<head>
	<script src="/assets/js/customer-notification.js" type="text/javascript"></script>
    <title>SmartiWard | Notifications</title>
    <style>
    	.align-right{
			text-align: right;
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
 		   		margin: -80px -750px 0px -750px;
			}
			footer {
   			 	height: auto !important;
			}
			.footer-top {
 			   padding: 0 0px;
			}
			.align-right{
				text-align: center;
			}	
		}	
	</style>
</head>


<body class="" ng-controller="notificationController">

<div class="page-wrapper">
	<#include "navbar.ftl">
    <div class="main" style="margin-top: 63px; display: block;">
        <div class="main-inner">
            <div class="container">
                <div class="content">
                    
    <div class="document-title" style="background-color: #6a70d7; background: #6a70d7;">
        <h1>My Notifications</h1>
    </div><!-- /.document-title -->

    <form class="filter">
        <div class="row">
            <div class="col-sm-12 align-right">
                <button type="button" ng-click="markSeen(-1)" class="btn btn-primary"><i class="fa fa-check" aria-hidden="true"></i>Mark all as seen</button>
            </div><!-- /.col-* -->
        </div><!-- /.row -->
    </form>


<h2 class="page-title">
    {{notifications.length}} new notification(s)
</h2><!-- /.page-title -->

<div class="cards-row" ng-if="notifications.length > 0">
    
        <div class="card-row" ng-repeat="notification in notifications">
            <div class="card-row-inner">
                <div class="card-row-image" ng-if="!notification.complaint.image" style="background-image:url('/assets/img/tmp/imgnotfound.jpg')">
                </div>

                <div class="card-row-image" ng-repeat="image in notification.complaint.images" style="background-image:url('/open/mobile/download-image/{{image}}')">
                    <div class="card-row-label" ng-if="notification.complaint.image"><a>Image</a></div>
                </div>

                <div class="card-row-body">
                    <h2 class="card-row-title"><a>{{notification.notification}}</a></h2>
                    <div class="card-row-content"><p>Complaint &#35;{{notification.complaint.incidentId}}</p></div>
                    <div class="card-row-content"><p>{{notification.complaint.complaint}}</p></div>
							<button type="button" ng-click="markSeen(notification.id)" class="btn"><i class="fa fa-check" aria-hidden="true"></i>Mark as seen</button>
                </div>

                <div class="card-row-properties">
                    <dl>
                            <dd>Department</dd><dt>{{notification.complaint.subDepartment.department.name}}</dt>
                            <dd>Sub department</dd><dt>{{notification.complaint.subDepartment.name}}</dt>
                            <dd ng-if="notification.complaint.status">Status</dd><dt>{{notification.complaint.status}}</dt>
                            <dd>Registered on</dd><dt>{{notification.complaint.createdDate | date : "dd.MM.y"}}</dt>
                            <dd>Tentative Date Of Completion</dd><dt ng-if="notification.complaint.tentativeDateOfCompletion">{{notification.complaint.tentativeDateOfCompletion | date : "dd.MM.y"}}</dt><dt ng-if="!notification.complaint.tentativeDateOfCompletion">N/A</dt>
                            <dd>Registered from</dd><dt>{{notification.complaint.compliantSource}}</dt>
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
