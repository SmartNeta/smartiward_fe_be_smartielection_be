<header class="header" style="position: fixed; width: 100%; z-index: 9;">
    <div class="header-wrapper">
        <div class="container">
            <div class="header-inner">
                <div class="header-logo">
                    <a href="/open/customer/home">
                        <img src="/open/mobile/logo.jpg" alt="Logo">
                        <!-- <span>{{applicationSetting.name}}</span> -->
                    </a>
                </div>

                <div class="header-content">

                    <div class="header-bottom">                    
                    
                        <ul class="header-nav-primary nav nav-pills collapse navbar-collapse">
                        	<li >
                            	<a href="#" ng-click="home()">Home</a>
            	            </li>
                        	<li >
                            	<a href="#" ng-click="complaints()">Complaints</a>
            	            </li>
                        	<li >
                            	<a href="#" ng-click="contactUs()">Contact Us</a>
            	            </li>
                        	<li >
                            	<a href="#" ng-click="logout()">Logout</a>
            	            </li>
                        	<li >
                        		<a><i ng-click="notification()" class="fa fa-bell" style="cursor: pointer;"><span ng-if="notificationCount != 0" class="notification-number">{{notificationCount}}</span></i></a>
            	            </li>
                        </ul>

                        <button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target=".header-nav-primary">
                            <span class="sr-only">Toggle navigation</span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>

                    </div>
                </div>
            </div>
        </div>
    </div>
</header>