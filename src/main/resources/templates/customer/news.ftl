<!DOCTYPE html>
<html ng-app="app">

<#include "header.ftl">

<head>
	<script src="/assets/js/customer-news.js" type="text/javascript"></script>
    <title>SmartiWard</title>
	<style>
		@media(max-width: 1027px){
			.page-title{
				text-align: center;
			}			
			.card-auto{
				width: 34%;
				margin: auto;
			}
			.footer-top {
 			   padding: 0 0px;
			}
		}
			
			.card-auto{
				width: 34%;
				margin: auto;
			}
			.card-title{
				background: transparent;
			}
			.page-title{
				text-align: center;
			}	
		
		@media(max-width: 550px){
			.header{
		    background: #fff;
    		height: 82px;
    	}
			.main-inner {
 			   padding: 23px 0px;
    			padding-bottom: 10px;
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
		.card-auto img{
			height: 300px !important;
		}	
	</style>
	
</head>


<body class="" ng-controller="newsController">

<div class="page-wrapper">
	<#include "navbar.ftl">
    <div class="main" style="margin-top: 63px; display: block;">
        <div class="main-inner">
            <div class="container">
                <div class="content">
                    
<h2 class="page-title">
    {{news.header}}
</h2>

<div class="cards-row">
        <div class="card-row card-title">
            <div class="card-row-inner card-auto">
                <img style="height: 400px;" ng-if="news.image" src="/open/mobile/download-image/{{news.image}}">
                <img ng-if="!news.image" src="/assets/img/tmp/imgnotfound.jpg">
            </div>
        </div>

        <div class="card-row">
            <div class="card-row-inner">
                    <p>{{news.details}}</p>
                    <p style="font-size:13px" id="newsWebLink"></p>
                </div>
            </div>
        </div>
</div>


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
