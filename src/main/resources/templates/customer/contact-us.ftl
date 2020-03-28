<!DOCTYPE html>
<html ng-app="app">

<#include "header.ftl">

<head>
	<script src="/assets/js/customer-contact-us.js" type="text/javascript"></script>
    <title>SmartiWard | Contact Us</title>
    <style>
    	@media(max-width: 550px){
			.header{
		    background: #fff;
    		height: 82px;
    	}
			footer {
   			 	height: auto !important;
			}
			.footer-top {
 			   padding: 0 0px;
			}
				.header-logo {
 		   position: absolute;
    		margin-left: 80px;	
		}
		.navbar-toggle{
			position: absolute;
			left: 0px;
		}
		}	
    </style>
</head>


<body class="" ng-controller="contactUsController">

<div class="page-wrapper">
	<#include "navbar.ftl">
    <div class="main" style="margin-top: 63px; display: block;">
        <div class="main-inner">
            <div class="container">
                <div class="content">


    			    <div class="document-title" style="background: #4a4a4a !important; background-color: #4a4a4a !important;">
			    	    <h1>Contact Us</h1>
				    </div>
				    
				    
		<h3>About us</h3>
		<p>{{applicationSetting.about}}</p>

        <div class="row">
            <div class="col-sm-3">
                <h3>Address 1</h3>
                <p>{{applicationSetting.address1}}</p>
            </div>
            <div class="col-sm-3">
                <h3>Address 2</h3>
                <p>{{applicationSetting.address2}}</p>
            </div>
            <div class="col-sm-2">
                <h3>Contact</h3>
                <p>{{applicationSetting.contact}}</p>
            </div>
            <div class="col-sm-2">
                <h3>Email</h3>
                <p>{{applicationSetting.email}}</p>
            </div>
            <div class="col-sm-2">
                <h3>Website</h3>
                <p><a href="{{applicationSetting.website}}" target="_blanck">{{applicationSetting.website}}</a></p>
                    <ul class="social-links nav nav-pills">
                        <li><a href="{{applicationSetting.twitterLink}}" target="_blanck"><i class="fa fa-twitter"></i></a></li>
                        <li><a href="{{applicationSetting.facebookLink}}" target="_blanck"><i class="fa fa-facebook"></i></a></li>
                    </ul><!-- /.header-nav-social -->
                
            </div>
        </div>

                </div>
            </div>
        </div>
    </div>

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
