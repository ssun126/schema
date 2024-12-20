$(function () {
   auditNcr.init();
});

var txtVendor;
var txtMaterial;
var vendorVal = null;
var materialVal = null;

var auditNcr = {
    init: function () {
        $that = this;
        $that.style();
        $that.design();
        $that.event();
        $that.auditNcrListEvent();
        $that.getCombobox();
    	$("#btnSearch").click();
    	//auditNcr.setSrchPlant();

    },
    style : function(){
    	$("#txtLYName").attr('required', false);
    	$("#txtLYVendorName").attr('required', false);
    	$("#txtLYMaterial").attr('required', false);
    	$(".layer-popup").addClass("hidden");
    	$("head").append(
			'<style>                                                        	'+
			'	.txt-detailview, #popCoa label{                                          		'+
			'    	text-overflow:ellipsis;white-space:nowrap;word-wrap:normal;overflow:hidden  '+
			'	}                                                               '+
			'</style>															'
		);
    	$(".panel.w-350").width("100px");
    	//로딩바 위치 지정
    	$("#loadingArea #loading-text").css("left",$(window).width()/2-10);
    	$("#loadingArea #loading-text").css("top",($(window).height()/2)-60);
    	$("#loadingArea .loading").css("left",$(window).width()/2-10);
    	$("#loadingArea .loading").css("top",($(window).height()/2)-60);
    	/*$("#loadingArea .loading").css()*/
    	//$(window).width(), $(window).height()

    },
    design: function(){
    	$(".grid-box.h-600").height(300);

    	$(".layer-pop-con .layer-footer").css("margin-top", 0);
/*//todo_session sshan
    	var user = JSON.parse(sessionStorage.getItem("USER"));
		if (user.USERTYPE == "VU") {
			var userVendor = JSON.parse(sessionStorage.getItem("USERVENDOR"));
		}*/
    },
    event: function () {
    	//소속회사 세팅
    	/*  //todo_session sshan
    	var user = JSON.parse(sessionStorage.getItem("USER"));
		var userVendor = JSON.parse(sessionStorage.getItem("USERVENDOR"));
		var userType = user["USERTYPE"];
		if(userVendor&&userVendor["VENDOR_CODE"]){
			var vendorCodeAry = String(userVendor["VENDOR_CODE"]).split(",");
			var vendorNameAry = String(userVendor["VENDOR_NAME"]).split(",");
			var localData = new Array();
			//소속회사 조회 기본 값 세팅
			$("#searchAuditEntName").attr('data-code', vendorCodeAry[0]);
			$("#searchAuditEntName").val(vendorNameAry[0])
			var tempData = new Object();
		}
    	$(window).resize(function () {
    		common.resize.isSearchBox();
        }).trigger('resize');
*/
    	$(document).on("resize", ".layer-pop-con.ui-resizable", function(){
    		var height = $(this).height();
    		var gridBox  = $(this).find(".grid-box")
    		height -= $(this).find(".w-clearfix.layer-header").height();
    		height -= $(this).find(".layer-if").height();
    		height -= $(this).find(".layer-footer").height();
    		height -= 20;
    		gridBox.height(height);
    		if($(this).find("table.ui-jqgrid-btable").length > 0){
    			$(this).find("table.ui-jqgrid-btable").setGridWidth(gridBox.width());
    			$(this).find("table.ui-jqgrid-btable").setGridHeight(gridBox.height()-50);
    		}
    	});
    	$(".layer-popup .layer-pop-con").resizable({
    		minWidth: 300,
    		minHeight: 270
    	});

    	$('body').click(function(e) {
    	    if (!$(e.target).is('.layer-popup') && !$(e.target).is('.w-inline-block.btn-xs-search') && $(e.target).parents('.layer-popup').length == 0
    	    		&& $(e.target).parents('.w-inline-block.btn-xs-search').length == 0
    	    		&& $(e.target).parents('.micaMsgBoxSet').length == 0 && !$(e.target).is('.micaMsgBoxSet')
    	    		&& !$(e.target).is('#POPContact') && !$(e.target).is('#btnContact') && $(e.target).parents('#POPContact').length == 0 && $(e.target).parents('#btnContact').length == 0 ){
    	    	$(".layer-popup").addClass('hidden');
    	    }
    	});
    	$("#tabBtn1").click(function () {
    		auditNcr.tab.btnClick("tabBtn1","1");
 		});
    	$("#tabBtn2").click(function () {
    		auditNcr.tab.btnClick("tabBtn2","2");
 		});
    	$("#tabBtn3").click(function () {
    		auditNcr.tab.btnClick("tabBtn3","3");
 		});
    	$("#btnPopAuditNcrPlanSave").click(function () {
    		var msgOption = {
				type:"info",
				width:"400",
				height: "145",
				html: micaCommon.lang("M_ConfirmRegist"),
				okButton : {
					after : function() {
						var popPcnCompanyLabel="";
						var popPcnCompanyValue="";
			    		var reqParam = {
			    			typ :"I",
			    			dif : $("#popAuditNcrNcrDif").val(),
			    			ncr_seq : $("#popAuditNcrNcrSeq").val(),
		    				ncr_p_plan_date : $("#popAuditNcrTabPlanDate").val(),
		    				ncr_p_make_empcd : $("#popAuditNcrTabPlanEmpcd").val(),
		    				ncr_p_make_empnm : $("#popAuditNcrTabPlanEmpnm").text(),
		    				ncr_p_email : $("#popAuditNcrTabPlanEmail").text(),
		    				ncr_p_comment : $("#popAuditNcrTabPlanComment").val(),
		    				vendor_nm : $("#popAuditNcrPartnersNm").text(),
		    				plant : $("#popAuditNcrPlant").val()
		    			};
		    			var options = {
		    				method : "POST",
		    				success: function (data) {
		    					var rowData = new Object();
		    					rowData.NCR_Seq = $("#popAuditNcrNcrSeq").val();
		    					auditNcr.popControl.popGetData(rowData);
		    					$("#btnSearch").click();
		    					auditNcr.successMessageBox(micaCommon.lang("M_DbSuccessMsg"));
		    				}
		    			};
		    			common.ajax("/api/audit/auditNcrMgmt/auditNcrPlanInsert/", reqParam, options);
					}
				}
			}

	    	micaCommon.messageBox(msgOption);
	    	$('.micaMsgBoxSet').css('z-index', '1000000');
			$(".mm-backdrop.fade.in").css('z-index', '999999');
 		});
    	$("#btnPopAuditNcrResultSave").click(function () {
    		var ary = auditNcr.multiFileInput.getNameList("popAuditResultFileArea");
    		var jsonAry = JSON.stringify(ary);
    		var msgOption = {
				type:"info",
				width:"400",
				height: "145",
				html: micaCommon.lang("M_ConfirmRegist"),
				okButton : {
					after : function() {
						var popPcnCompanyLabel="";
						var popPcnCompanyValue="";
			    		var reqParam = {
			    			typ :"I",
			    			dif : $("#popAuditNcrNcrDif").val(),
			    			ncr_seq : $("#popAuditNcrNcrSeq").val(),
		    				ncr_r_complete_date : $("#popAuditNcrTabResultCompleteDate").val(),
		    				ncr_r_comment : $("#popAuditNcrTabResultComment").val(),
		    				plant : $("#popAuditNcrPlant").val(),
		    				vendor_nm : $("#popAuditNcrPartnersNm").text(),
		    				file_info : jsonAry
		    			};
		    			var options = {
		    				method : "POST",
		    				success: function (data) {
		    					var rowData = new Object();
		    					rowData.NCR_Seq = $("#popAuditNcrNcrSeq").val();
		    					auditNcr.popControl.popGetData(rowData);
		    					$("#btnSearch").click();
		    					auditNcr.successMessageBox(micaCommon.lang("M_DbSuccessMsg"));
		    				}
		    			};
		    			common.ajax("/api/audit/auditNcrMgmt/auditNcrResultInsert/", reqParam, options);
					}
				}
			}

	    	micaCommon.messageBox(msgOption);
	    	$('.micaMsgBoxSet').css('z-index', '1000000');
			$(".mm-backdrop.fade.in").css('z-index', '999999');
 		});
    	$("#btnPopAuditNcrPlanComplete").click(function () {
    		var msgOption = {
				type:"info",
				width:"400",
				height: "145",
				html: micaCommon.lang("M_ConfirmRegist"),
				okButton : {
					after : function() {
						var popPcnCompanyLabel="";
						var popPcnCompanyValue="";
			    		var reqParam = {
		    				typ :"U",
		    				dif : $("#popAuditNcrNcrDif").val(),
			    			ncr_seq : $("#popAuditNcrNcrSeq").val(),
		    				ncr_p_plan_date : $("#popAuditNcrTabPlanDate").val(),
		    				ncr_p_make_empcd : $("#popAuditNcrTabPlanEmpcd").val(),
		    				ncr_p_make_empnm : $("#popAuditNcrTabPlanEmpnm").text(),
		    				ncr_p_email : $("#popAuditNcrTabPlanEmail").text(),
		    				ncr_p_comment : $("#popAuditNcrTabPlanComment").val(),
		    				vendor_nm : $("#popAuditNcrPartnersNm").text(),
		    				plant : $("#popAuditNcrPlant").val(),
		    				NCR_P_Make_Date : $("#popAuditNcrTabPlanMakeDate").text()
		    			};
		    			var options = {
		    				method : "POST",
		    				success: function (data) {
		    					var rowData = new Object();
		    					rowData.NCR_Seq = $("#popAuditNcrNcrSeq").val();
		    					auditNcr.popControl.popGetData(rowData);
		    					$("#btnSearch").click();
		    					auditNcr.successMessageBox(micaCommon.lang("M_DbSuccessMsg"));
		    				}
		    			};
		    			common.ajax("/api/audit/auditNcrMgmt/auditNcrPlanInsert/", reqParam, options);
					}
				}
			}

	    	micaCommon.messageBox(msgOption);
	    	$('.micaMsgBoxSet').css('z-index', '1000000');
			$(".mm-backdrop.fade.in").css('z-index', '999999');
 		});
    	$("#btnPopAuditNcrResultComplete").click(function () {
    		var ary = auditNcr.multiFileInput.getNameList("popAuditResultFileArea");
    		var jsonAry = JSON.stringify(ary);
    		var msgOption = {
				type:"info",
				width:"400",
				height: "145",
				html: micaCommon.lang("M_ConfirmRegist"),
				okButton : {
					after : function() {
						$('.panel-footer [id*=btnPop]').hide();
						$('#popProgressRegister').show();
						var popPcnCompanyLabel="";
						var popPcnCompanyValue="";
			    		var reqParam = {
		    				typ :"U",
			    			dif : $("#popAuditNcrNcrDif").val(),
			    			ncr_seq : $("#popAuditNcrNcrSeq").val(),
		    				ncr_r_complete_date : $("#popAuditNcrTabResultCompleteDate").val(),
		    				ncr_r_comment : $("#popAuditNcrTabResultComment").val(),
		    				vendor_nm : $("#popAuditNcrPartnersNm").text(),
		    				plant : $("#popAuditNcrPlant").val(),
		    				file_info : jsonAry
			    		}
		    			var options = {
		    				method : "POST",
		    				success: function (data) {
		    					var rowData = new Object();
		    					rowData.NCR_Seq = $("#popAuditNcrNcrSeq").val();
		    					auditNcr.popControl.popGetData(rowData);
		    					$("#btnSearch").click();
		    					auditNcr.successMessageBox(micaCommon.lang("M_DbSuccessMsg"));
		    				}
		    			};
		    			common.ajax("/api/audit/auditNcrMgmt/auditNcrResultInsert/", reqParam, options);
					}
				}
			}

	    	micaCommon.messageBox(msgOption);
	    	$('.micaMsgBoxSet').css('z-index', '1000000');
			$(".mm-backdrop.fade.in").css('z-index', '999999');
 		});
    	//조회 버튼
    	$("#btnSearch").click(function () {
    		var data = {
    			ncr_no : $("#searchAuditNcrNo").val(),
    			type : $("#searchAuditNcrAuditType").val(),
    			partnerscd : $("#searchAuditEntName").attr("data-code"),
    			ncr_type : $("#searchAuditNcrNcrType").val(),
    			year : $("#searchAuditNcrNcrYear").val(),
    			/*ncr_subject : $("#searchAuditNcrTitle").val(),*/
    			ncr_status : $("#searchAuditNcrNcrStatus").val()
    		};
			auditNcr.gridSet(data);
        });


    	$("#btnCompanySearch").click(function(){
    		$("#gridPOPVendor").html("");
    		txtVendor = $("#searchAuditEntName");
    		$("#txtLYVendorCode").val("");
    		$("#txtLYVendorName").val("");
    		$(".layer-popup").not('#POPVendor').addClass("hidden");
    		$("#POPVendor").css({top: $("#searchAuditEntName").offset().top + $("#searchAuditEntName").outerHeight()+5, left :$("#searchAuditEntName").offset().left - 40});
    		if($("#POPVendor").is('.hidden') == true){
				$("#POPVendor").removeClass('hidden');
			}else{
				$("#POPVendor").addClass('hidden');
			}
    	});

    	$(".btn-layer-cancel, .btn-layer-close").click(function(){
    		$(".layer-popup").addClass("hidden");
    	});

    	$(".modal .btn-layer-cancel, .modal .btn-layer-close").click(function(){
    		$(".modal").micaModal('hide');
    	});

    },
    auditNcrListEvent: function(){
    	$("#POPVendor .btn-layer-srch").click(function(){
			var data = {};
			data.vendorId = $("#txtLYVendorCode").val();
			data.name = $("#txtLYVendorName").val();

			var extOption = {
				rowsName : "LIST",
				colModel : [
					{name:"L_VendorCode", align:"center"},
					{name:"L_VendorName", align:"left"}
				]
			};

			auditNcr.layerPopupGridSet('gridPOPVendor', data, '/api/common/vendorSimpleList/' + common.encParam(data), extOption, "#POPVendor");
		});
    	$("#POPVendor .btn-layer-ok").click(function(){
			var vendorData = micaCommon.grid.get.selData("#gridPOPVendorTable")[0];
			if(vendorData == null){
				coa.messageBox(micaCommon.lang("M_SelectVendor"));
				return false;
			}
			$("#searchAuditEntName").attr('data-code', vendorData.L_VendorCode);
			$("#searchAuditEntName").val(vendorData.L_VendorName);
			try { txtVendor.change(); } catch (e) {}
			$("#POPVendor").addClass('hidden');

			auditNcr.layerPopupGridSet('gridPOPVendor', vendorData);
		});
		//선택삭제
    	$("#btnDelete").click(function(){
    		var data = micaCommon.grid.get.selData("#gridTable");
			if (data.length < 1) {
				auditNcr.messageBox(micaCommon.lang("M_NotSelectedRow"));
				return false;
			}

			var confirmData = true;
			$.each(data, function(idx, item) {
				if (item.status != 'A') {
					confirmData = false;
					return false;
				}
			});

			if (!confirmData) {
				auditNcr.messageBox(micaCommon.lang("M_NotDeleteItem"));
				return false;
			}
			for(var i=0; i<data.length; i++){
				var reqParam = {
					pcn_no : data[i].PCN_NO,
					pcn_seq : data[i].pcn_seq
				};
				var options = {
					method : "POST",
					success: function (data) {
						$("#btnSearch").click();
						auditNcr.messageBox(micaCommon.lang("M_SuccessDelete"));
					}
				};
				common.ajax("/api/coa/pcnmgmt/pcnDel/", reqParam, options);
			}

    	});
    	$("#btnPopAuditPlan").click(function(){
    		$(".modal").micaModal('hide');
			$("#popAuditPlan").removeClass('hidden');
			auditNcr.lodingBar.hide();$("#popAuditPlan").micaModal('show');
    	});
    },
    messageBox : function(msg){
    	micaCommon.messageBox({type:"danger", width:"400", height: "145", html: msg});
    	$('.micaMsgBoxSet').css('z-index', '1000000');
		$(".mm-backdrop.fade.in").css('z-index', '999999');
    },
    successMessageBox : function(msg){
    	micaCommon.messageBox({type:"success", width:"400", height: "145", html: msg});
    	$('.micaMsgBoxSet').css('z-index', '1000000');
		$(".mm-backdrop.fade.in").css('z-index', '999999');
    },
    getCombobox: function(){
    	//진행상태
    	var reqParam = {
				pFlag : "PARTNER_AUDIT_NCRSTA",
				pGroupCd : "9999"
		};
		var options = {
			method : "POST",
			success: function (data) {
				var localData = data.dataList;
				var comboOptions = { checkboxes: false, autoDropDownHeight : true, selectedIndex: 0 };
		        var options = {
		        	local: localData,
		            textName: "name", // DISPLAY_VALUE
		            valueName: "code",
		        }
		        micaCommon.comboBox.set("#searchAuditNcrNcrStatus", comboOptions, options);
			}
		};
		common.ajax("/api/audit/auditNcrMgmt/getCodeList", reqParam, options);
    	//Audit구분
    	var reqParam = {
				pFlag : "PARTNER_AUDIT_TYPE",
				pGroupCd : "9999"
		};
		var options = {
			method : "POST",
			success: function (data) {
				var localData = data.dataList;
				var comboOptions = { checkboxes: false, autoDropDownHeight : true, selectedIndex: 0 };
		        var options = {
		        	local: localData,
		            textName: "name", // DISPLAY_VALUE
		            valueName: "code",
		        }
		        micaCommon.comboBox.set("#searchAuditNcrAuditType", comboOptions, options);
			}
		};
		common.ajax("/api/audit/auditNcrMgmt/getCodeList", reqParam, options);
		//지적구분
    	var reqParam = {
				pFlag : "PARTNER_AUDIT_NCRTYP",
				pGroupCd : "9999"
		};
		var options = {
			method : "POST",
			success: function (data) {
				var localData = data.dataList;
				var comboOptions = { checkboxes: false, autoDropDownHeight : true, selectedIndex: 0 };
		        var options = {
		        	local: localData,
		            textName: "name", // DISPLAY_VALUE
		            valueName: "code",
		        }
		        micaCommon.comboBox.set("#searchAuditNcrNcrType", comboOptions, options);
			}
		};
		common.ajax("/api/audit/auditNcrMgmt/getCodeList", reqParam, options);
		//년
		var localData = new Array();
		var currentYear = new Date().getFullYear();
		var currentMonth = new Date().getMonth();
		if(3>Number(currentMonth)){
			currentYear = (Number(currentYear) -1)+"";
		}
		for(var i=Number(currentYear); i>=2018; i--){
			var tempObj = new Object();
			tempObj["NAME"] =i+"";
			tempObj["ID"] =i+"";
			localData.push(tempObj);
		}

		var comboOptions = { checkboxes: false, autoDropDownHeight : true, selectedIndex: 0 };
        var options = {
        	local: localData,
            textName: "NAME", // DISPLAY_VALUE
            valueName: "ID",
        }
        micaCommon.comboBox.set("#searchAuditNcrNcrYear", comboOptions, options);
    },
	gridSet: function (data) {
		$("#grid").html("");
		$("#grid").append($("<table id='gridTable'/>"));
		$("#grid").append($("<div id='gridPager'/>"));

		var checked;
		var gridOptions = {
			pager: "#gridPager",
			height: $("#grid").height() + 500 - 58,
			width: $("#grid").width(),
			rowNum: 30,
			multiselect : false,
			gridview: false,
			colModel : [
			    { name : 'L_NcrNo', align:'center', width:10,
			    	formatter : function(cellValue,rowObject,options){
			    		//var jsonData = JSON.stringify(options);
			    		var param = new Object();
			    		param['ncr_seq'] = options.NCR_Seq;
			    		var jsonData = JSON.stringify(param);
			    		console.log( jsonData)
			    		return "<div class='w-icon fa fa-search ico-clear'><a style='margin-left:5px;' href='#' onclick='auditNcr.popControl.pop("+jsonData+")'>"+cellValue+"</a></div>";
			    	}
			    },
				{ name : 'L_Progress', align:'center', width:10},
				{ name : 'L_AuditPlanType', align:'center', width:10},
				{ name : 'L_NcrType', align:'center', width:10},
				{ name : 'L_Title', align:'center', width:10},
				{ name : 'L_NcrPublishedDate', align:'center', width:10},
				{ name : 'Auditor', align:'center', width:10},
				{ name : 'L_CorrectiveActionPlanDate', align:'center', width:10},
				{ name : 'L_CorrectiveActionMakeDate', align:'center', width:10},
				{ name : 'L_CompleteDate', align:'center', width:10},
			],
			beforeSelectRow: function(rowid, e) {
				return $(e.target).is('input[type=checkbox]');
			}
		}

		var options = {
    		url: "/api/audit/auditNcrMgmt/list/" + common.encParam(data),
		    mtype: "get",
		    rowsName: "LIST"
		}

		var callback = {
		    after: function () {
		        $(window).resize(function () {
		            $("#gridTable").setGridWidth($("#grid").width(), true);
		            $("#gridTable").setGridHeight($(".measure-box").height() - $(".w-clearfix.h-3").height() - 50);
		        }).trigger('resize');
		    }
		}
		micaCommon.grid.set("#gridTable", gridOptions, options, callback);
    },
    modalKey : {},
    layerPopupGridSet:function(el, data, url, extOption, el2){
    	var url = url ||  '/data/vendor.json';
    	$("#" + el ).html("");
    	$("#" + el ).append($("<table id='"+ el + "Table'/>"));

    	extOption = extOption || {};

    	var gridOptions = {
    		height: $("#" + el ).height() - 30,
    		width: $("#" + el ).width(),
    		scroll: true,
    		colModel : extOption.colModel || null,
            ondblClickRow : function(rowid, iRow, iCol, e){
            	$(el2 +" .btn-layer-ok").click();
            }
    	}

		var options = {
    		url: url,
		    mtype: "get",
		    data: data,
		    rowsName : extOption.rowsName || "list",
		}

		micaCommon.grid.set("#" + el + "Table", gridOptions, options);
    },
    setSrchPlant: function(el){

		var data = {sqmYn : "Y"};

    	var comboOptions = {
			checkboxes: false,
			selectedIndex: 0
		};

    	var options = {
    		url: '/api/common/factoryList/' + common.encParam(data),
    		mtype:"GET",
    		rowsName:"LIST",
    		textName: "NAME",
    		valueName: "ID",
    		allText : "ALL"
    	};

    	micaCommon.comboBox.set("#searchPcnPlant", comboOptions, options)
    },
    multiFileInput: {
    	multiFileOption: {},
    	multiFileElementId:{},
    	set: function(multiFileId,fileInfoAry){
    		$("#"+multiFileId).append("<div class='multiFileArea multifilearea'></div>");
    		$("#"+multiFileId+" .multiFileArea").append("<div class='multiFileButton'>");
    		$("#"+multiFileId+" .multiFileArea").append("<button class='btn pop-btn fa fa-plus' type='button' style='height:22px; background-color:#ddd; color:#333;' onClick='javascript:auditNcr.multiFileInput.add(\""+multiFileId+"_multiFileData\")'> 파일추가</button>");
    		$("#"+multiFileId+" .multiFileArea").append("</div>");
    		$("#"+multiFileId+" .multiFileArea").append("<div id='"+multiFileId+"_multiFileData' class='multifilearea-multifiledata'></div>");

    		if(fileInfoAry && fileInfoAry.length > 0){
    			for(var i=0; i<fileInfoAry.length; i++){
	    			if(fileInfoAry[i].attach_filename && fileInfoAry[i].attach_path){
	    				var id = auditNcr.multiFileInput.add(multiFileId+"_multiFileData");
	    				var fileName = fileInfoAry[i].attach_filename;
	    				var filePath = fileInfoAry[i].attach_path;
	    				var fileSeq = fileInfoAry[i].attach_seq;
	    				var ncrSeq = fileInfoAry[i].ncr_seq;
	    				var filesize = fileInfoAry[i].attach_size;
	    				var filePathAry = String(filePath).split('/');
	    				var dpPortalFilePath = filePathAry[filePathAry.length-1];
		    			micaCommon.fileInput.edit("#"+id, {//TODO 개선 필요
		    				fileName : fileName,
		    				fileUrl : filePath,
		    				fileSeq : fileSeq,
		    				filesize : filesize,
		    				downloadFn : function(){

		    					var url = "/api/audit/auditNcrMgmt/dpPortalAuditFileDownload";
		    		    		if( url ){
		    		    	        var inputs = '';
		    		    	        inputs+='<input type="hidden" name="povisPath" value="'+filePath+'" />';
		    		    	        inputs+='<input type="hidden" name="fileName" value="'+fileName+'" />';
		    		    	        inputs+='<input type="hidden" name="dpPortalPath" value="'+dpPortalFilePath+'" />';
		    		    	        inputs+='<input type="hidden" name="attachSeq" value="'+fileSeq+'" />';
		    		    	        inputs+='<input type="hidden" name="ncrSeq" value="'+ncrSeq+'" />';
		    		    	        inputs+='<input type="hidden" name="attachType" value="'+"AUDIT"+'" />';
		    		    	        jQuery('<form action="'+ url +'" method="post">'+inputs+'</form>')
		    		    	        .appendTo('body').submit().remove();
		    		    	    };
		    		    	}
		    			});
	    			}
    			}
    		}else{
    			auditNcr.multiFileInput.add(multiFileId+"_multiFileData");
    		}
    	},
    	add: function(multiFileId){
    		var id = multiFileId+Math.floor(Math.random()*10000000)+1;
    		var template ="";
    		template+="<div class='multiFileDataInputArea' style='width:100%; float:left'>";
    		template+="<div style='float:left'><a class='btn fa fa-trash-o' type='button' onclick='auditNcr.multiFileInput.del($(this))' style='width:15px; height:25px; background-color:white; color:#353535'></a></div>";
    		template+="<div id='"+id+"' style='width:840px; float:left; margin-left:5px'></div>";
    		template+="</div>";
    		$("#"+multiFileId).append(template);
	    	micaCommon.fileInput.set("#"+id, {
	            url: micaConfig.uploadUrl,
	            attachType: "AUDIT",
	            success: function (data) {
	            	$("#"+id).attr("filename", data.DATASET.FILE_NAME);
	            	$("#"+id).attr("filepath", data.DATASET.FILE_PATH);
	            	$("#"+id).attr("filesize", data.DATASET.FILE_SIZE);
	            },
	            size: "10MB"
	        });
	    	$("#"+multiFileId+" .fileinput").css("margin-bottom","2px");
	    	$("#"+multiFileId+" .fileinput").css("margin-top","2px");
	    	return id;
    	},
    	downloadSet : function(multiFileId,fileInfoAry,dif){
    		$("#"+multiFileId).append("<div class='multiFileArea'></div>");
    		$("#"+multiFileId+" .multiFileArea").append("<div id='"+multiFileId+"_multiFileData' class='multifilearea-multifiledata' style='overflow-x:hidden; overflow-y:scroll; height:60px; margin-top: 5px; padding-left: 5px;'></div>");
    		if(fileInfoAry && fileInfoAry.length>0){
    			for(var i=0; i<fileInfoAry.length; i++){
	    			if(fileInfoAry[i].attach_filename && fileInfoAry[i].attach_path){
	    				var fileName = fileInfoAry[i].attach_filename;
	    				var povisPath = fileInfoAry[i].attach_path;
	    				var fileSeq = fileInfoAry[i].attach_seq;
	    				var ncrSeq = fileInfoAry[i].NCR_Seq;
	    				var filesize = fileInfoAry[i].attach_size;
	    				var filePathAry = String(povisPath).split('/');
	    				filePath = "";
	    				for(var j=0; j<filePathAry.length; j++){
	    					if(j>0){
	    						filePath+="\/";
	    					}
	    					filePath += filePathAry[j];
	    				}
	    				if(i>0){
	    					$("#"+multiFileId+'_multiFileData').append("<div style='clear:both;'>");
	    				}
	    				var template= "<div style='float:left'><div style='float:left'><a class='btn fa fa-folder-open' type='button' onclick='auditNcr.multiFileInput.del($(this))' style='width:15px; height:25px; background-color:white; color:#353535'></a></div>";
	    				template += "<div style='float:left; margin-left: 4px; padding-top: 2px;'><a dif='"+dif+"' ncrSeq='"+ncrSeq+"' fileSeq='"+fileSeq+"' dpPortalPath='"+filePath+"' povisPath='"+povisPath+"' onClick='auditNcr.multiFileInput.downloadSetAction($(this))'>"+fileName+"</a></div></div>";
	    				$("#"+multiFileId+'_multiFileData').append(template);
	    			}
    			}
    		}
    	},
    	downloadSetAction : function(self){
    		var url = "/api/audit/auditNcrMgmt/resultFileDownload";
    		if( url ){
    			var inputs = '';
    	        inputs+='<input type="hidden" name="povisPath" value="'+self.attr("povisPath")+'" />';
    	        inputs+='<input type="hidden" name="dpPortalPath" value="'+self.attr("dpPortalPath")+'" />';
    	        inputs+='<input type="hidden" name="fileName" value="'+self.text()+'" />';
    	        inputs+='<input type="hidden" name="attachSeq" value="'+self.attr("fileSeq")+'" />';
    	        inputs+='<input type="hidden" name="dif" value="'+self.attr("dif")+'" />';
    	        inputs+='<input type="hidden" name="ncrSeq" value="'+self.attr("ncrSeq")+'" />';
    	        inputs+='<input type="hidden" name="attachType" value="'+"AUDIT"+'" />';
    	        jQuery('<form action="'+ url +'" method="post">'+inputs+'</form>')
    	        .appendTo('body').submit().remove();
    	    };
    	},
    	del: function(val){
    		var fileInfo = val.parents(".multiFileDataInputArea").children("[id*=_multiFileData]");
    		var reqParam = {
    			dif : $("#popAuditNcrNcrDif").val(),
				attach_seq : fileInfo.attr("fileSeq"),
				ncr_seq : $("#popAuditNcrNcrSeq").val()
			};
			var options = {
				method : "POST",
				success: function (data) {
					val.parents(".multiFileDataInputArea").remove();
				}
			};
			common.ajax("/api/audit/auditNcrMgmt/delNcrResultFile", reqParam, options);
			/*if(!fileInfo.attr("fileSeq")){
				val.parents(".multiFileDataInputArea").remove();
    			return;
    		}else{
    			common.ajax("/api/audit/auditNcrMgmt/delNcrResultFile", reqParam, options);
    		}*/
    	},
    	getNameList: function(multiFileId){
    		var fileInfoAry = new Array();
    		$("div [id*="+multiFileId+"_multiFileData]").each(function( index ) {
    			var fileInfoObj = new Object();
    			var filePathAry = String($(this).attr("filepath")).split('/');
				var dpPortalFilePath = filePathAry[filePathAry.length-1];
    			fileInfoObj["filepath"] = $(this).attr("filepath");
    			fileInfoObj["dpPortalFilePath"] = dpPortalFilePath;
    			fileInfoObj["filename"] = $(this).attr("filename");
    			fileInfoObj["fileseq"] = $(this).attr("fileseq");
    			fileInfoObj["filesize"] = $(this).attr("filesize");
    			if(fileInfoObj["filepath"] && fileInfoObj["filename"]){
    				fileInfoAry.push(fileInfoObj);
    			}
    		});

    		return fileInfoAry;
    	}
    },
    popControl:{
    	pop: function(rowData){
    		$(".modal").micaModal('hide');
			$("#popAuditNcr").removeClass('hidden');
			auditNcr.lodingBar.show();
			auditNcr.popControl.popClearData();
			auditNcr.popControl.popGetData(rowData);
    	},
    	popGetData: function(rowData){
    		if(!rowData){
    			auditNcr.popControl.popDisableControl(null);
    			$("#popPcn").css("position","absolute");
    			$("#popPcn").css("top","0px");
    			$("#popPcn").css("left","0px");
    			setTimeout(function() {auditNcr.lodingBar.hide();$("#popAuditNcr").micaModal('show');}, 1500);

    			return;
    		}
    		var reqParam = {
    			ncr_seq : rowData.ncr_seq
			};
			var options = {
				method : "POST",
				success: function (data) {
					if(data.auditNcrInfo[0]){
						var auditNcrInfo = data.auditNcrInfo[0];
						$("#popAuditNcrNcrStatus").val(auditNcrInfo.NCR_Status);
			    		$("#popAuditNcrAuditType").html(auditNcrInfo.TypeNM);
			    		$("#popAuditNcrPartnersNm").html(auditNcrInfo.PartnersNM);
			    		$("#popAuditNcrAuditDate").html(auditNcrInfo.Audit_Date);
			    		$("#popAuditNcrMakeDate").html(auditNcrInfo.Make_Date);
			    		$("#popAuditNcrNcrTypeNm").html(auditNcrInfo.NCR_TypeNM);
			    		$("#popAuditNcrAuditor").html(auditNcrInfo.AuditorNM);
			    		$("#popAuditNcrNcrSubject").html(auditNcrInfo.NCR_Subject);
			    		$("#popAuditNcrNcrComment").val(auditNcrInfo.NCR_Comment);
			    		$("#popAuditNcrNcrSeq").val(auditNcrInfo.NCR_Seq);
			    		$("#popAuditNcrNcrDif").val(auditNcrInfo.NCR_DIF);
			    		$("#popAuditNcrPlant").val(auditNcrInfo.PLANT);//CM:공통, PT:평택, IF:익산
			    		auditNcr.tab.btnClick("tabBtn"+auditNcrInfo.NCR_DIF,auditNcrInfo.NCR_DIF);
			    		console.log('auditNcrInfo');
						console.log(auditNcrInfo);
					}
				}
			};
			common.ajax("/api/audit/auditNcrMgmt/getAuditNcrInfo/", reqParam, options);
			setTimeout(function() {auditNcr.lodingBar.hide();$("#popAuditNcr").micaModal('show');}, 1500);

    	},
    	popGetDataTab: function(paramDif){
    		var reqParam = {
    			ncr_seq : $("#popAuditNcrNcrSeq").val(),
    			dif : paramDif
			};
			var options = {
				method : "POST",
				success: function (data) {
					auditNcr.popControl.popClearDataTab();
					if(data.auditNcrInfoPlan){
						var auditNcrInfoPlan = data.auditNcrInfoPlan[0];
						var userInfo = data.userInfo;
						if(!auditNcrInfoPlan){
							$("#popAuditNcrTabPlanEmpnm").html(userInfo.USER_NAME);
							$("#popAuditNcrTabPlanEmail").html(userInfo.EMAIL_ADDRESS);
							$("#popAuditNcrTabPlanEmpcd").val(userInfo.USER_ID);
						}else{
							$("#popAuditNcrTabPlanEmpnm").html(auditNcrInfoPlan.NCR_P_Make_EmpNM);
							$("#popAuditNcrTabPlanEmail").html(auditNcrInfoPlan.NCR_P_EMail);
							$("#popAuditNcrTabPlanEmpcd").val(auditNcrInfoPlan.NCR_P_Make_EmpCD);
							$("#popAuditNcrTabPlanMakeDate").html(auditNcrInfoPlan.NCR_P_Make_Date);
							$("#popAuditNcrTabPlanDate").val(auditNcrInfoPlan.NCR_P_Plan_Date);
							$("#popAuditNcrTabPlanComment").val(auditNcrInfoPlan.NCR_P_Comment);
						}

					}
					if(data.auditNcrInfoResult[0]){
						var auditNcrInfoResult = data.auditNcrInfoResult[0];
						$("#popAuditNcrTabResultMakeDate").html(auditNcrInfoResult["NCR_R_Make_Date"]);
						$("#popAuditNcrTabResultCompleteDate").val(auditNcrInfoResult["NCR_R_Complete_Date"]);
						$("#popAuditNcrTabResultComment").val(auditNcrInfoResult["NCR_R_Comment"]);

						$("#popAuditNcrTabNcrConfirmDate").html(auditNcrInfoResult["NCR_C_Confirm_Date"]);
						$("#popAuditNcrTabConfirmEmpNm").html(auditNcrInfoResult["NCR_C_Confirm_EmpNM"]);
						$("#popAuditNcrTabConfirmYn").html(auditNcrInfoResult["NCR_C_Confirm_YN"]);
						$("#popAuditNcrTabConfirmComment").val(auditNcrInfoResult["NCR_C_Confirm_Comment"]);

					}

					//영역 제어
					auditNcr.popControl.popDisableControlTab(paramDif);
					//파일 area 타입 제어
					auditNcr.popControl.popFileAreaControlTab(paramDif,data);
					//버튼 제어
					auditNcr.popControl.popBtnControlTab(paramDif);
				}
			};
			common.ajax("/api/audit/auditNcrMgmt/getAuditNcrInfoTab", reqParam, options);

    	},
    	popClearData: function(){
    		//지적사항
    		$("#popAuditNcrAuditType").html("");
    		$("#popAuditNcrPartnersNm").html("");
    		$("#popAuditNcrAuditDate").html("");
    		$("#popAuditNcrMakeDate").html("");
    		$("#popAuditNcrNcrTypeNm").html("");
    		$("#popAuditNcrAuditor").html("");
    		$("#popAuditNcrNcrSubject").html("");
    		$("#popAuditNcrNcrComment").val("");
    		$("#popAuditNcrNcrSeq").val("");
    		$("#popAuditNcrNcrDif").val("");
    		$("#popAuditNcrPlant").val("");
    		

    		//조치계획
    		$("#popAuditNcrTabPlanMakeDate").html("");
    		$("#popAuditNcrTabPlanEmpnm").html("");
    		$("#popAuditNcrTabPlanDate").val("");
    		$("#popAuditNcrTabPlanEmail").html("");
    		$("#popAuditNcrTabPlanComment").val("");

    		//조치결과
    		$("#popAuditNcrTabResultMakeDate").html("");
    		$("#popAuditNcrTabResultCompleteDate").val("");
    		$("#popAuditNcrTabResultComment").val("");
    		$("#popAuditResultFileArea").html("");

    		//조치확인
    		$("#popAuditNcrTabNcrConfirmDate").html("");
			$("#popAuditNcrTabConfirmEmpNm").html("");
			$("#popAuditNcrTabConfirmYn").html("");
			$("#popAuditNcrTabConfirmComment").val("");

			//disable 상태제거
			auditNcr.popControl.allDisableSet(false);
    		return;

    	},
    	popClearDataTab: function(){
    		//조치계획
    		$("#popAuditNcrTabPlanMakeDate").html("");
    		$("#popAuditNcrTabPlanEmpnm").html("");
    		$("#popAuditNcrTabPlanDate").val("");
    		$("#popAuditNcrTabPlanEmail").html("");
    		$("#popAuditNcrTabPlanComment").val("");

    		//조치결과
    		$("#popAuditNcrTabResultMakeDate").html("");
    		$("#popAuditNcrTabResultCompleteDate").val("");
    		$("#popAuditNcrTabResultComment").val("");
    		$("#popAuditResultFileArea").html("");

    		//조치확인
    		$("#popAuditNcrTabNcrConfirmDate").html("");
			$("#popAuditNcrTabConfirmEmpNm").html("");
			$("#popAuditNcrTabConfirmYn").html("");
			$("#popAuditNcrTabConfirmComment").val("");

			//disable 상태제거
			auditNcr.popControl.allDisableSet(false);
    		return;

    	},
    	popFileAreaControlTab: function(clickDif,data){
    		if(clickDif==1){
				if($("#popAuditNcrNcrStatus").val()>"F"){
					if(data.auditNcrInfoResultFile){
						auditNcr.multiFileInput.downloadSet("popAuditResultFileArea",data.auditNcrInfoResultFile,clickDif);
					}
				}else if($("#popAuditNcrNcrStatus").val()=="E"||$("#popAuditNcrNcrStatus").val()=="F"){
					if(data.auditNcrInfoResultFile){
						var auditNcrInfoResultFile = data.auditNcrInfoResultFile;
						auditNcr.multiFileInput.set("popAuditResultFileArea",data.auditNcrInfoResultFile);
					}else{
						auditNcr.multiFileInput.set("popAuditResultFileArea");
					}
				}
			}else if(clickDif==2){
				if($("#popAuditNcrNcrStatus").val()>"K"){
					if(data.auditNcrInfoResultFile){
						auditNcr.multiFileInput.downloadSet("popAuditResultFileArea",data.auditNcrInfoResultFile,clickDif);
					}
				}else if($("#popAuditNcrNcrStatus").val()=="J"||$("#popAuditNcrNcrStatus").val()=="K"){
					if(data.auditNcrInfoResultFile){
						var auditNcrInfoResultFile = data.auditNcrInfoResultFile;
						auditNcr.multiFileInput.set("popAuditResultFileArea",data.auditNcrInfoResultFile);
					}else{
						auditNcr.multiFileInput.set("popAuditResultFileArea");
					}
				}
			}else if(clickDif==3){
				if($("#popAuditNcrNcrStatus").val()>"P"){
					if(data.auditNcrInfoResultFile){
						auditNcr.multiFileInput.downloadSet("popAuditResultFileArea",data.auditNcrInfoResultFile,clickDif);
					}
				}else if($("#popAuditNcrNcrStatus").val()=="O"||$("#popAuditNcrNcrStatus").val()=="P"){
					if(data.auditNcrInfoResultFile){
						var auditNcrInfoResultFile = data.auditNcrInfoResultFile;
						auditNcr.multiFileInput.set("popAuditResultFileArea",data.auditNcrInfoResultFile);
					}else{
						auditNcr.multiFileInput.set("popAuditResultFileArea");
					}
				}
			}
    	},
    	popDisableControlTab: function(clickDif){
    		var dif = $("#popAuditNcrNcrDif").val();
    		//auditNcr.popControl.allDisableSet(false);
    		auditNcr.popControl.allDisableSet(true);
    		if(clickDif<dif||$("#popAuditNcrNcrStatus").val()=="R"){
    			return;
    		}else{
    			if(dif=="1"){
    				if($("#popAuditNcrNcrStatus").val()<"E"){
    					$("#popAuditNcrTabPlanComment").prop("disabled",false);
    					$("#popAuditNcrTabPlanDate").prop("disabled",false);
    					$("#popAuditResultFileArea").html("");
        			}else if($("#popAuditNcrNcrStatus").val()<"G"){
        				$("#popAuditNcrTabResultCompleteDate").prop("disabled",false);
        				$("#popAuditNcrTabResultComment").prop("disabled",false);

        			}
    			}else if(dif=="2"){
    				if($("#popAuditNcrNcrStatus").val()<"J"){
    					$("#popAuditNcrTabPlanComment").prop("disabled",false);
    					$("#popAuditNcrTabPlanDate").prop("disabled",false);
    					$("#popAuditResultFileArea").html("");
        			}else if($("#popAuditNcrNcrStatus").val()<"L"){
        				$("#popAuditNcrTabResultCompleteDate").prop("disabled",false);
        				$("#popAuditNcrTabResultComment").prop("disabled",false);
        			}
    			}else if(dif=="3"){
    				if($("#popAuditNcrNcrStatus").val()<"O"){
    					$("#popAuditNcrTabPlanComment").prop("disabled",false);
    					$("#popAuditNcrTabPlanDate").prop("disabled",false);
    					$("#popAuditResultFileArea").html("");
        			}else if($("#popAuditNcrNcrStatus").val()<"Q"){
        				$("#popAuditNcrTabResultCompleteDate").prop("disabled",false);
        				$("#popAuditNcrTabResultComment").prop("disabled",false);
        			}
    			}
    			return;
    		}

    	},
    	popBtnControlTab: function(dif){
    		auditNcr.popControl.allhideBtnSet();
    		if($("#popAuditNcrNcrStatus").val()=="R"){
    			return;
    		}else{
    			if(dif=="1"){
    				if($("#popAuditNcrNcrStatus").val()=="C"||$("#popAuditNcrNcrStatus").val()=="D"){
    					$("#btnPopAuditNcrPlanSave").show();
    					$("#btnPopAuditNcrPlanComplete").show();
    				}else if($("#popAuditNcrNcrStatus").val()=="E"||$("#popAuditNcrNcrStatus").val()=="F"){
    					$("#btnPopAuditNcrResultSave").show();
    					$("#btnPopAuditNcrResultComplete").show();
    				}
    			}else if(dif=="2"){
    				if($("#popAuditNcrNcrStatus").val()=="H"||$("#popAuditNcrNcrStatus").val()=="I"){
    					$("#btnPopAuditNcrPlanSave").show();
    					$("#btnPopAuditNcrPlanComplete").show();
    				}else if($("#popAuditNcrNcrStatus").val()=="J"||$("#popAuditNcrNcrStatus").val()=="K"){
    					$("#btnPopAuditNcrResultSave").show();
    					$("#btnPopAuditNcrResultComplete").show();
    				}
    			}else if(dif=="3"){
    				if($("#popAuditNcrNcrStatus").val()=="M"||$("#popAuditNcrNcrStatus").val()=="N"){
    					$("#btnPopAuditNcrPlanSave").show();
    					$("#btnPopAuditNcrPlanComplete").show();
    				}else if($("#popAuditNcrNcrStatus").val()=="O"||$("#popAuditNcrNcrStatus").val()=="P"){
    					$("#btnPopAuditNcrResultSave").show();
    					$("#btnPopAuditNcrResultComplete").show();
    				}
    			}
    		}
    	},
    	allDisableSet:function(setType){
    		$("#popAuditNcrTabPlanDate").prop("disabled",setType);
    		$("#popAuditNcrTabPlanComment").prop("disabled",setType);
    		$("#popAuditNcrTabResultCompleteDate").prop("disabled",setType);
    		$("#popAuditNcrTabResultComment").prop("disabled",setType);
    	},
    	allhideBtnSet: function(){
    		$('#popProgressRegister').hide();
    		$('#btnPopAuditNcrPlanSave').hide();
    		$('#btnPopAuditNcrResultSave').hide();
    		$('#btnPopAuditNcrPlanComplete').hide();
    		$('#btnPopAuditNcrResultComplete').hide();
    	}
    },
    lodingBar:{
    	show: function(){
    		$(".mm-backdrop").show();
    		$("#loadingArea").css("visibility","visible");
    	},
    	hide: function(){
    		$("#loadingArea").css("visibility","hidden");
    	}
    },
    tab:{
    	btnClick: function(id,paramDif){
    		this.btnClear();
    		$("#"+id).addClass("tab-button-click");
    		auditNcr.popControl.popGetDataTab(paramDif);
    	},
    	btnClear: function(){
    		$(".tab-button-click").removeClass("tab-button-click");
    	}
    }
};
