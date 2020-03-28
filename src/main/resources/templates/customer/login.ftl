<!DOCTYPE html>
<html ng-app="app">
<style type="text/css">
    .login-background {
        background: #0c7592!important;
        overflow: hidden;
    }
    .session-banner {
        background-attachment: fixed;
        background-position: 50%;
        background-repeat: no-repeat;
        background-size: cover;
        bottom: 0;
        left: 0;
        overflow: auto;
        padding: 4rem 1rem;
        position: fixed;
        right: 0;
        top: 0;
    }

    .container {
        padding: 24px;
    }

    @media only screen and (min-width: 1264px)
    .container {
        max-width: 1185px;
    }

    @media only screen and (min-width: 960px)
    .container {
        max-width: 900px;
    }

    .container {
        -webkit-box-flex: 1;
        -ms-flex: 1 1 100%;
        flex: 1 1 100%;
        margin: auto;
        padding: 16px;
        width: 100%;
    }

    .px-0 {
        padding-right: 0!important;
    }

    .layout.wrap {
        -ms-flex-wrap: wrap;
        flex-wrap: wrap;
    }
    .layout.row {
        -webkit-box-orient: horizontal;
        -webkit-box-direction: normal;
        -ms-flex-direction: row;
        flex-direction: row;
    }
    @media (min-width: 1264px)
    .flex.lg5 {
        -ms-flex-preferred-size: 41.66666666666667%;
        flex-basis: 41.66666666666667%;
        -webkit-box-flex: 0;
        -ms-flex-positive: 0;
        flex-grow: 0;
        max-width: 41.66666666666667%;
    }

    @media (min-width: 960px)
    .flex.md6 {
        -ms-flex-preferred-size: 50%;
        flex-basis: 50%;
        -webkit-box-flex: 0;
        -ms-flex-positive: 0;
        flex-grow: 0;
        max-width: 50%;
    }
    @media (min-width: 600px)
    .flex.sm10 {
        -ms-flex-preferred-size: 83.33333333333334%;
        flex-basis: 83.33333333333334%;
        -webkit-box-flex: 0;
        -ms-flex-positive: 0;
        flex-grow: 0;
        max-width: 83.33333333333334%;
    }
    @media (min-width: 0)
    .flex.xs12 {
        -ms-flex-preferred-size: 100%;
        flex-basis: 100%;
        -webkit-box-flex: 0;
        -ms-flex-positive: 0;
        flex-grow: 0;
        max-width: 100%;
    }
    .mx-auto {
        margin: 0 auto;
    }
    .child-flex>*, .flex {
        -webkit-box-flex: 1;
        -ms-flex: 1 1 auto;
        flex: 1 1 auto;
    }

    .mx-auto {
        margin-left: auto!important;
        margin-right: auto!important;
    }
    .child-flex>*, .flex {
        padding: 0 10px!important;
    }
    .child-flex>*, .flex {
        padding: 5px 10px!important;
    }

    .session-block {
        background-color: #fff;
        padding: 3.125rem;
        border-radius: 10px;
        box-shadow: 0 12px 34px 0 rgba(0,0,0,.5);
    }

    .login-background .logo-pos {
        height: 39%;
        width: 35%;
        margin: auto;
    }

    .login-background .logo-pos img {
        height: 100%;
        width: 100%;
    }

    img {
        border-style: none;
    }

    .mb-4 {
        margin-bottom: 24px!important;
    }    

    .mb-form {
        margin-bottom: 50px!important;
    }

    .fs-14 {
        font-size: 14px;
    }
    .px-5 {
        padding-right: 48px!important;
    }
    .px-5 {
        padding-left: 48px!important;
    }
    p {
        margin-top: 0;
        margin-bottom: 1rem;
    }

    .btn {
        margin: 0;
    }

    .btn {
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;
        border-radius: 2px;
        display: -webkit-inline-box;
        display: -ms-inline-flexbox;
        display: inline-flex;
        height: 36px;
        -webkit-box-flex: 0;
        -ms-flex: 0 1 auto;
        flex: 0 1 auto;
        font-size: 14px;
        font-weight: 500;
        -webkit-box-pack: center;
        -ms-flex-pack: center;
        justify-content: center;
        margin: 6px 8px;
        min-width: 88px;
        outline: 0;
        text-transform: uppercase;
        text-decoration: none;
        transition: .3s cubic-bezier(.25,.8,.5,1),color 1ms;
        position: relative;
        vertical-align: middle;
        -webkit-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
        user-select: none;
    }

    [role=button], [type=button], [type=reset], [type=submit], button {
        cursor: pointer;
    }

    .login-window{
        max-width: 41.66666666666667%;
    }

     @media only screen and (max-width: 960px){
        .login-window{
            max-width: 91.333333%;
        }
     }
    .layout.row {
        -webkit-box-orient: horizontal;
        -webkit-box-direction: normal;
        -ms-flex-direction: row;
        flex-direction: row;
    }
    .application--wrap {
        -webkit-box-flex: 1;
        -ms-flex: 1 1 auto;
        flex: 1 1 auto;
        -webkit-backface-visibility: hidden;
        backface-visibility: hidden;
        -webkit-box-orient: vertical;
        -webkit-box-direction: normal;
        -ms-flex-direction: column;
        flex-direction: column;
        min-height: 100vh;
        max-width: 100%;
        position: relative;
    }


    .btn__content {
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;
        border-radius: inherit;
        color: inherit;
        display: -webkit-box;
        display: -ms-flexbox;
        display: flex;
        height: inherit;
        -webkit-box-flex: 1;
        -ms-flex: 1 0 auto;
        flex: 1 0 auto;
        -webkit-box-pack: center;
        -ms-flex-pack: center;
        justify-content: center;
        margin: 0 auto;
        padding: 0 16px;
        transition: .3s cubic-bezier(.25,.8,.5,1);
        white-space: nowrap;
        width: inherit;
    }
    .btn__content {
        height: auto;
    }

    .btn__content:before {
        border-radius: inherit;
        color: inherit;
        content: "";
        position: absolute;
        left: 0;
        top: 0;
        height: 100%;
        opacity: .12;
        transition: .3s cubic-bezier(.25,.8,.5,1);
        width: 100%;
    }

    .text-xs-center {
        text-align: center!important;
    }

    .application .theme--light.btn:not(.btn--icon):not(.btn--flat), .theme--light .btn:not(.btn--icon):not(.btn--flat) {
        background-color: #f5f5f5;
    }
    .btn:not(.btn--depressed) {
        will-change: box-shadow;
        box-shadow: 0 3px 1px -2px rgba(0,0,0,.2), 0 2px 2px 0 rgba(0,0,0,.14), 0 1px 5px 0 rgba(0,0,0,.12);
    }

    .btn {
        margin: 0;
    }
    .bck {
        background: linear-gradient(0deg,#f29828,#f29828)!important;
    }
    .btn-gradient-primary {
        background: linear-gradient(0deg,#6a11cb,#2575fc);
    }
    .btn--block {
        display: -webkit-box;
        display: -ms-flexbox;
        display: flex;
        -webkit-box-flex: 1;
        -ms-flex: 1;
        flex: 1;
        margin: 6px 0;
        width: 100%;
    }

    [type=reset], [type=submit], button, html [type=button] {
        -webkit-appearance: button;
    }
    [role=button], [type=button], [type=reset], [type=submit], button {
        cursor: pointer;
    }
    button, input {
        overflow: visible;
    }
    button, input, select, textarea {
        vertical-align: baseline;
    }
    button, input, select, textarea {
        background-color: transparent;
        border-style: none;
        color: inherit;
    }
    .btn:after, .detail-gallery .owl-next:after, .detail-gallery .owl-prev:after {
        background-color: rgba(0, 0, 0, 0.15);
        bottom: 0px;
        content: '';
        display: block;
        height: 2px;
        left: 0px;
        position: absolute;
        width: 100%;
    }
    .btn__content:before {
        border-radius: inherit;
        color: inherit;
        content: "";
        position: absolute;
        left: 0;
        top: 0;
        height: 100%;
        opacity: .12;
        transition: .3s cubic-bezier(.25,.8,.5,1);
        width: 100%;
    }

</style>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1">

        <link href="http://fonts.googleapis.com/css?family=Nunito:300,400,700" rel="stylesheet" type="text/css">
        <link href="/assets/libraries/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
        <link href="/assets/libraries/owl.carousel/assets/owl.carousel.css" rel="stylesheet" type="text/css" >
        <link href="/assets/libraries/colorbox/example1/colorbox.css" rel="stylesheet" type="text/css" >
        <link href="/assets/libraries/bootstrap-select/bootstrap-select.min.css" rel="stylesheet" type="text/css">
        <link href="/assets/libraries/bootstrap-fileinput/fileinput.min.css" rel="stylesheet" type="text/css">
        <link href="/assets/css/superlist.css" rel="stylesheet" type="text/css" >
        <link href="/assets/css/toastr.min.css" rel="stylesheet" type="text/css" >

        <link rel="shortcut icon" type="image/x-icon" href="/assets/img/smatriward.png">
    	<script src="/assets/js/angular.js" type="text/javascript"></script>
    	<script src="/assets/js/customer-login.js" type="text/javascript"></script>

        <title>SmartiWard | Login</title>
    </head>

    <body class="" ng-controller="loginController">
        <div class="page-wrapper">
            <div class="application--wrap">
                <div class="session-banner login-background">
                    <div class="container px-0">
                        <div ng-if="otpFlag == 1 || otpFlag == 2" class="layout row wrap">
                            <div class="flex xs12 sm10 md6 lg5 mx-auto login-window">
                                <div class="session-block">
                                    <div class="session-head text-xs-center">
                                        <div class="div-icon logo-pos">
                                            <img src="/open/mobile/logo.jpg">
                                        </div> 
                                    </div>
                                    <form class="text-xs-center mb-form" ng-if="otpFlag == 1" ng-submit="generateOTP()">
                                        <h3 class="mb-4" style="font-weight: 400;">Generate OTP</h3> 
                                        <p class="fs-14 px-5" style="color: rgba(0,0,0,.87);">Enter Voter Id and mobile number to generate OTP.</p>
                                        <div class="form-group">
                                            <input type="text" class="form-control" ng-model="form.voterId" required placeholder="Your Voter ID" id="login-form-email">
                                        </div>
                                        <div class="form-group">
                                            <input type="text" class="form-control" ng-model="form.mobile" required pattern="[0-9]{10}" title="Mobile number is not valid" placeholder="Your Mobile no."id="login-form-password">
                                        </div>
                                        <div class="form-group">
                                            <button type="submit" class="btn-gradient-primary bck btn btn--block form-control btn btn-primary btn__content">Generate OTP</button>
                                        </div>
                                    </form>
                                    <form class="text-xs-center mb-form" ng-if="otpFlag == 2" ng-submit="loginWithOTP()">
                                        <h3 class="mb-4" style="font-weight: 400;">Login with OTP</h3>
                                        <p class="fs-14 px-5 mb-form" style="color: rgba(0,0,0,.87);">Verify OTP</p>
                                        <div class="form-group">
                                            <input type="text" class="form-control" ng-model="form.otp" required pattern="[0-9]{4}" title="OTP is not valid" placeholder="Enter OTP" id="login-form-password">
                                        </div>
                                        <div class="form-group">
                                            <button type="submit" class="btn-gradient-primary bck btn btn--block form-control btn btn-primary btn__content">Login</button>
                                        </div>
                                        <div class="form-group">
                                            <a href="#" ng-click="resendOTP()">Resend OTP</a>
                                        </div>
                                    </form>
                                    
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div><!-- /.page-wrapper -->

        <script src="/assets/js/jquery.js" type="text/javascript"></script>
        <script src="/assets/js/map.js" type="text/javascript"></script>
        <script src="/assets/js/toastr.min.js" type="text/javascript"></script>

        <script src="/assets/libraries/bootstrap-sass/javascripts/bootstrap/collapse.js" type="text/javascript"></script>
        <script src="/assets/libraries/bootstrap-sass/javascripts/bootstrap/carousel.js" type="text/javascript"></script>
        <script src="/assets/libraries/bootstrap-sass/javascripts/bootstrap/transition.js" type="text/javascript"></script>
        <script src="/assets/libraries/bootstrap-sass/javascripts/bootstrap/dropdown.js" type="text/javascript"></script>
        <script src="/assets/libraries/bootstrap-sass/javascripts/bootstrap/tooltip.js" type="text/javascript"></script>
        <script src="/assets/libraries/bootstrap-sass/javascripts/bootstrap/tab.js" type="text/javascript"></script>
        <script src="/assets/libraries/bootstrap-sass/javascripts/bootstrap/alert.js" type="text/javascript"></script>

        <script src="/assets/libraries/colorbox/jquery.colorbox-min.js" type="text/javascript"></script>

        <script src="/assets/libraries/flot/jquery.flot.min.js" type="text/javascript"></script>
        <script src="/assets/libraries/flot/jquery.flot.spline.js" type="text/javascript"></script>

        <script src="/assets/libraries/bootstrap-select/bootstrap-select.min.js" type="text/javascript"></script>

        <script src="http://maps.googleapis.com/maps/api/js?libraries=weather,geometry,visualization,places,drawing" type="text/javascript"></script>

        <script type="text/javascript" src="/assets/libraries/jquery-google-map/infobox.js"></script>
        <script type="text/javascript" src="/assets/libraries/jquery-google-map/markerclusterer.js"></script>
        <script type="text/javascript" src="/assets/libraries/jquery-google-map/jquery-google-map.js"></script>

        <script type="text/javascript" src="/assets/libraries/owl.carousel/owl.carousel.js"></script>
        <script type="text/javascript" src="/assets/libraries/bootstrap-fileinput/fileinput.min.js"></script>

        <script src="/assets/js/superlist.js" type="text/javascript"></script>

    </body>
</html>

<!-- old login page

<!DOCTYPE html>
<html ng-app="app">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1">

    <link href="http://fonts.googleapis.com/css?family=Nunito:300,400,700" rel="stylesheet" type="text/css">
    <link href="/assets/libraries/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="/assets/libraries/owl.carousel/assets/owl.carousel.css" rel="stylesheet" type="text/css" >
    <link href="/assets/libraries/colorbox/example1/colorbox.css" rel="stylesheet" type="text/css" >
    <link href="/assets/libraries/bootstrap-select/bootstrap-select.min.css" rel="stylesheet" type="text/css">
    <link href="/assets/libraries/bootstrap-fileinput/fileinput.min.css" rel="stylesheet" type="text/css">
    <link href="/assets/css/superlist.css" rel="stylesheet" type="text/css" >
    <link href="/assets/css/toastr.min.css" rel="stylesheet" type="text/css" >

    <link rel="shortcut icon" type="image/x-icon" href="/assets/img/smatriward.png">
    <script src="/assets/js/angular.js" type="text/javascript"></script>
    <script src="/assets/js/customer-login.js" type="text/javascript"></script>

    <title>SmartiWard | Login</title>
</head>


<body class="" ng-controller="loginController">

<div class="page-wrapper">
    
    <header class="header" style="position: fixed; width: 100%; z-index: 11;">
    <div class="header-wrapper">
        <div class="container">
            <div class="header-inner">
                <div class="header-logo">
                    <a href="/open/customer/login">
                        <img src="/assets/img/smatriward.png" alt="Logo"></img>
                        <span>SmartiWard</span>
                    </a>
                </div>

                  <div class="header-content">
                    <div class="header-bottom">
                    </div>
                </div>
            </div>
        </div>
    </div>
</header>

    <div class="main" style="margin-top: 65px; padding-bottom: 65px; display: block; background: #f29828;">
        <div class="main-inner">
            <div class="container">
                <div class="content">
                    
                    <div class="row">


                        <div ng-if="otpFlag == 1" class="col-sm-4 col-sm-offset-4" style="padding: 19px; background-color: #0c7592;">
                            <div class="page-title">
                            <h1 style="color: aliceblue">Generate OTP</h1>
                            </div>

                            <form ng-submit="generateOTP()">
                                <div class="form-group">
                                    <input type="text" class="form-control" ng-model="form.voterId" required placeholder="Voter ID" id="login-form-email">
                                </div>

                                <div class="form-group">
                                    <input type="text" class="form-control" ng-model="form.mobile" required pattern="[0-9]{10}" title="Mobile number is not valid" placeholder="Mobile Number"id="login-form-password">
                                </div>

                                <div class="form-group">
                                    <button type="submit" class="form-control btn btn-primary">Generate OTP</button>
                                </div>

                            </form>
                        </div>


                        <div ng-if="otpFlag == 2" class="col-sm-4 col-sm-offset-4" style="padding: 19px; background-color: #0c7592;">
                            <div class="page-title">
                            <h1 style="color: aliceblue">Login with OTP</h1>
                            </div>

                            <form ng-submit="loginWithOTP()">
                                <div class="form-group">
                                    <input type="text" class="form-control" ng-model="form.voterId" required placeholder="Voter ID" id="login-form-email">
                                </div>

                                <div class="form-group">
                                    <input type="text" class="form-control" ng-model="form.otp" required pattern="[0-9]{4}" title="OTP is not valid" placeholder="OTP" id="login-form-password">
                                </div>

                                <div class="form-group">
                                    <button type="submit" class="form-control btn btn-primary">Login</button>
                                </div>
                            </form>
                        </div>

                    </div>

                </div>
            </div>
        </div>
    </div>


    <footer class="footer">
    <div class="footer-top">
        <div class="container">
            <div class="">
                <div class="col-sm-6">
                    <h2>About SmartiWard</h2>
                    <p style="text-align: justify;">SmartiWard is a unique digital platform that allows Citizens to report complaints to local authorities and check updated status of the same. This digital platform connects seamlessly the various departments of the local authorities and brings attention to citizen complaints.. Moving towards Digital india.</p>
                </div>

                <div class="col-sm-3">
                    <h2>Contact Information</h2>
                    <p>Sampark 579, 32nd D Cross Road,<br>
                        4th Block Jayanagar, Bangalore 560070.
                    <br>
                    <a href="#">+919844202861</a>
                    </p>
                </div>

                <div class="col-sm-3">
                    <h2>Stay Connected</h2>

                    <ul class="social-links nav nav-pills">
                        <li><a href="#"><i class="fa fa-twitter"></i></a></li>
                        <li><a href="#"><i class="fa fa-facebook"></i></a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    <div class="footer-bottom">
        <div class="container">
            <div class="footer-bottom-left">
                &copy; 2017 Smart Neta. Political Campaign Management Software in Bangalore. All Rights Reserved. <a target="_blank" href="http://smartneta.com/privacy-policy/">Privacy Policy</a>.
            </div>

        </div>
    </div>

</footer>

</div>

<script src="/assets/js/jquery.js" type="text/javascript"></script>
<script src="/assets/js/map.js" type="text/javascript"></script>
<script src="/assets/js/toastr.min.js" type="text/javascript"></script>

<script src="/assets/libraries/bootstrap-sass/javascripts/bootstrap/collapse.js" type="text/javascript"></script>
<script src="/assets/libraries/bootstrap-sass/javascripts/bootstrap/carousel.js" type="text/javascript"></script>
<script src="/assets/libraries/bootstrap-sass/javascripts/bootstrap/transition.js" type="text/javascript"></script>
<script src="/assets/libraries/bootstrap-sass/javascripts/bootstrap/dropdown.js" type="text/javascript"></script>
<script src="/assets/libraries/bootstrap-sass/javascripts/bootstrap/tooltip.js" type="text/javascript"></script>
<script src="/assets/libraries/bootstrap-sass/javascripts/bootstrap/tab.js" type="text/javascript"></script>
<script src="/assets/libraries/bootstrap-sass/javascripts/bootstrap/alert.js" type="text/javascript"></script>

<script src="/assets/libraries/colorbox/jquery.colorbox-min.js" type="text/javascript"></script>

<script src="/assets/libraries/flot/jquery.flot.min.js" type="text/javascript"></script>
<script src="/assets/libraries/flot/jquery.flot.spline.js" type="text/javascript"></script>

<script src="/assets/libraries/bootstrap-select/bootstrap-select.min.js" type="text/javascript"></script>

<script src="http://maps.googleapis.com/maps/api/js?libraries=weather,geometry,visualization,places,drawing" type="text/javascript"></script>

<script type="text/javascript" src="/assets/libraries/jquery-google-map/infobox.js"></script>
<script type="text/javascript" src="/assets/libraries/jquery-google-map/markerclusterer.js"></script>
<script type="text/javascript" src="/assets/libraries/jquery-google-map/jquery-google-map.js"></script>

<script type="text/javascript" src="/assets/libraries/owl.carousel/owl.carousel.js"></script>
<script type="text/javascript" src="/assets/libraries/bootstrap-fileinput/fileinput.min.js"></script>

<script src="/assets/js/superlist.js" type="text/javascript"></script>

</body>
</html>
 -->