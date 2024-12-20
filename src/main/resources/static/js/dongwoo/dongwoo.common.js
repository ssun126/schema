document.title = "SCHEMA";
var dpiisUrl = "https://dpiis.dwchem.co.kr/";
$("head").append('<link rel="shortcut icon" type="image/x-icon" href="../../../images/common/favicon.ico" />');
$("head").append("<script src='../../../js/dongwoo/ajaxService.js'>");

$(function () {
	if ("/" != common.curURI() && "/#" != common.curURI()
			&& "/index.html" != common.curURI()
			&& common.curURI().indexOf('/REF.html') == -1
			&& "/AGREENEMNT.html" != common.curURI()
			&& common.curURI().indexOf('/JOIN.html') == -1
			&& "/find.html" != common.curURI()
			&& "/findPartner.html" != common.curURI()
			&& "/FAQ.html" != common.curURI()
			&& common.curURI().indexOf('/FAQ_VIEW.html') == -1
			&& common.curURI().indexOf('/FAQ_VIEW_FORM.html') == -1
			&& common.curURI().indexOf('/index_nomfa.html') == -1
			&& "/LOGIN_STATUS.html" != common.curURI()) {
		common.init();
	} else if (common.curURI().indexOf('/FAQ') > -1){
		this.user = JSON.parse(sessionStorage.getItem("USER"));
		this.userVendor = JSON.parse(sessionStorage.getItem("USERVENDOR"));
		$(".user-info").html(common.user == null ? "" : common.user.USERNAME);
		if (this.user && this.user.USERTYPE == "VU") {
			$(".user-info.person").html(common.userVendor == null ? "" : common.userVendor.VENDOR_NAME);
		} else if (this.user) {
			$(".user-info.person").html(this.user.USERTYPEVALUE);
		}

		common.event();
		// setAuthEl();
	}

});


var common = {
	user: {},
	userVendor: {},
	menuName: "",
	queryString: {},
	init: function() {
		$that = this;
		this.setUserCheck();
		this.setQueryString();
		this.style();
		this.design();
		this.event();
		setAuthEl();

		this.user = JSON.parse(sessionStorage.getItem("USER"));
		this.userVendor = JSON.parse(sessionStorage.getItem("USERVENDOR"));
		$(".user-info").html(common.user == null ? "" : common.user.USERNAME);
		if (this.user && this.user.USERTYPE == "VU") {
			$(".user-info.person").html(common.userVendor == null ? "" : common.userVendor.VENDOR_NAME);
		} else if (this.user) {
			$(".user-info.person").html(this.user.USERTYPEVALUE);
		}
	},
	setUserCheck : function() {
        //if (sessionStorage.getItem("USER") == null) {
        	// var msg = '사용자 정보가 없습니다. 다시 로그인하세요.';
        	// location.href = '/REF.html?' +(encodeURIComponent(msg));
        	//location.href = '/error.html?M_NoUserInfo';
        //}
	},
	setQueryString : function() {
		var queryItemArr = window.location.search.substr(1).split("&");
		if (queryItemArr.length > 0) {
			$.each(queryItemArr, function(idx, item){
				var queryArr = item.split("=");
				if (queryArr.length > 1) {
					common.queryString[queryArr[0]] = queryArr[1];
				} else {
					common.queryString[queryArr[0]] = "";
				}
			});
		}
	},
	style: function(){
		$("head").append(
	            '<style>                        ' +
	            '    .modal-key-value{          ' +
	            '        border-color: #ff0000	' +
	            '       !important;				' +
	            '    }                          ' +
	            '	.modal-disabled{            ' +
	            '        background: #ccc !important;      ' +
	            '    }                          ' +
	            '    /* jqGrid IE Checkbox CSS */                          ' +
	            '   .ui-jqgrid td input, .ui-jqgrid td select, .ui-jqgrid td textarea {'+
	            '   margin : 0; padding-top: 0px; padding-bottom :0px; ' +
	            '    }                          ' +
	            ' .ui-jqgrid .ui-jqgrid-view input, .ui-jqgrid .ui-jqgrid-view select, .ui-jqgrid .ui-jqgrid-view textarea, .ui-jqgrid .ui-jqgrid-view button { '+
	            '     border-style: none;  ' +
	            '     background-color: transparent ' +
	            '  }  ' +
	            '</style>                  '
	        );
	},
	design: function() {

	},
	resize : {
		menuMain: function(){
			var height = $(window).height();
	      	height -= $('.w-nav.navi-con').height() + Number($(".chart-tt.box").css("margin-top").split("px")[0]) + 40;
	      	$('.content-box').css({height: height, 'min-height':680});
		},
		noGrid: function(){
			var height = $(window).height();
	      	height -= $('.w-nav.navi-con').height() + Number($(".chart-tt.box").css("margin-top").split("px")[0])+ $(".w-clearfix.h-1").height()
	  		+  Number($(".content-area").css("margin-top").split("px")[0]) + 20

	  		var minHeight = Number($('.content-box').css('min-height').split('px')[0])
	  		- ($(".w-clearfix.h-1").height() +  Number($(".content-area").css("margin-top").split("px")[0]) + 70);

	      	$('.detail-txt.view').height(height - $('.detail-view-h3').height() - $('.detail-viewbtn').height() -30);
	      	$('.detail-txt.view').css('min-height', minHeight - $('.detail-view-h3').height() - $('.detail-viewbtn').height() + 15);
	      	$(".content-area").height(height);
	  		$(".content-area").css('min-height', minHeight);
		},
		noSearchBox : function(){
			var height = $(window).height();
	      	height -= $('.w-nav.navi-con').height() + Number($(".chart-tt.box").css("margin-top").split("px")[0])+ $(".w-clearfix.h-1").height()
	  		+  Number($(".content-area").css("margin-top").split("px")[0]) + 32

	  		var minHeight = Number($('.content-box').css('min-height').split('px')[0])
	  		- ($(".w-clearfix.h-1").height() +  Number($(".content-area").css("margin-top").split("px")[0]) + 27);
	      	$(".content-area").height(height);
	  		$(".content-area .w-row.jqx-splitter").height(height);
	  		$(".content-area").css('min-height', minHeight);
	  		$(".content-area .w-row.jqx-splitter").css('min-height', minHeight);
	  		$('.measure-box').css('height','100%');
		},
		isSearchBox: function(){
			var height = $(window).height();
	      	//height -= $('.w-nav.navi-con').height() + Number($(".chart-tt.box").css("margin-top").split("px")[0])+ $(".w-clearfix.h-1").height()
	  		//+ $(".search-box").height() +  Number($(".content-area").css("margin-top").split("px")[0]) + 40
/*
	  		var minHeight = Number($('.content-box').css('min-height').split('px')[0])
	  		- ($(".w-clearfix.h-1").height() + $(".search-box").height() + Number($(".content-area").css("margin-top").split("px")[0]) + 32);
	      	$(".content-area").height(height);
	  		$(".content-area .w-row.jqx-splitter").height(height);
	  		$(".content-area").css('min-height', minHeight);
	  		$(".content-area .w-row.jqx-splitter").css('min-height', minHeight);
	  		$('.measure-box').css('height','100%');*/
		},
	},
	event: function(){
		$('body').click(function(e) {
            if (!$(e.target).is('#POPContact') && !$(e.target).is('#btnContact') && $(e.target).parents('#POPContact').length == 0 && $(e.target).parents('#btnContact').length == 0 ) {
                $("#POPContact").addClass('hidden');
            }
        });
		$("#POPContact .btn-layer-ok, #POPContact .btn-layer-close").click(function(){
			$("#POPContact").addClass("hidden");
		});
        $("#btnLogout").click(function(){
        	 sessionStorage.clear();
             localStorage.clear();
			location.href = "/index.html";
		});
        $('.w-nav-brand.brand-logo').click(function(){
        	sessionStorage.setItem("CURRMENU", "MY_MENU");
        	location.href = '/main.html';
        });
        $("#btnContact").click(function(){
        	$("#POPContact").removeClass('hidden');
        	$("#POPContact").offset({top: $("#btnContact").offset().top + 32, left: $("#btnContact").offset().left - 280});

        });
        //2016-08-29 태블릿 등 해상도가 낮은 디바이스를 위해 좌측 메뉴 버튼 기능 살림
        $("#open-btn").click(function() {
            if ($(".slidebar").hasClass("hidden")) {
            	$(".slidebar").removeClass("hidden");
            	$(".content").css("margin-left", "220px");
            } else {
            	$(".slidebar").addClass("hidden");
            	$(".content").css("margin-left", "5px");
            }
            //길이 초과시 말줄임 표시
            var menuWidth;
            var elWidth = $('.w-container').last().width();
            $(".menu-txt").css({
                "width": (elWidth - 15) + 'px',
                "overflow": "hidden",
                "text-overflow": "ellipsis",
                "white-space": "nowrap"
            });
            var menuWidth = elWidth - 20;
            $(".menu-txt-lvl2").css({
                "width": menuWidth  + 'px',
                "overflow": "hidden",
                "text-overflow": "ellipsis",
                "white-space": "nowrap"
            });
            $(".menu-txt-lvl2").next().css('margin-left','5px');
            $(".menu-txt-lvl3").css({
                "width": elWidth + 'px',
                "overflow": "hidden",
                "text-overflow": "ellipsis",
                "white-space": "nowrap"
            });
        });
	},
	click: function(el){
		$(document).on("keyup", el, function(){
        	var x = event.keyCode;
        	if(x == 13){
        		$("#btnSearch").click();
        	}
       	});
	},
	dynamicSort: function(property) {
	    return function (a,b) {
	    	if (a[property[0]] == null || b[property[0]] == null) {
	    		return (a[property[1]].toUpperCase() > b[property[1]].toUpperCase() ? -1 : 0);
	    	} else {
	    		return (a[property[0]].toUpperCase() < b[property[0]].toUpperCase()) ? -1 : (a[property[0]].toUpperCase() > b[property[0]].toUpperCase()) ? 1 : 0
	    				|| (a[property[0]].toUpperCase() == b[property[0]].toUpperCase() && a[property[1]].toUpperCase() > b[property[1]].toUpperCase());

	    	}
	    }
	},
	replaceMultiLangMsg: function(msg, replace){
//		var replaceMsg = msg;
//		msg.match(/{/g).length;
		for(var i = 0; i< replace.length; i++){
			msg = micaCommon.lang(msg).replace("{"+i+"}", micaCommon.lang(replace[i]));
		}
		return msg;
	},
	validation: function(postdata, griddata, key, keyMsg, overlapMsg){
		var keyValue = $(".modal-key-value");
		var flag = true;
		$.each(keyValue, function(index, value){
			if(postdata[$(value).attr('id')] == ""){
				var msg = "";
				if(keyMsg == null){
					msg = common.replaceMultiLangMsg('M_InputKeyValue', [$(value).attr('id')]);
				}else{
					msg = common.replaceMultiLangMsg('M_InputKeyValue', [keyMsg[index]]);
				}
				micaCommon.messageBox({type:"danger", width:"400", height: "145", html: msg});
				flag = false;
				return false;
			}
		});
		if(key != null && flag == true){
			if(typeof(key) == 'object'){
				var keyvalueList = [];
				for(var i = 0; i< key.length; i++){
					keyvalueList.push(postdata[key[i]]);
				}
				for(var i = 0; i< keyvalueList.length; i++){
					if(keyvalueList[i].indexOf(" ") != -1){
						micaCommon.messageBox({type:"danger", width:"400", height: "145", html:micaCommon.lang('M_NotAllowBlank')});
						flag = false;
						return false;
					}
				}
				$.map(griddata, function(value, index){
					var count = 0;
					$.map(keyvalueList, function(keyvalue, keyindex){
		    			if(value[key[keyindex]] == keyvalue){
		    				count++;
		    				if(count == key.length){
		    					var msg = "";
		    					if(overlapMsg == null){
		    						msg = common.replaceMultiLangMsg('M_NotAllowRepeat2', key);
		    					}else{
		    						msg = common.replaceMultiLangMsg('M_NotAllowRepeat2', overlapMsg);
		    					}
		    					micaCommon.messageBox({type:"danger", width:"400", height: "145", html: msg});
		        				flag = false;
		    				}
		    				return false;
		    			}
		    		});
	    		});
			}
			else{
				var keyvalue = postdata[key];
				if(keyvalue.indexOf(" ") != -1){
					micaCommon.messageBox({type:"danger", width:"400", height: "145", html:micaCommon.lang('M_NotAllowBlank')});
					flag = false;
					return false;
				}
				$.map(griddata, function(value, index){
	    			if(value[key] == keyvalue){
	    				if(overlapMsg == null){
    						msg = common.replaceMultiLangMsg('M_NotAllowRepeat', [key]);
    					}else{
    						msg = common.replaceMultiLangMsg('M_NotAllowRepeat', overlapMsg);
    					}
	    				micaCommon.messageBox({type:"danger", width:"400", height: "145", html: msg});
	    				flag = false;
	    			}
	    		});
			}
    	}
		if(flag == false){
			return [false];
		}else{
			return [true];
		}
	},
	curURI : function() {
		var urlArr = location.href.split("/");
		return "/" + urlArr.slice(3, urlArr.length).join("/");
	},
	ajax : function(url, data, options) {
        options = options || {};

        var method = options.method || "GET";
        var success = options.success || function (data) { console.log("Success") };
        var fail = options.fail || function (data) { console.error("Fail : " + data.EXCEPTIONMESSAGE); micaCommon.notication.open("Warning", "Fail : " + data.EXCEPTIONMESSAGE); };
        var error = options.error || function (data) { console.error("Error : 관리자에게 문의"); micaCommon.notication.open("Error", "Error : 관리자에게 문의"); };

        var successFn = function (data) {
            // if (data.ISSUCCESS) {
                success(data);
            // } else {
            //     fail(data);
            // }
        };

        if (null != data) {
        	var dataObj = typeof data == "string" ? JSON.parse(data) : data;
        	/*dataObj.USERID = common.user.USERID;
        	dataObj.SITEID = common.user.SITEID;*/ //
        	//todo_session sshan
        	dataObj.USERID = 'covision';
            dataObj.SITEID = 'DW01';
        }

        var errorFn = function (data) {
            error(data);
        };
        return ajaxService(data, url, successFn, method, errorFn, options);
	},
    fileDownload: function (fileName, errorFunc, type) {
        var error = errorFunc || function (data) { console.error("Error : 관리자에게 문의"); micaCommon.notication.open("Error", "Error : 관리자에게 문의"); };
        var attachType = typeof type == "undefined" || null == type ? "" : "&attachType=" + type;
        if (typeof micaConfig.downloadUrl != "undefined" && null != micaConfig.downloadUrl && "" != micaConfig.downloadUrl) {
            $.ajax({
                url: micaConfig.downloadUrl,
                type: "POST",
                data: "downloadFileChk=" + encodeURIComponent(fileName) + attachType,
                success: function (data) {
                    window.location.href = micaConfig.downloadUrl + "?downloadFile=" + encodeURIComponent(fileName) + attachType;
                },
                error: function (data) {
                    error(data);
                }
            });
        } else {
            console.error("openMes _ error : Download URL Not Found");
        }
    },
    leftMenu: {
    	set: function (el, key) {
            //el : element
            el = el == null ? $(".w-container").last() : el;
            el = typeof el == "string" ? $(el) : el;
            el.html("");

            key = key || "MENU";
            if (sessionStorage.getItem(key) == null) {
            	// var msg = '메뉴정보가 없습니다. 다시 로그인하세요.';
            	// location.href = '/REF.html?' +(encodeURIComponent(msg));
            	location.href = '/error.html?M_NoMenuInfo';
                // micaCommon.notication.open("error", "메뉴정보가 없습니다 다시 로그인하세요");
                // return;
            }

            var data = JSON.parse(sessionStorage.getItem(key));
            var source =
            {
                datatype: "json",
                localdata: data,
                id: 'id'
            };

            var dataAdapter = new $.jqx.dataAdapter(source);
            dataAdapter.dataBind();
            var records = dataAdapter.getRecordsHierarchy('MENU_ID', 'PARENT_MENU_ID', 'items', [{ name: 'MENUNAME', map: 'text' }, { name: 'VIEW_ID', map: 'HREF' }]);

            //데이터 정렬
            records = micaCommon.leftMenu.dataSort("SEQUENCE", records);

            //메뉴 생성
            /*
            if (common.curURI().indexOf("BOARD_VIEW.html") > -1 || common.curURI().indexOf("BOARD_VIEW_FORM.html") > -1) {
            	micaCommon.leftMenu.makeMenu($(el), records, "BOARD.html");
        	} else {
        		micaCommon.leftMenu.makeMenu($(el), records);
        	}
        	*/
            micaCommon.leftMenu.makeMenu($(el), records);

            var menuWidth;
            $(".menu-txt").css({
                "width": (el.width() - 15) + 'px',
                "overflow": "hidden",
                "text-overflow": "ellipsis",
                "white-space": "nowrap"
            });

            //var menuWidth = el.width() - parseInt($(".menu-txt-lvl2").css('padding-left').replace(/px/,'')) - parseInt($(".menu-txt-lvl2").css('margin-left').replace(/px/,'')) - $(".menu-txt-lvl2").prev().width()- $(".menu-txt-lvl2").next().width();
            //var menuWidth = (el.width() - 20) - $('.menu-txt-lvl2').offset().left - parseInt($(".menu-txt-lvl2").css('padding-left').replace(/px/,''));
            var menuWidth = (el.width() - 30);
            $(".menu-txt-lvl2").css({
                "width": menuWidth  + 'px',
                "overflow": "hidden",
                "text-overflow": "ellipsis",
                "white-space": "nowrap"
            });
            $(".menu-txt-lvl2").next().css('margin-left','5px');
            //menuWidth = (el.width() - 20) - parseInt($(".menu-txt-lvl3").css('padding-left').replace(/px/,'')) - parseInt($(".menu-txt-lvl3").css('margin-left').replace(/px/,''));
            $(".menu-txt-lvl3").css({
                "width": el.width() + 'px',
                "overflow": "hidden",
                "text-overflow": "ellipsis",
                "white-space": "nowrap"
            });

            $('.w-dropdown-toggle').css('padding-right','0px');
        }
    },
    grid : {
    	addForm: function (grid, url, options) {
    		if (!url) { return false;}
    		micaCommon.grid.ajaxSetup();
    		var gridOptions = options || {};
    		gridOptions.colModel = micaCommon.grid.get(grid, "getGridParam", "colModel");
    		gridOptions.urlRowIdFlag = false;
    		gridOptions.serializeEditData = common.grid.serializeEditData(gridOptions);
    		gridOptions.beforeViewForm = function (parentElem) {
    			var user = JSON.parse(sessionStorage.getItem("USER"));
    			$(parentElem).find("#SITE_ID").first().val(user.SITEID);
    			$(parentElem).find("#MODIFIER").first().val(user.USERID);
    			$(parentElem).find("#L_SiteId").first().val(user.SITEID);
    			$(parentElem).find("#L_Modifier").first().val(user.USERID);
    			// $("#MODIFY_TIME").val(sessionStorage.getItem("SITE_ID"));
    		}
    		// gridOptions = this.langFormOptions(gridOptions);
    		micaCommon.grid.addForm(grid, url, "POST", gridOptions);
    	},
    	editForm: function (grid, url, options) {
    		if (!url) { return false;}
    		micaCommon.grid.ajaxSetup();
    		var gridOptions = options || {};
    		gridOptions.colModel = micaCommon.grid.get(grid, "getGridParam", "colModel");
    		gridOptions.urlRowIdFlag = false;
    		gridOptions.serializeEditData = common.grid.serializeEditData(gridOptions);
    		// gridOptions = this.langFormOptions(gridOptions);
    		micaCommon.grid.editForm(grid, url, "PUT", gridOptions);
    	},
    	delForm: function (grid, url, options) {
    		if (!url) { return false;}
    		micaCommon.grid.ajaxSetup();
    		var gridOptions = options || {};
    		var dataArr = [];
    		var colDataArr = micaCommon.grid.get.selData("#gridTable");
    		if (null != colDataArr || colDataArr.length > 0){
    			var data = {};
    			$.each(colDataArr, function(idx, item) {
    				var data = {};
    				$.each(item, function (name, value) {
    					data[name] = value;
    				});
    				dataArr.push(data);
    			});
    			url = (url.endsWith("/") ? url : url + "/") + common.encParam(dataArr);
    		}
    		gridOptions.urlRowIdFlag = false;
    		// gridOptions.serializeDelData = common.grid.serializeEditData(gridOptions);
    		// gridOptions = this.langFormOptions(gridOptions);
    		micaCommon.grid.delForm(grid, url, "DELETE", gridOptions);
    	},
    	serializeEditData : function (options) {
    		options = options || {};
    		return function (postdata) {
    			var result = {};

    			$.each(options.colModel, function (i, v) {
    				$.each(postdata, function (j, vv) {
    					if (j == v.name) {
    						if (v.formatter == "number" || v.formatter == "integer") {
    							if (vv != null) {
    								postdata[j] = Number(vv);
    							}
    						}
    					}
    				});
    			});

    			$.each(postdata, function (name, value) {
    				result[name] = value;
    			});

    			postdata = JSON.stringify(postdata);
    			return postdata;
    		}
    	},
        setKeyValue : function(els){
        	if(typeof(els) == 'object'){
        		$.each(els, function(index, value){
        			 $(els).removeClass("modal-disabled");
        			 $(value).addClass("modal-key-value");
        		     $(value).attr("disabled", false);
        		});
        	}else if(typeof(els) == 'string'){
        	   $(els).removeClass("modal-disabled");
    		   $(els).addClass("modal-key-value");
    	       $(els).attr("disabled", false);
        	}
        },
        setDisabledValue: function(els){
        	if(typeof(els) == 'object'){
        		$.each(els, function(index, value){
        			 $(els).removeClass("modal-key-value");
        			 $(value).addClass("modal-disabled");
        		     $(value).attr("disabled", true);
        		});
        	}else if(typeof(els) == 'string'){
        	   $(els).removeClass("modal-key-value");
    		   $(els).addClass("modal-disabled");
    	       $(els).attr("disabled", true);
        	}
        }
    },
    encParam : function(value) {
    	value = value || {};
    	if (typeof value == "string") {
    		return encodeURIComponent(value);
    	} else {
    		return encodeURIComponent(JSON.stringify(value));
    	}
    },
	vendorGridSet: function(el, data, type){
		  $(el).html("");
	      $(el).append($("<table id='popGridTable'/>"));

	      var type = type || {};
	      var gridOptions = {
	          height : $(el).height() - 25,
	          width : $(el).width(),
	//          rowNum : 30,
	          scroll : true,
	//          multiselect: true,
	          colNames:['L_VendorId','L_Name', 'L_HomeAddress'],
	          colModel : [
	              { label : 'L_VendorId', 		name:'ID'},
	              { label : 'L_Name', 			name:'NAME'},
	              { label : 'L_HomeAddress', 	name:'ADDRESS'}
	          ],
	  		   ondblClickRow : function(rowid, iRow, iCol, e){
	         	  $(el).parents('.layer-popup').find(".btn-layer-ok").click();
	           }
	      };
	      gridOptions.rowNum = type.rowNum || 30;
	      if(type.multiselect == false){
	    	  gridOptions.multiselect = false;
	      }else{
	    	  gridOptions.multiselect = true;
	    	  gridOptions.beforeSelectRow = function(rowid, e) {
	        	return $(e.target).is('input[type=checkbox]');
			  };
			  gridOptions.ondblClickRow  = function(rowid, iRow, iCol, e){
	  			  $(e.target).parent().find(':checkbox').prop('checked', true).click();
	  			  $(el).parents('.layer-popup').find(".btn-layer-ok").click();
			  }
	      }
	      var options = {
	          url : '/api/join/searchVendorGrid/' + encodeURIComponent(JSON.stringify(data)),
	          mtype : "get",
	          rowsName : "list"
	      };
	      micaCommon.grid.set("#popGridTable", gridOptions, options);
	},
	vendorSelectSet: function(el, input){
		var vendorData = micaCommon.grid.get.selData("#popGridTable");
		if(vendorData.length == 0){
			micaCommon.messageBox({type:"danger", width:"400", height: "145", html:micaCommon.lang("M_SelectVendor")});
			$(".micaMsgBoxSet").css('z-index','1053');
			$(".micaMsgBoxSet ").next('.mm-backdrop').css('z-index','1052');
			return false;
		}
		var vendorList = "";
		var vendorCodeList = [];
		for(var i = 0; i < vendorData.length; i++){
			vendorList += vendorData[i].NAME
			if(i != vendorData.length - 1){
				vendorList += ', ';
			}
			vendorCodeList.push(vendorData[i].ID);
		}
		input.val(vendorList).change();
		if(vendorCodeList.length == 1){
			input.attr('data-code', vendorCodeList);
		}else{
			input.attr('data-code', '['+vendorCodeList+']');
		}
		$(el).addClass('hidden');
	},
	favorite: {
		menuInfo: {},
		iconTrue : function(menuId) {
			return "<a href='javascript:common.favorite.remFav(\"" + menuId + "\");' class='w-clearfix w-inline-block h1-icon-linkselected' data-menuid='" + menuId + "'><div class='w-icon fa fa-star h1-icon'></div></a>";
		},
		iconFalse : function(menuId) {
			return "<a href='javascript:common.favorite.addFav(\"" + menuId + "\");' class='w-clearfix w-inline-block h1-icon-link2' data-menuid='" + menuId + "'><div class='w-icon fa fa-star h1-icon'></div></a>";
		},
		setTrue : function(menuId) {
			$(".content-box div").first().append(common.favorite.iconTrue(menuId));
		},
		setFalse : function(menuId) {
			$(".content-box div").first().append(common.favorite.iconFalse(menuId));
		},
		addFav : function(el, menuId) {
			var options = {
				method : "POST",
				success : function(data) {
					if (data.isSuccess) {
						$(".content-box div").first().find(".w-clearfix.w-inline-block").remove();
						common.favorite.setTrue(menuId);
					}
				}
			};
			common.ajax("/api/userFavoriteMenu/", {MENU_ID : openMes.page.getCurrentPageInfo(sessionStorage.getItem("CURRMENU")).MENU_ID}, options);
		},
		remFav : function(el, menuId) {
    		var options = {
				method : "DELETE",
				success : function(data) {
					if (data.isSuccess) {
						$(".content-box div").first().find(".w-clearfix.w-inline-block").remove();
						common.favorite.setFalse(menuId);
					}
				}
    		};
    		common.ajax("/api/userFavoriteMenu/deleteUserFavoriteMenu/" +openMes.page.getCurrentPageInfo(sessionStorage.getItem("CURRMENU")).MENU_ID, null, options);
		}
	},
	getBrowserName : function() {
		var agt = navigator.userAgent.toLowerCase();
		if (agt.indexOf("chrome") != -1) return 'Chrome';
		if (agt.indexOf("opera") != -1) return 'Opera';
		if (agt.indexOf("staroffice") != -1) return 'Star Office';
		if (agt.indexOf("webtv") != -1) return 'WebTV';
		if (agt.indexOf("beonex") != -1) return 'Beonex';
		if (agt.indexOf("chimera") != -1) return 'Chimera';
		if (agt.indexOf("netpositive") != -1) return 'NetPositive';
		if (agt.indexOf("phoenix") != -1) return 'Phoenix';
		if (agt.indexOf("firefox") != -1) return 'Firefox';
		if (agt.indexOf("safari") != -1) return 'Safari';
		if (agt.indexOf("skipstone") != -1) return 'SkipStone';
		if (agt.indexOf("msie") != -1) return 'Internet Explorer';
		if (agt.indexOf("netscape") != -1) return 'Netscape';
		if (agt.indexOf("mozilla/5.0") != -1) return 'Mozilla';
	}
}

var Base64 = {
	_keyStr: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",
	encode: function(input) {
		var output = "";
		var chr1, chr2, chr3;
		var enc1, enc2, enc3, enc4;
		var i = 0;

		input = Base64._utf8_encode(input);

		while (i < input.length) {
			chr1 = input.charCodeAt(i++);
			chr2 = input.charCodeAt(i++);
			chr3 = input.charCodeAt(i++);

			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;

			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}

			output = output + this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) + this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);
		}

		return output;
	},
	decode: function(input) {
		var output = "";
		var chr1, chr2, chr3;
		var enc1, enc2, enc3, enc4;
		var i = 0;

		input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

		while (i < input.length) {
			enc1 = this._keyStr.indexOf(input.charAt(i++));
			enc2 = this._keyStr.indexOf(input.charAt(i++));
			enc3 = this._keyStr.indexOf(input.charAt(i++));
			enc4 = this._keyStr.indexOf(input.charAt(i++));

			chr1 = (enc1 << 2) | (enc2 >> 4);
			chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
			chr3 = ((enc3 & 3) << 6) | enc4;

			output = output + String.fromCharCode(chr1);

			if (enc3 != 64) {
				output = output + String.fromCharCode(chr2);
			}
			if (enc4 != 64) {
				output = output + String.fromCharCode(chr3);
			}
		}

		output = Base64._utf8_decode(output);

		return output;
	},
	_utf8_encode: function(string) {
		string = string.replace(/\r\n/g, "\n");
		var utftext = "";

		for (var n = 0; n < string.length; n++) {
			var c = string.charCodeAt(n);

			if (c < 128) {
				utftext += String.fromCharCode(c);
			} else if ((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			} else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}
		}

		return utftext;
	},
	_utf8_decode: function(utftext) {
		var string = "";
		var i = 0;
		var c = c1 = c2 = 0;

		while (i < utftext.length) {
			c = utftext.charCodeAt(i);

			if (c < 128) {
				string += String.fromCharCode(c);
				i++;
			} else if ((c > 191) && (c < 224)) {
				c2 = utftext.charCodeAt(i + 1);
				string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
				i += 2;
			} else {
				c2 = utftext.charCodeAt(i + 1);
				c3 = utftext.charCodeAt(i + 2);
				string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
				i += 3;
			}
		}

		return string;
	}
}

function setAuthEl() {
    if (sessionStorage.OBJECT) {
        var menuId = openMes.page.getCurrentPageInfo("MENU", "");
        if (!menuId) {
            return;
        }
        var data = JSON.parse(sessionStorage.getItem("OBJECT"));
        data = data[menuId.MENU_ID];

        if (data) {
            $.each(data, function(i, e){
                if ('N' == data[i].ACTION) {
                	$(data[i].OBJECT_ID).addClass('hidden');
                } else if ('C' == data[i].ACTION) {
                    $(data[i].OBJECT_ID).css("opacity", openMes.authOpacity);
                    $(data[i].OBJECT_ID).css("pointer-events", "none");
                } else if("Y" == data[i].ACTION){
                	$(data[i].OBJECT_ID).removeClass('hidden');
                }
            })
        }
    }
}