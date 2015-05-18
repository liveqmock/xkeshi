/**
 * Created by JetBrains PhpStorm.
 * User: xuheng
 * Date: 12-4-9
 * Time: 上午10:16
 * To change this template use File | Settings | File Templates.
 */

/**
 * 订阅邮件
 * @param txtId
 * @param btnId
 */
function subEmail(txtId, btnId) {
    function checkEmail(data) {
        if (data.length == 0) {
            alert("数据不能为空!");
            return false;
        }
        var pattern = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
        var flag = pattern.test(data);
        if (!flag) {
            alert("邮箱地址不合法！");
            return false;
        }
        return true;
    }

    var txtBox = util.$G(txtId);
    util.on(txtBox, "click", function (event) {
        var target = util.getTarget(util.getEvent(event));
        txtBox.value = "";
        txtBox.style.color = "#000";
    });

    var tmpHandler = function () {
        if (checkEmail(txtBox.value)) {
            util.request(
                {
                    url:"../build/build_email.php",
                    onSuccess:function (responseTxt) {
                        alert("感谢您的订阅，我们发布新版本时会通过邮件告诉您");
                        txtBox.value = "";
                    },
                    param:"q=" + txtBox.value + "&rand=" + Math.random(),
                    loading_Id:null,
                    hasLoading:false
                });
        }
    };
    util.on(util.$G(btnId), "click", tmpHandler);
    util.on(util.$G(txtId), "keyup", function (event) {
        var evt = util.getEvent(event);
        if (util.getCharCode(evt) == 13 && !!evt.ctrlKey) {
            tmpHandler();
        }
    });
}

/**
 * 切换面板
 *@param obj
 */
function switchTab(obj) {
    var ltab = util.$G(obj.ltab),
        rtab = util.$G(obj.rtab),
        r2tab = util.$G(obj.r2tab),
        r3tab = util.$G(obj.r3tab),
        lpanel = util.$G(obj.lpanel),
        rpanel = util.$G(obj.rpanel),
        r2panel = util.$G(obj.r2panel),
        r3panel = util.$G(obj.r3panel);

    util.on(ltab, "click", function () {
        ltab.className = "cur";
        rtab.className = "border-rt";
        r2tab.className = "border-rt";
        r3tab.className = "border-rt";

        lpanel.style.display = "block";
        rpanel.style.display = "none";
        r2panel.style.display = "none";
        r3panel.style.display = "none";
    });

    util.on(rtab, "click", function () {
        ltab.className = "border-lt";
        rtab.className = "cur";
        r2tab.className = "border-rt";
        r3tab.className = "border-rt";

        lpanel.style.display = "none";
        rpanel.style.display = "block";
        r2panel.style.display = "none";
        r3panel.style.display = "none";
    });

    util.on(r2tab, "click", function () {
        ltab.className = "border-lt";
        rtab.className = "border-rt";
        r2tab.className = "cur";
        r3tab.className = "border-rt";

        lpanel.style.display = "none";
        rpanel.style.display = "none";
        r2panel.style.display = "block";
        r3panel.style.display = "none";
    });

    util.on(r3tab, "click", function () {
        ltab.className = "border-lt";
        rtab.className = "border-rt";
        r2tab.className = "border-rt";
        r3tab.className = "cur";

        lpanel.style.display = "none";
        rpanel.style.display = "none";
        r2panel.style.display = "none";
        r3panel.style.display = "block";
    });
}

/**
 * 展开闭合
 *@param issueCls
 * @param conCls
 */
function Pop(issueCls, conCls, isDoc) {
    var doc = document;
    this.popUpId = 0;
    this.popDownId = 0;
    this.isDoc = isDoc;

    this.issueArr = util.getElementsByClassName(issueCls);
    this.conArr = util.getElementsByClassName(conCls);
    this.init();
}
Pop.prototype = {
    init:function () {
        var _this = this,
            version = 0;
        if (!!window.ActiveXObject) {
            version = parseFloat(navigator.userAgent.toLowerCase().match(/msie (\d+)/)[1]);
        }
        for (var i = 0; i < this.issueArr.length; i++) {
            var issueObj = this.issueArr[i],
                conObj = this.conArr[i],
                heightSum = conObj.scrollHeight;
            //ie6 7下搜索动画显示不全
            if (this.isDoc && (version == 7 || version == 6)) {
                heightSum += 30;
            }

            util.on(issueObj, "click", function (issueObj, conObj, heightSum, _this) {
                return function (event) {
                    _this.closeOther(util.getTarget(util.getEvent(event)));
                    if (conObj.offsetHeight == 0) {
                        _this.popUpId = setInterval(function () {
                            _this.popUp(conObj, heightSum)
                        }, 30);
                    }
                    else if (conObj.offsetHeight == heightSum) {
                        _this.popDownId = setInterval(function () {
                            _this.popDown(conObj)
                        }, 30);
                    }
                }
            }(issueObj, conObj, heightSum, _this));
        }
    },
    popUp:function (con, heightSum) {
        var height = con.offsetHeight;
        var speed = Math.ceil((heightSum - height) / 8);
        if (con.offsetHeight == heightSum)
            clearInterval(this.popUpId);
        else
            con.style.height = height + speed + "px";
    },
    popDown:function (con) {
        var height = con.offsetHeight;
        var speed = Math.ceil(height / 8);
        if (con.offsetHeight == 0)
            clearInterval(this.popDownId);
        else
            con.style.height = height - speed + "px";
    },
    closeOther:function (target) {
        var nextNode = target.nextSibling.nodeType == 1 ? target.nextSibling : target.nextElementSibling;
        for (var i = 0; i < this.conArr.length; i++) {
            var conObj = this.conArr[i];
            if (nextNode != conObj && conObj.offsetHeight != 0) {
                clearInterval(this.popUpId);
                conObj.style.height = "0px";
            }
        }
    }
};

/**
 * 返回顶端
 */
function ToTop(btnId) {
    this.backTopId = 0;
    this.btn = util.$G(btnId);
    this.init();
}
ToTop.prototype = {
    init:function () {
        var _this = this,
            version = 0;
        if (!!window.ActiveXObject) {
            version = parseFloat(navigator.userAgent.toLowerCase().match(/msie (\d+)/)[1]);
        }
        util.on(this.btn, "click", function () {
            _this.backTopId = setInterval(function () {
                _this.backTop()
            }, 30);
        });
        util.on(window, "scroll", function () {
            var doc = document,
                scrTop = doc.documentElement.scrollTop || doc.body.scrollTop;

            _this.btn.style.display = scrTop >= 100 ? "block" : "none";
            //ie6下返回顶端位置
            if (version == 6)  _this.btn.style.top = 600 + scrTop + "px";
        });
    },
    backTop:function () {
        var doc = document,
            scrTop = doc.documentElement.scrollTop || doc.body.scrollTop,
            speed = Math.ceil(scrTop / 4);
        if (scrTop == 0)
            clearInterval(this.backTopId);
        else
            doc.documentElement.scrollTop = doc.body.scrollTop = scrTop - speed;
    }
};
/**
 * family展示
 */
function initFamily() {
    var doc = document,
        familyBtn = util.$G("J_family");

    function getViewportElement() {
        var browser=util.getBrowerVersion();
        return (browser.ie && browser.quirks) ?
            document.body : document.documentElement;
    }

    function setImgCenter() {
        var show = util.$G("J_show"),
            viewportEl = getViewportElement(),
            width = (window.innerWidth || viewportEl.clientWidth) | 0,
            height = (window.innerHeight || viewportEl.clientHeight) | 0,
            scrTop = doc.documentElement.scrollTop || doc.body.scrollTop;

        show.style.cssText = "left:" + (width / 2 - show.offsetWidth / 2)
            + "px;top:" + (height / 2 - show.offsetHeight / 2+scrTop) + "px;";
    }

    util.on(familyBtn, "click", function () {
        if (!util.$G("J_mask")) {
            var fragement = doc.createDocumentFragment(),
                mask = doc.createElement("div"),
                show = doc.createElement("div");

            mask.id = "J_mask";
            mask.className = "mask";
            fragement.appendChild(mask);

            show.id = "J_show";
            show.className = "show";
            show.innerHTML = "<img src='images/member.jpg' alt='UEditor全家福'>" +
                "<a class='close' id='J_close'></a>";
            fragement.appendChild(show);

            util.$G("wrapper").appendChild(fragement);
            setImgCenter();

            util.on(util.$G("J_close"), "click", function () {
                util.$G("J_mask").style.display = "none";
                util.$G("J_show").style.display = "none";
            });
        } else {
            util.$G("J_mask").style.display = "";
            util.$G("J_show").style.display = "";
            setImgCenter();
        }
    });
}
/**
 * 边框旋转
 */
function border_move() {
    var borTop = util.$G("J_borTop"),
        borBottom = util.$G("J_borBottom"),
        borLeft = util.$G("J_borLeft"),
        borRight = util.$G("J_borRight"),

        left = util.getStyleValue(borTop, 'left'),
        top = util.getStyleValue(borLeft, 'top');

    setInterval(function () {
        if (left < 0) {
            left += 2;
            borRight.style.top = left + "px";
            borTop.style.left = left + "px";
        } else left = -1500;

        if (top > -3000) {
            top -= 2;
            borBottom.style.left = top + "px";
            borLeft.style.top = top + "px";
        } else top = -1500;
    }, 60);
}

/**
 * 切换面板
 *@param obj
 */
function switchTab(obj) {
    var ltab = util.$G(obj.ltab),
        rtab = util.$G(obj.rtab),
        r2tab = util.$G(obj.r2tab),
        r3tab = util.$G(obj.r3tab),
        lpanel = util.$G(obj.lpanel),
        rpanel = util.$G(obj.rpanel),
        r2panel = util.$G(obj.r2panel),
        r3panel = util.$G(obj.r3panel);

    util.on(ltab, "click", function () {
        ltab.className = "cur";
        rtab.className = "border-rt";
        r2tab.className = "border-rt";
        r3tab.className = "border-rt";

        lpanel.style.display = "block";
        rpanel.style.display = "none";
        r2panel.style.display = "none";
        r3panel.style.display = "none";
    });

    util.on(rtab, "click", function () {
        ltab.className = "border-lt";
        rtab.className = "cur";
        r2tab.className = "border-rt";
        r3tab.className = "border-rt";

        lpanel.style.display = "none";
        rpanel.style.display = "block";
        r2panel.style.display = "none";
        r3panel.style.display = "none";
    });

    util.on(r2tab, "click", function () {
        ltab.className = "border-lt";
        rtab.className = "border-rt";
        r2tab.className = "cur";
        r3tab.className = "border-rt";

        lpanel.style.display = "none";
        rpanel.style.display = "none";
        r2panel.style.display = "block";
        r3panel.style.display = "none";
    });

    util.on(r3tab, "click", function () {
        ltab.className = "border-lt";
        rtab.className = "border-rt";
        r2tab.className = "border-rt";
        r3tab.className = "cur";

        lpanel.style.display = "none";
        rpanel.style.display = "none";
        r2panel.style.display = "none";
        r3panel.style.display = "block";
    });
}