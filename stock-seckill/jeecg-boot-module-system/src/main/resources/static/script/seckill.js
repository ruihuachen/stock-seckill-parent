//存放主要的交互逻辑js代码
//javaScript 模块化
var seckill = {
    //封装秒杀相关的ajax的url
    URL: {
        //现在的时间
        now:function () {
            return '/stock-seckill/seckill/time/now';
        },
        exposer:function (stockGoodId) {
            return '/stock-seckill/seckill/' + stockGoodId + "/exposer";
        },
        execution:function (stockGoodId, md5) {
            return '/stock-seckill/seckill/'+ stockGoodId + '/' + md5 + '/execute';
        }
    },
    //进入抢票逻辑
    handleSeckillkill: function(stockGoodId, node) {

        console.log("进入-处理抢票逻辑");

        //处理秒杀逻辑
        node.hide()
            .html('<button class="btn btn-primary btn-lg" id="killBtn">开始抢购</button>');

        $.post(seckill.URL.exposer(stockGoodId),{},function (result) {
            //返回值存在且判断为true
            if (result && result['success']) {
                var exposer = result['data'];
                //后台传入若为true，暴露抢票URL，显示抢票按钮
                if (exposer['exposed']) {
                    //获取加密过后的秒杀地址
                    var md5 = exposer['md5'];
                    //执行抢票操作的url：指定某个票商品
                    var seckillUrl = seckill.URL.execution(stockGoodId, md5);

                    //按钮点击一次即禁用：避免前端发送多个重复请求
                    $('#killBtn').one('click', function () {

                        console.log("开始进行抢票,提交按钮被禁用");

                        //绑定秒杀请求操作
                        //1.禁用按钮
                        $(this).addClass("disabled");

                        //2.发送请求
                        $.post(seckillUrl,{}, function (result) {
                            var killResult = result['data'];

                            console.log("KillResult: "+ killResult);

                            var state = killResult['state'];
                            var stateInfo = killResult['stateInfo'];
                            console.log("抢购状态" + stateInfo);

                            //3.显示最终抢购结果
                            node.html('<span class="label label-success">'+stateInfo+'</span>');
                        });
                    });
                    node.show();

                }else {
                    //未到开启秒杀时间 (客户端pc机和服务端pc时间)
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];

                    console.log(now);

                    //重新计算计时逻辑
                    //seckill.countDown(stockGoodId, now, start, end);
                }

            }else {
                console.log("result: "+result);
            }
        });

    },
    //验证用户账号
    validateUserName: function (userName) {
        if (userName.length != 0) {
            return true;
        }
        return false;
    },
    //验证用户密码
    validatePassword: function (password) {
        if (password.length != 0) {
            return true;
        }
        return false;
    },
    //判断是否达到抢购时间
    countDown: function(stockGoodId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');

        console.log("stockGoodId " + stockGoodId);

        //抢购结束
        if (nowTime > endTime) {
            seckillBox.html('该票商品抢购已经结束！请关注平台往后咨讯');
        }else if (nowTime < startTime) {
            //抢购未开启
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime,function (event) {
               //时间格式
               var format = event.strftime('距离抢票开始还剩时间: %D天 %H时 %M分 %S秒');
               seckillBox.html(format);
               /*时间完成后回调抢购地址(回调事件)*/
            }).on('finish.countdown', function () {
                //获取抢购地址 控制显示逻辑
                //用户执行抢购
                seckill.handleSeckillkill(stockGoodId, seckillBox);
            });
        }else {
            //开启抢票
            seckill.handleSeckillkill(stockGoodId, seckillBox);
        }
    },

    //详情页交互逻辑
    detail: {
        //详情页初始化
        init: function (params) {
                //从detail.jsp传过来的数值
                var startTime = params['startTime'];
                var endTime = params['endTime'];
                var stockGoodId = params['stockGoodId'];

                console.log("startTime " + startTime);
                console.log("endTime " + startTime);
                console.log("stockGoodId "+ stockGoodId);

                $.get(seckill.URL.now(), {}, function (result) {
                    //存在且为true
                    if (result && result['success']) {
                        var nowTime = result['data'];

                        console.log(nowTime);

                        //时间判断
                        seckill.countDown(stockGoodId, nowTime, startTime, endTime);

                        console.log(stockGoodId +" " +nowTime +" "+ startTime +" "+ endTime);
                    }else {
                        console.log('result: '+ result);
                    }
                });

        }
    }
};