<!DOCTYPE html>
<html ng-app="app">

<#include "header.ftl">

<head>
    <title>SmartiWard</title>
	<script src="/assets/js/customer-home.js" type="text/javascript"></script>
	<style>	
	
	.view-more-mob {
		display:none;
	}
		@media(max-width: 550px){
			.view-more-desktop {
				display:none;
			}
			
			.view-more-mob {
				display:block;
			}
			
		
		.header{
		    background: #fff;
    		height: 82px;
    	}	
		.padd{
			padding: 15px 0px;
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
		}
		.cards-row{
			cursor: pointer;
		}		
	</style>
</head>

<body class="" ng-controller="homeController">

<div class="page-wrapper">
<#include "navbar.ftl">

    <div class="main" style="margin-top: 63px; display: block;">
        <div class="main-inner">
            <div class="container">
                <div class="content">
                    
    <div class="document-title" style="background: #0c7592!important; background-color: #0c7592!important;">
         <h1>{{applicationSetting.text}} </h1>
    </div>
<!--
<h4 class="page-title padd">
   {{applicationSetting.text}} 
</h4> 
-->
<h2 class="page-title">
    News
</h2>

<div class="cards-row" ng-if="newses.length > 0">
    

        <div class="card-row" ng-repeat="news in newses">
            <div class="card-row-inner">
                <div  ng-click="showNews(news.id)" class="card-row-image"  style="background-image:url('/open/mobile/download-image/{{news.image}}')">
                    <div class="card-row-image" ng-if="!news.image" style="background-image: url('/assets/img/tmp/imgnotfound.jpg');"></div>
                </div>

                <div class="card-row-body">
                    <h2 class="card-row-title" ng-click="showNews(news.id)">{{news.header}}</h2>
                    <div class="card-row-content"><p style="  cursor: auto; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis;">{{news.details}}</p></div>
                    <p><a href="{{createWebLinkElement(news.webLink)}}" target="_blank">{{news.webLink}}</a></p>
                    <a class="view-more-desktop" ng-click="showNews(news.id)" ng-if="news.details.length > 500" href="#">view more</a>
                    <a class="view-more-mob" ng-click="showNews(news.id)" ng-if="news.details.length > 200" href="#">view more</a>
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
