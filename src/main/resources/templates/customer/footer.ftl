<footer class="footer">
    <div class="footer-top">
        <div class="container">
            <div class="row">
                <div class="col-sm-8">
                    <h2>About SmartiWard</h2>

                    <p style="text-align: justify;">SmartiWard is a unique digital platform that allows Citizens to report complaints to local authorities and check updated status of the same. This digital platform connects seamlessly the various departments of the local authorities and brings attention to citizen complaints.. Moving towards Digital india.</p>
                </div><!-- /.col-* -->



                <div class="col-sm-4">
                    <h2>Stay Connected</h2>

                    <ul class="social-links nav nav-pills">
                        <li><a href="{{applicationSetting.twitterLink}}"><i class="fa fa-twitter"></i></a></li>
                        <li><a href="{{applicationSetting.facebookLink}}"><i class="fa fa-facebook"></i></a></li>
                    </ul><!-- /.header-nav-social -->
                </div><!-- /.col-* -->
            </div><!-- /.row -->
        </div><!-- /.container -->
    </div><!-- /.footer-top -->

    <div class="footer-bottom">
        <div class="container">
            <div ng-if="tokens.length == 2" class="footer-bottom-left footer-text-center">
                &copy; {{tokens[0]}} <a target="_blank" href="http://smartneta.com/" style="color:#fff">Smart Neta</a> {{tokens[1]}} <a target="_blank" href="http://smartneta.com/privacy-policy/"  style="color: #fff">Privacy Policy</a>.
            </div><!-- /.footer-bottom-left -->
            <div ng-if="tokens.length != 2" class="footer-bottom-left footer-text-center">
                &copy; {{applicationSetting.footer}} <a target="_blank" href="http://smartneta.com/privacy-policy/">Privacy Policy</a>.
            </div><!-- /.footer-bottom-left -->

        </div><!-- /.container -->
    </div>
</footer><!-- /.footer -->
