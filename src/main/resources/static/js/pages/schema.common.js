/*SCHEMA 공통 스크립트
document.addEventListener("DOMContentLoaded", function() {
    //모달 Draggable 처리
    const modalHeader = document.querySelector(".dialog_head");
    const modalDialog = document.querySelector(".cui_dialog");

    // modalHeader가 null인지 확인하고, 존재하지 않으면 경고 메시지 출력
    if (!modalHeader || !modalDialog) {
        return; // 요소가 없으면 더 이상 코드를 실행하지 않음
    }

    let isDragging = false;
    let mouseOffset = { x: 0, y: 0 };
    let dialogOffset = { left: 0, top: 0 }; // 'right' -> 'top'으로 수정

    // 드래그 시작 시 모달의 현재 위치를 계산하여 저장
    modalHeader.addEventListener("mousedown", function (event) {
      isDragging = true;
      mouseOffset = { x: event.clientX, y: event.clientY };
      // 모달의 현재 위치가 없을 경우 기본값을 0으로 설정
      dialogOffset = {
        left: modalDialog.style.left === '' ? 800 : Number(modalDialog.style.left.replace('px', '')),
        right: modalDialog.style.top === '' ? 400 : Number(modalDialog.style.top.replace('px', ''))
      }
    });
    // 드래그 중일 때 모달 위치 변경
    document.addEventListener("mousemove", function (event) {
      if (!isDragging) {
        return;
      }
      // 새로운 위치 계산
      let newX = event.clientX - mouseOffset.x;
      let newY = event.clientY - mouseOffset.y;
      // 모달의 위치 업데이트
      modalDialog.style.left = `${dialogOffset.left + newX}px`
      modalDialog.style.top = `${dialogOffset.right + newY}px`
    });
     // 드래그 종료 시 이벤트 리스너 제거
    document.addEventListener("mouseup", function () {
      isDragging = false;
    });
});
*/

//모달 초기화
function modal_init(id, status){
    $('#modalTitle').text(status == 'add'?'신규 추가':''); //모달창의 제목 변경

    // form 전체 초기화
    //if(status =='add'){
        $("form[name='form"+id+"']").each(function() {
            this.reset();
            $("input[type=hidden]").val(''); //reset만으로 hidden type은 리셋이 안되기 때문에 써줌

            // 파일 입력 초기화
            $(this).find('input[type="file"]').each(function() {
                $(this).val('');  // 파일 입력 초기화
            });

            $('#modalFileName span').text('');

            // readOnly 속성 초기화
            $(this).find('input, textarea, select').each(function() {
                // readOnly 속성 제거 (읽기 전용 상태를 해제)
                $(this).removeAttr('readonly');
            });
            $(this).find('[style*="display: none"]').each(function() {
                $(this).css('display', 'block');  // 보이게 하기
            });
        });
    //}
}

//모달 열기
function modal_overlay(status) {
    if (status) {
        overlay.style.display = "block"; // 오버레이 배경 표시
        $(overlay).css("opacity", "0%");
        $(overlay).animate({opacity: '100%'}, 500);
    } else {
        $(overlay).animate({opacity: '0%'},{duration: 500, complete: function(){
            overlay.style.display = "none"; // 오버레이 숨기기
        }});
    }
}

function modal_open(id, status, sUrl, param, info){
    var overlayStatus = true;

    if (info != undefined) {
        if (info.overlayStatus == false) overlayStatus = false;
    }

    if (overlayStatus) {
        modal_overlay(true);
    }

    $("#modalState").val(status); // status 저장
    if(status !='add'){
        var sUrl = sUrl;
        var data = {};  // 전송할 데이터 객체 생성

        // param1, param2, ...로 data 구성
        if(param != null && typeof param === 'string' && param.includes("|")){
            var params = param.split("|");
            //console.log("param:"+param + "//////"+params.length);
            for (var i = 0; i < params.length; i++) {
                data["param" + (i + 1)] = params[i];  // param1, param2, ... 형식으로 추가
            }
        }else{
            data = {param1 : param};
        }
        $.ajax({
            type: "get",
            url:   sUrl,
            data: data,
            success: function(res) {
                console.log("요청성공", res);

                if (res != null) {
                    $("#" + id).fadeIn();
                    modal_init(id, status);

                    // res 데이터를 다른 함수로 전달하여 데이터 바인딩 처리
                    if(status === 'second'){ //두번째 모달창을 여는 경우
                        bindModalData2(status, res);
                    }else{
                        bindModalData(status, res);
                    }
                }
            },
            error: function(err) {
              console.log("에러발생", err);
            }
        });
    }else{
        modal_init(id, status);
        $("#" + id).fadeIn();
    }
}

//모달 닫기
function modal_close(id, info){
    var overlayStatus = true;

    if (info != undefined) {
        if (info.overlayStatus == false) overlayStatus = false;
    }

    if (overlayStatus) {
        modal_overlay(false);
    }

    $("#" + id).fadeOut();
    //초기화
    modal_init(id, 'add');
}


//2024.11.08 sylee 이사왔음.
// updatePagination(response.pageMaker);
// 페이지네이션 처리 함수 (updatePagination)
function updatePagination(pageMaker) {
    var paginationHtml = '';
    var currentPage = pageMaker.criteria.pageNum;
    var startPage = pageMaker.startPage;
    var endPage = pageMaker.endPage;
    var totalPages = pageMaker.totalPages;

    // 이전 페이지 버튼
    if (currentPage > 1) {
        paginationHtml += `<a href="#" class="prev" data-page="${currentPage - 1}">&lt;&lt;</a>`;
    }

    // 페이지 번호
    for (var i = startPage; i <= endPage; i++) {
        if (i === currentPage) {
            paginationHtml += `<a href="#" class="page active" data-page="${i}">${i}</a>`;
        } else {
            paginationHtml += `<a href="#" class="page" data-page="${i}">${i}</a>`;
        }
    }

    // 다음 페이지 버튼
    if (currentPage < totalPages) {
        paginationHtml += `<a href="#" class="next" data-page="${currentPage + 1}">&gt;&gt;</a>`;
    }

    // 페이지네이션 HTML 업데이트
    $('#pagination').html(paginationHtml);

    // 페이지 번호 클릭 시, 해당 페이지로 데이터 로드
    $('.page, .prev, .next').on('click', function(event) {
        event.preventDefault();
        var pageNum = $(this).data('page');
        searchCompanies(pageNum);
    });
}


function modalConfirmClose(id){
    $("#" + id).fadeOut();
}

function confirmAction(isConfirmed) {
    if (window.confirmCallback) {
        window.confirmCallback(isConfirmed);
    }
    modalConfirmClose('dialogConfirm');
}

function showConfirm(type, message ,callback ) {
   var alertDiv = document.querySelector('#alertTypeimg');
   alertDiv.setAttribute('data-type', type);  // 'success' 또는 'warning' 설정
   document.getElementById('confirmMessageContent').innerText = message;  // 메시지 설정
   modal('dialogConfirm');
   window.confirmCallback = callback;
}

function showAlert(type, message ) {
   var alertDiv = document.querySelector('#alertTypeimg');
   alertDiv.setAttribute('data-type', type);  // 'success' 또는 'warning' 설정
   document.getElementById('messageContent').innerText = message;  // 메시지 설정
   modal_open('dialog', 'add');
}

     //모달 열기
  function modal(id){
    overlay.style.display = "block"; // 오버레이 배경 표시
    $("#" + id).fadeIn();
  }

/* 첨부파일 검증 */
function validation(obj){
    const fileTypes = [
                      'image/jpeg', 'image/png', 'image/gif', 'image/bmp', 'image/svg+xml', 'image/webp',
                      'application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
                      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'audio/mpeg', 'audio/wav',
                      'video/mp4', 'application/zip', 'application/xlsx'
                    ];
    if (obj.name.length > 100) {
        alert("파일명이 100자 이상인 파일은 제외되었습니다.");
        return false;
    } else if (obj.size > (100 * 1024 * 1024)) {
        alert("최대 파일 용량인 100MB를 초과한 파일은 제외되었습니다.");
        return false;
    } else if (obj.name.lastIndexOf('.') == -1) {
        alert("확장자가 없는 파일은 제외되었습니다.");
        return false;
    } else if (!fileTypes.includes(obj.type)) {
        alert("첨부가 불가능한 파일은 제외되었습니다.");
        return false;
    } else {
        return true;
    }


    //


}

var Cookies = function (name, value, expire) {
    if (typeof value != "undefined" && typeof expire != "undefined") {
        var day = new Date();
        day.setDate(day.getDate() + expire);
        document.cookie = name + "=" + escape(value) + "; path=/; expires=" + day.toGMTString() + ";";
    } else {
        var org = document.cookie;
        var dlm = name + "=";
        var x = 0;
        var y = 0;
        var z = 0;

        while (x <= org.length) {
            y = x + dlm.length;

            if (org.substring(x, y) == dlm) {
                if ((z = org.indexOf(";", y)) == -1) {
                    z = org.length;
                }

                return org.substring(y, z);
            }

            x = org.indexOf(" ", x) + 1;

            if (x == 0) {
                break;
            }
        }

        return "";
    }
}

var siteLang = {}
siteLang.selLang = (Cookies("selLang") || "KOR");
siteLang.langsData = null;
siteLang.clear = function () {
    localStorage.clear();
}
siteLang.init = function () {
    var data = localStorage.getItem('SITE_LANGUAGE_DATA');
    if (!data) {
        siteLang.getLangsList(siteLang.selLang);
        data = localStorage.getItem('SITE_LANGUAGE_DATA');
    }

    if (!data) {
        return;
    }

    siteLang.langsData = JSON.parse(data);
}
siteLang.getLangsList = function () {
    $.ajax({
    	url : '/multiLanguage/getLocalStorage',
    	type : "post",
    	async : false,
    	success : function(data) {
       		localStorage.setItem("SITE_LANGUAGE_DATA", JSON.stringify(data)); //로컬스토리지에 저장
    	},
    	error : function() {
    	}
    });
}
siteLang.showLangs = function (obj) {
    obj = (obj || $('body'))

    obj.find('[data-langsid]').each(function () {
        var kor = $(this).attr("data-langsid");

        try {
            const item = siteLang.langsData.find(entry => entry.KOR === kor);
            if (item && item[siteLang.selLang]) {
                $(this).html(item[siteLang.selLang]);
            }
            else
            {
                siteLang.devDBSet(kor);
                $(this).html(kor);
            }
        } catch (e) {
        }
    });

    obj.find('.data-langsid').each(function () {
        var kor = $(this).attr("data-langsid");

        if (kor == undefined) {
            kor = $(this).html();
            $(this).attr("data-langsid", kor);
            $(this).removeClass("data-langsid");
        }

        try {
            const item = siteLang.langsData.find(entry => entry.KOR === kor);
            if (item && item[siteLang.selLang]) {
                $(this).html(item[siteLang.selLang]);
            }
            else
            {
                siteLang.devDBSet(kor);
                $(this).html(kor);
            }
        } catch (e) {
        }
    });

    obj.find('input[placeholder]').each(function () {
        var kor = $(this).attr("data-langsid");

        try {
            const item = siteLang.langsData.find(entry => entry.KOR === kor);
            if (item && item[siteLang.selLang]) {
                $(this).attr("placeholder", item[siteLang.selLang]);
            }
            else
            {
                siteLang.devDBSet(kor);
                $(this).attr("placeholder", kor);
            }
        } catch (e) {
        }
    });

    obj.find('input[messages],select[messages],textarea[messages]').each(function () {
        var kor = $(this).attr("data-messages");

        try {
            const item = siteLang.langsData.find(entry => entry.KOR === kor);
            if (item && item[siteLang.selLang]) {
                $(this).attr("messages", item[siteLang.selLang]);
            }
            else
            {
                siteLang.devDBSet(kor);
                $(this).attr("messages", kor);
            }
        } catch (e) {
        }
    });

    obj.find('div[gridYN=Y]').each(function () {
        var grid = $(this).pqGrid("instance");
        var colModel;

        if (this.gridColModel) {
            colModel = this.gridColModel;
        } else {
            colModel = grid.getColModel();
            for (j = 0; j < colModel.length; j++) {
                var kor = colModel[j].data_langsid;

                if (typeof kor == "undefined" || kor == "") {
                    continue;
                }

                try {
                    const item = siteLang.langsData.find(entry => entry.KOR === kor);
                    if (item && item[siteLang.selLang]) {
                        colModel[j].title = item[siteLang.selLang];
                    }
                    else
                    {
                        siteLang.devDBSet(kor);
                        colModel[j].title = kor;
                    }
                } catch (e) {
                }
            }
        }

        grid.option( "colModel", colModel );
        grid.refreshCM();
        grid.refreshView();
    });
}
siteLang.getLang = function (kor) {
    try {
       const item = siteLang.langsData.find(entry => entry.KOR === kor);
        if (item && item[siteLang.selLang]) {
            return item[siteLang.selLang];
        }
        else
        {
            siteLang.devDBSet(kor);
            return kor;
        }
    } catch (e) {
    }
}
siteLang.chgLangs = function (selLang) {
    Cookies("selLang", selLang, 365);
    siteLang.selLang = selLang;
    siteLang.showLangs();
}
$(document).ready(function () {
    siteLang.init();
    siteLang.showLangs();
});

siteLang.devDBSet = function (KOR) {
    if (typeof KOR == "undefined" || KOR == "") {
        return;
    }

    $.ajax({
    	url : '/multiLanguage/saveMultiLanguage',
    	type : "post",
    	data : "KOR=" + KOR,
    	dataType : "text",
    	contentType : "application/x-www-form-urlencoded;charset=utf-8",
    	success : function(data) {
    	},
    	error : function() {
    	}
    });
}

var Common = {};

// Property 추가
Common.AddProperty = function (obj, name, onGet, onSet) {
    // wrapper functions
    var oldValue = obj[name],
        getFn = function () {
            return onGet.apply(obj, [oldValue]);
        },
        setFn = function (newValue) {
            return oldValue = onSet.apply(obj, [newValue]);
        };

    // Modern browsers, IE9+, and IE8 (must be a DOM object),
    if (Object.defineProperty) {

        Object.defineProperty(obj, name, {
            get: getFn,
            set: setFn
        });

        // Older Mozilla
    } else if (obj.__defineGetter__) {

        obj.__defineGetter__(name, getFn);
        obj.__defineSetter__(name, setFn);

        // IE6-7
        // must be a real DOM object (to have attachEvent) and must be attached to document (for onpropertychange to fire)
    } else {
        var onPropertyChange = function (e) {
            if (event.propertyName == name) {
                // temporarily remove the event so it doesn't fire again and create a loop
                obj.detachEvent("onpropertychange", onPropertyChange);

                // get the changed value, run it through the set function
                var newValue = setFn(obj[name]);

                // restore the get function
                //obj[name] = getFn;
                //obj[name].toString = getFn;

                // restore the event
                obj.attachEvent("onpropertychange", onPropertyChange);
            }
        };

        obj[name] = onGet;
        obj[name].toString = onGet;

        obj.attachEvent("onpropertychange", onPropertyChange);
    }
}

//페이지 이동
Common.MoveUrl = function (Url, Msg, Check) {
    if (Url == "")
        return;

    if (Msg == null || Msg == undefined) {
		location.href = Url;
	} else if (Check == null || Check == undefined || Check == false) {
	    swal({
	        title: Msg,
	        showCancelButton: false,
	        confirmButtonColor: "#DD6B55",
	        confirmButtonText: "확인",
            closeOnConfirm: true,
            animation : false
	    }).then(function () {
	        location.href = Url;
	    });
	} else if (Check == true) {
	    swal({
	        title: Msg,
	        type: "warning",
	        showCancelButton: true,
	        confirmButtonColor: "#DD6B55",
	        confirmButtonText: "확인",
	        cancelButtonText: "취소",
            closeOnConfirm: false,
            animation: false
	    }).then(function () {
	        location.href = Url;
	    });
	}
}

//페이지 새창
Common.OpenUrl = function (url, wname, width, height, scrl, resi, stat, addOption) {
    if (typeof wname == "undefined") {
        return window.open(url);
    }

	// 듀얼모니터 일경우 가운데 정렬에 문제가 좀 있어서 우선 선택한 창에서 나오게는 처리함
	//var winl = (screen.width - width) / 2;
	var winl = window.screenX + (screen.width / 2) - (width / 2);
	var wint = (screen.height - height) / 2;

	if (typeof scrl == "undefined") {
		var scroll = "no";
	} else {
		var scroll = scrl;
	}

	if (typeof resi == "undefined") {
		var resizable = "no";
	} else {
		var resizable = resi;
	}

	if (typeof stat == "undefined") {
		var status = "yes";
	} else {
		var status = stat;
	}

	if (typeof addOption == "undefined") {
		addOption = "";
	} else {
		addOption = "," + addOption;
	}

	return window.open(url, wname, "left=" + winl + ", top=" + wint + ", scrollbars=" + scroll + ", status=" + status + ", resizable=" + resizable + ", width=" + width + ", height=" + height + addOption);
}

Common.Msg = function (Msg, info) {
    info = (info || {});
    var mode = (info.mode || "msg");
    var okback = (info.okback || undefined);
    var cancelback = (info.cancelback || undefined);
    var text = (info.text || "");
    var html = (info.html || "");
    var allowOutsideClick = true;

    if (info.allowOutsideClick != undefined) {
        allowOutsideClick = info.allowOutsideClick;
    }

    var option = {};
    option.title = Msg;
    if (text != "") {
        option.text = text;
    }
    if (html != "") {
        option.html = html;
    }
    option.showCancelButton = false;
    option.confirmButtonColor = "#DD6B55";
    option.confirmButtonText = "확인";
    option.closeOnConfirm = true;
    option.allowOutsideClick = allowOutsideClick;
    option.animation = false;

    if (mode == "msg") {
        swal(option).then(function () {
                if (typeof okback == "function") {
                    okback();
                }
            }
        );
    } else if (mode == "confrim") {
        option.type = "warning";
        option.cancelButtonText = "취소";
        option.showCancelButton = true;

        swal(option).then(function () {
            if (typeof okback == "function") {
                okback();
            }
        }, function (dismiss) {
            if (dismiss === 'cancel') {
                if (typeof cancelback == "function") {
                    cancelback();
                }
            }
        });
    }
}

//값체크
//strValue(string) : 원본문자열, strCheck(string) : 체크문자열
Common.Contains = function (strValue, strCheck) {
    if (strValue == undefined || strValue == null)
        return false;

    if (strValue.indexOf(strCheck) == -1)
        return false;
    else
        return true;
}

//===========================================================================================
//제목 : Ajax
//===========================================================================================
//Param : [{ Key: key, Value: value }, { Key: key, Value: value }] 형태의 Default로 무조건 들어가는 값
Common.RequestInfo = function (Param) {
    var thisObj = this;
    var defaultParam;
    this.Parameters = [];
    this.formData = null;

    this.Default = function () {
        if (this.Parameters.length == 0 && defaultParam) {
            for (var i = 0; i < defaultParam.length; i++) {
                var dataInfo = defaultParam[i];
                this.Parameters[i] = { Key: dataInfo.Key, Value: dataInfo.Value };
            }
        }
    };

    this.AddParameter = function (key, value, addCheck) {
        if (typeof value == "undefined" && typeof key == "object" && key != null && key.find) {
            var NoSearchObj = null;
            if (key.attr("NoSearchParam") != undefined && $("#" + key.attr("NoSearchParam")).length > 0) {
                NoSearchObj = $("#" + key.attr("NoSearchParam")).find("input,textarea,select");
            }
            key.find("input,textarea,select").not(NoSearchObj).each(function (n) {
                if ($(this).attr("name") && $(this).attr("name") != "") {
                    if ($(this)[0].tagName == "INPUT" && $(this).attr("type") && $(this).attr("type").toLowerCase() == "radio") {
                        if ($(this).prop("checked") == true) {
                            thisObj.AddParameter($(this).attr("name"), $(this).val());
                        }
                    } else if ($(this)[0].tagName == "INPUT" && $(this).attr("type") && $(this).attr("type").toLowerCase() == "checkbox") {
                        if ($(this).prop("checked") == true) {
                            var tmpValue = thisObj.GetParameter($(this).attr("name"));
                            if (tmpValue == "") {
                                thisObj.AddParameter($(this).attr("name"), $(this).val());
                            } else {
                                if ($(this).attr("multiNumber") != undefined) {
                                    thisObj.AddParameter($(this).attr("name"), Common.Convert.Int(tmpValue) + Common.Convert.Int($(this).val()));
                                } else {
                                    thisObj.AddParameter($(this).attr("name"), tmpValue + "," + $(this).val());
                                }
                            }
                        }
                    } else if ($(this)[0].tagName == "INPUT" && $(this).attr("type") && $(this).attr("type").toLowerCase() == "file" && thisObj.formData != null) {
                        if ($(this).val() == "" && $(this).attr("dragFile") != undefined) {
                            thisObj.formData.append($(this).attr("name"), Common.RequestInfo.Files[$(this).attr("dragFile")]);
                        } else if ($(this).val() == "" && $(this).attr("dragFiles") != undefined) {
                            var file_arr = Common.RequestInfo.Files[$(this).attr("dragFiles")];
                            for (var Idx in file_arr) {
                                if (file_arr[Idx]) {
                                    thisObj.formData.append($(this).attr("uploadFileName"), file_arr[Idx].file);
                                }
                            }
                        } else {
                            thisObj.formData.append($(this).attr("name"), $(this)[0].files[0]);
                        }
                    } else if ($(this)[0].tagName == "SELECT" && $(this).attr("multiple") && $(this).attr("multiple") == "multiple") {
                        thisObj.AddParameter($(this).attr("name"), $(this).val().toString(), true);
                    } else {
                        thisObj.AddParameter($(this).attr("name"), $(this).val(), true);
                    }
                }
            });
        } else {
            value = (value || "");
            var dataParametersLength = this.Parameters.length;
            var exists = false;
            for (var i = 0; i < dataParametersLength; i++) {
                var dataInfo = this.Parameters[i];
                if (dataInfo.Key == key) {
                    if (typeof addCheck == "undefined") {
                        this.Parameters[i] = { Key: key, Value: value };
                    } else if (addCheck == true) {
                        var tmpValue = dataInfo.Value;
                        this.Parameters[i] = { Key: key, Value: tmpValue + "," + value };
                    }
                    exists = true;
                }
            }

            if (exists == false)
                this.Parameters.push({ Key: key, Value: value });
        }
    };

    this.GetParameter = function (key) {
        var rtnParam = "1=1";
        var dataParametersLength = this.Parameters.length;
        if (typeof key == "undefined") {
            for (var i = 0; i < dataParametersLength; i++) {
                rtnParam = rtnParam + "&" + this.Parameters[i].Key + "=" + this.Parameters[i].Value;
            }
            return rtnParam;
        } else {
            rtnParam = "";
            for (var i = 0; i < dataParametersLength; i++) {
                if (this.Parameters[i].Key == key) {
                    rtnParam = this.Parameters[i].Value;
                    break;
                }
            }
            return rtnParam;
        }
    };

    this.SetParameter = function (param) {
        var params = param.split("&");
        for (var i = 0; i < params.length; i++) {
            if (params[i] != "") {
                var p = params[i].split("=");
                if (p.length == 2)
                    AddParameter(p[0], p[1]);
            }
        }
    };

    this.GetJson = function () {
        var rtnParam = {};
        var dataParametersLength = this.Parameters.length;

        for (var i = 0; i < dataParametersLength; i++) {
            if (thisObj.formData == null) {
                rtnParam[this.Parameters[i].Key] = this.Parameters[i].Value;
            } else {
                thisObj.formData.append(this.Parameters[i].Key, this.Parameters[i].Value);
            }
        }

        if (thisObj.formData == null) {
            return rtnParam;
        } else {
            return thisObj.formData;
        }
    };

    return (function () {
        defaultParam = Param;
        if (Param)
            thisObj.Default();
    })();
}
Common.RequestInfo.Files = {};

//url(string) : 실행경로, data(Common.RequestInfo) : 파라메타 데이타, callback(function) : 처리, responseType(string) : 처리내역 형태
//formMethod(string) : 전송방식, async(bool) : 시크타입, errback(function) : 에러 처리
Common.Ajax = function (url, data, callback, info) {
    if (url == undefined || url == "") {
        Common.Msg("No Url Info.");
        return;
    }

    var contentType, responseType, formMethod, async, errback, okErr;

    if (info == undefined) {
        formMethod = "POST";
    } else {
        if (info.async == undefined) async = false
        else async = info.async

        formMethod = (info.formMethod || "POST");
        responseType = (info.responseType || undefined);
        contentType = (info.contentType || "application/x-www-form-urlencoded;charset=utf-8");
        errback = (info.errback || undefined);
        okErr = (info.okErr || undefined);
    }

    async = (async || false);
    data = (data || new Common.RequestInfo());
    var Parameters;

    if (formMethod && formMethod == "GET") {
        data.formData = null;
        AType = { type: "GET", contentType: "charset=utf-8", processData: true };
        Parameters = data.GetParameter();
    } else {
        if (data.formData == null) {
            AType = {
                type: "POST", contentType: contentType, processData: true
            };
        } else {
            AType = {
                type: "POST", contentType: false, processData: false
            };
        }

        Parameters = data.GetJson();

        if (contentType == "application/json") {
            Parameters = JSON.stringify(Parameters);
        }
    }

    $.ajax({
        type: AType.type,
        contentType: AType.contentType,
        processData: AType.processData,
        headers: {
            "cache-control": "no-cache", "pragma": "no-cache", "requesttype": "ajax"
        },
        async: async,
        cache: false,
        url: url,
        data: Parameters,
        dataType: (responseType || "text"),
        timeout: 1000 * 60 * 60 * 24,
        success: function (result, textStatus) {
            if (Common.CheckErrorMsg(result, false) == false) {
                Common.Loading.Hide();
                Common.CheckErrorMsg(result);

                return;
            }

            if (typeof callback == "function")
                callback(result);
        },
        error: ((errback == null || errback == undefined) ? Common.AjaxError : function (XMLHttpRequest, textStatus, errorThrown) {
            Common.Loading.Hide();
            if (typeof errback == "function")
                errback(errorThrown);
        })
    });
}
Common.AjaxError = function (XMLHttpRequest, textStatus, errorThrown) {
    Common.Loading.Hide();
    if (XMLHttpRequest.responseText == "")
        return;

    try {
        alert(XMLHttpRequest.responseText);
    } catch (e) {
    };
}
Common.CheckErrorMsg = function (MsgStr, ShowMsg) {
    ShowMsg = (ShowMsg == undefined ? true : ShowMsg);
    if (MsgStr != null && !MsgStr.ResultCode) {
        if (MsgStr.substring(0, 13) == "|||[ERROR]|||") {
            var mString = MsgStr.replace("|||[ERROR]|||", "");
            if (ShowMsg == true) {
                Common.Msg(mString);
            }
            return false;
        }
    } else if (!MsgStr) {
        Common.Msg("Message Is Null!");
        return false;
    }
    return true;
}

//형식변환
Common.Convert = {
    Bool: function (value) {
        if (!value) return false;

        var RtnValue = false;

        if (value == 1 || value == "1") {
            RtnValue = true;
        } else if (value != "" && value.toLowerCase() == "true") {
            RtnValue = true;
        } else {
            RtnValue = false;
        }

        return RtnValue;
	},
    Int: function (value, DefValue) {
        if (isNaN(value) == true) value = 0;
        if (DefValue == undefined) DefValue = 0;
        if (value == "")
            value = DefValue;

        try {
            if (value == null) return DefValue;
            value = value.toString();

            if (value.indexOf(",") > -1) {
                value = value.replaceAll(",", "");
            }

            if (value.indexOf(".") == -1) {
                return parseInt(value, 10);
            } else {
                var Tmp = value.split(".");
                return parseInt(Tmp[0], 10);
            }
        } catch (e) {
            return DefValue;
        }
    },
    FileSize: function (bytes) {
        bytes = Common.Convert.Int(bytes);

        var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];

        if (bytes == 0) return '0MB';

        var round = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));

        if (round == 0) return bytes + ' ' +sizes[round];
            return (bytes / Math.pow(1024, round)).toFixed(1) + ' ' +sizes[round];
    },
    IntToRGB: function (value) {
        return "#" + ('000000' +(Common.Convert.Int(value) & 0xFFFFFF).toString(16)).slice(-6);
    },
    DecimalToHex: function (number) {
        if (number < 0) {
            number = 0xFFFFFFFF +number +1;
        }
        return number.toString(16).toUpperCase();
    }
};

//EnterKey 체크
Common.Enter = function (e, textareaCheck) {
    textareaCheck = (textareaCheck || false);

    var EventObj;
    // Event Setting
    if (window.event) {
        EventObj = window.event;
    } else {
        EventObj = e;
    }

    if (EventObj.keyCode == 13 && (textareaCheck == false || (textareaCheck == true && EventObj.srcElement.tagName != "TEXTAREA"))) {
        return true;
    } else {
        return false;
    }
}

Common.Load = function (Obj) {
    Obj = (Obj || $("body"));

    Obj.find('input[NumberKey]').each(function () {
        var thisObj = $(this);

        $(this).bind("keyup", function (e) {
            Common.NumberKeyPress(thisObj);
        });
    });

    // Input Enter Event Set
    Obj.find("input[search]").each(function () {
        var BtnId = $(this).attr("search");

        if (Obj.find("#" + BtnId).length > 0) {
            $(this).bind("keypress", function (e) {
                if (Common.Enter(e) == true) {
                    Obj.find("#" + BtnId).click();
                }
            });
        }
    });

    Obj.find('.cui_file_field').each(function () {
        var thisObj = $(this);
        var inputObj = $('<input type="text" class="cui_text_field" readonly>');
        var buttonObj = $('<button type="button" class="cui_button"><span data-langsid="찾아보기">' + siteLang.getLang("찾아보기") + '</span></button>');
        var delAreaObj = $('<div class="item_content" style="width:40px;"></div>');
        var delObj = $('<button type="button" class="cui_button icon endpoint" data-cui-icon="xmark" style="opacity:0;"><span data-langsid="삭제">' + siteLang.getLang("삭제") + '</span></button>');

        $(this).css("opacity", "0");
        $(this).css("position", "fixed");
        $(this).css("width", "0");
        inputObj.css("border-top-right-radius", "0");
        inputObj.css("border-bottom-right-radius", "0");
        inputObj.css("border-right", "0");
        buttonObj.css("border-top-left-radius", "0");
        buttonObj.css("border-bottom-left-radius", "0");

        inputObj.bind("click", function () {
            if ($(this).val() == "") {
                thisObj.click();
            }
        });
        buttonObj.bind("click", function () {
            thisObj.click();
        });
        delObj.bind("click", function () {
            thisObj.val("");
            inputObj.val("");
            delObj.css("opacity", "0");
        });

        delAreaObj.append(delObj);
        thisObj.before(inputObj);
        thisObj.before(buttonObj);
        thisObj.before(delAreaObj);

        thisObj.bind("change", function () {
            inputObj.val(thisObj.val());
            if (thisObj.val() != "") {
                delObj.css("opacity", "1");
            }
        });
    });

    Obj.find('input[datePickerControl]').each(function () {
        var thisObj = $(this);
        thisObj.datepicker({
            dateFormat: "yy-mm-dd",  // 날짜 형식 (예: 2024-11-22)

        }).css('z-index', 999);
        var nextObj = thisObj.next();
        if (nextObj && nextObj.attr("data-cui-icon") && nextObj.attr("data-cui-icon") == "calendar") {
            nextObj.bind("click", function () {
                if (thisObj.datepicker("widget").is(":visible")) {
                    thisObj.datepicker("hide"); // datepicker 숨기기
                } else {
                    thisObj.datepicker("show"); // datepicker 보이기
                }
            });
        }
    });

    Obj.find('input[datePickerControlModal]').each(function () {
        var thisObj = $(this);
        thisObj.datepicker({
            dateFormat: "yy-mm-dd",  // 날짜 형식 (예: 2024-11-22)

        }).css('z-index', 9999);
        var nextObj = thisObj.next();
        if (nextObj && nextObj.attr("data-cui-icon") && nextObj.attr("data-cui-icon") == "calendar") {
            nextObj.bind("click", function () {
                if (thisObj.datepicker("widget").is(":visible")) {
                    thisObj.datepicker("hide"); // datepicker 숨기기
                } else {
                    thisObj.datepicker("show"); // datepicker 보이기
                }
            });
        }
    });

    Obj.find('textarea[editerMode=true]').each(function () {
        var thisObj = $(this);
        $(this).summernote({
            lang: 'ko-KR',
            toolbar: [
                // [groupName, [list of button]]
                ['style', ['bold', 'italic', 'underline']],
                ['fontname', ['fontname']],
                ['fontsize', ['fontsize']],
                ['color', ['color']],
                ['para', ['ul', 'ol', 'paragraph']],
                ['height', ['height']],
                ['codeview', ['codeview']],
                ['misc', ['undo', 'redo']]
            ],
            height: $(this).css("height"),
            dialogsInBody: true,
            disableDragAndDrop: false,
            fontNames: ['돋움', '돋움체', '굴림', '굴림체', '바탕', '바탕체', '궁서', 'Arial', 'Tahoma', 'Times New Roman', 'Verdana'],
            fontNamesIgnoreCheck: ['돋움'],
            fontName: '돋움',
            callbacks: {
                onEnter: function (e) {
                    // You may replace `c` with whatever key you want
                    if ((e.metaKey || e.ctrlKey) && e.which == 13) {
                        Common.EventCancelBubble(e);
                        Common.EventReturnValue(e);
                    }
                }
            }
        });

        thisObj.data("Text", function (text) {
            setTimeout(function () {
                thisObj.next().find(".panel-body").html(text);
            }, 100);
            thisObj.val(text);
        });
    });

    Obj.find("div[multiFileUpload]").each(function () {
        var thisObj = $(this);
        var fileName = thisObj.attr("multiFileUpload");

        var orgFileInfoDiv = thisObj.find("div");
        //var orgFileInfo = orgFileInfoDiv.split(",");
        var orgFileHTML = "";

        if (orgFileInfoDiv.length > 0) {
            orgFileInfoDiv.each(function () {
                var oneDivObj = $(this);
                //idx|namq|path|size
                var fileInfoData = oneDivObj.html();
                var splitData = fileInfoData.split("|");

                var AddFile = "";
/*
                AddFile += "<li>";
                AddFile += '<a href="#" orgFileInfo="" style="display:none" class="file_del" onclick="Common.FileDeleteOrg($(this));return false;"><img src="/images/ico_file_del.gif" width="16" height="" alt="" /></a> ';
                AddFile += '<em class="ico_file"></em> ';
                AddFile += '<label class="file_name" style="cursor:pointer;">' + oneDivObj.html() + '</label>';
                AddFile += '<span class="file_size"> <em class="file_trans_gauge"><em class="gauge_in" style="width:0%">&nbsp;</em></em></span>';
                AddFile += "</li>";
*/

                AddFile += "<li>";
                AddFile += '<a href="#" orgFileInfo='+splitData[0]+' style="display:" class="file_del" onclick="Common.FileDeleteOrg($(this));return false;"><img src="/images/ico_file_del.gif" width="16" height="" alt="" /></a> ';
                AddFile += '<em class="ico_file ' + Common.FileExe(splitData[1]) + '"></em> ';
                AddFile += '<label class="file_name" style="cursor:pointer;">' + splitData[1] + '</label>';
                AddFile += '<span class="file_size">' + Common.Convert.FileSize(splitData[3]) + ' </span>';
                AddFile += "</li>";


                /*
                AddFile += "<li>";
                AddFile += '<a href="#" orgFileInfo="1" style="display:" class="file_del" onclick="Common.FileDeleteOrg($(this));return false;"><img src="/images/ico_file_del.gif" width="16" height="" alt="" /></a> ';
                AddFile += '<em class="ico_file "></em> ';
                AddFile += '<label class="file_name" style="cursor:pointer;">파일명.xmls</label>';
                AddFile += '<span class="file_size">23 Bytes</span>';
                AddFile += "</li>";
                */

                orgFileHTML += AddFile;
            });
        }

        var fileHtml = '<div class="form_file_wrap" oncontextmenu="return false" filename="{fileName}_FILE" uploadtype="multi">';
        fileHtml += '    <div class="ar_fl">';
        fileHtml += '        <button type="button" class="cui_button" onclick="$(\'#{fileName}_FILE_file_input\').trigger(\'click\');" style="height:20px;font-size:13px;min-width:8px;">FILE</button>';
        fileHtml += '    </div>';
        fileHtml += '    <div class="ar_fr">';
        fileHtml += '        <span class="form_txt_srmy">Size : </span>';
        fileHtml += '        <span id="{fileName}_FILE_size_srmy" class="form_txt_srmy st3" filesize="0">0KB</span>';
        fileHtml += '    </div>';
        fileHtml += '    <div class="file_list_wrap" style="">';
        fileHtml += '       <input type="file" class="file_hidden" multiple="true" id="{fileName}_FILE_file_input" name="{fileName}_FILE_file_input" dragfiles="" uploadfilename="{fileName}_FILE">';
        fileHtml += '       <p class="file_cmt" style="line-height:58px;margin:0px;display:{fileListSummary};" data-langsid="여러개의 파일을 마우스로 끌어놓으세요.">' + siteLang.getLang("여러개의 파일을 마우스로 끌어놓으세요.") + '</p>';
        fileHtml += '       <ul class="file_list" id="{fileName}_FILE_file_list_area" style="display:{fileListShow};margin:0px;">{orgFileList}</ul>';
        fileHtml += '    </div>';
        fileHtml += '</div>';

        if (orgFileHTML != "") {
            thisObj.html(fileHtml.replaceAll("{fileName}", fileName).replaceAll("{orgFileList}", orgFileHTML).replace("{fileListShow}", "").replace("{fileListSummary}", "none"));
        } else {
            thisObj.html(fileHtml.replaceAll("{fileName}", fileName).replaceAll("{orgFileList}", "").replace("{fileListShow}", "none").replace("{fileListSummary}", ""));
        }
    });

    // File Event Set
    Obj.find(".form_file_wrap").each(function () {
        var thisObj = $(this);
        var fileName = thisObj.attr("fileName");
        var fileInputObj = thisObj.find("#" + fileName);
        var show_hide_btn = thisObj.find("button[iType=showhide]");
        var fileDragShowHide = Cookies("comfdsh");

        if (fileDragShowHide == "hide") {
            Cookies("comfdsh", "hide", 365);
            show_hide_btn.html("<i class=\"fa fa-chevron-down\"></i> 펼치기");
            thisObj.find(".file_list_wrap").css("display", "none");
        } else {
            show_hide_btn.html("<i class=\"fa fa-chevron-up\"></i> 숨기기");
            thisObj.find(".file_list_wrap").css("display", "");
        }
        show_hide_btn.bind("click", function () {
            if (Common.Contains(show_hide_btn.find("i").attr("class"), "fa-chevron-up")) {
                show_hide_btn.html("<i class=\"fa fa-chevron-down\"></i> 펼치기");
                thisObj.find(".file_list_wrap").css("display", "none");
                Cookies("comfdsh", "hide", 365);
            } else {
                show_hide_btn.html("<i class=\"fa fa-chevron-up\"></i> 숨기기");
                thisObj.find(".file_list_wrap").css("display", "");
                Cookies("comfdsh", "", 365);
            }
        });


        thisObj.find(".file_hidden").bind("change", function () {
            var fileObj = $(this)[0];
            for (var i = 0; i < fileObj.files.length; i++) {
                var file = fileObj.files[i];
                Common.FileUpload(file, fileInputObj, thisObj.find("span.st3"), thisObj.find(".file_cmt"), thisObj.find(".file_list"), thisObj);
            }
            fileObj.value = "";
            show_hide_btn.html("<i class=\"fa fa-chevron-up\"></i> 숨기기");
            thisObj.find(".file_list_wrap").css("display", "");
        });

        thisObj.bind("drop", function (e) {
            if (typeof FileList == 'undefined') {
                alert("File Drag & Drop 미지원 브라우저 혹은 버전입니다. 다른 브라우저를 이용하거나 브라우저를 업데이트 해주세요.");
                return;
            }

            var fileObj = "";

            if (e.originalEvent == undefined) {
                fileObj = e.dataTransfer;
            } else {
                fileObj = e.originalEvent.dataTransfer;
            }

            if (!fileObj.files) {
                alert("File Drag & Drop 미지원 브라우저 혹은 버전입니다. 다른 브라우저를 이용하거나 브라우저를 업데이트 해주세요.");
            } else {
                for (var i = 0; i < fileObj.files.length; i++) {
                    var file = fileObj.files[i];
                    Common.FileUpload(file, fileInputObj, thisObj.find("span.st3"), thisObj.find(".file_cmt"), thisObj.find(".file_list"), thisObj);
                }
            }

            // 에디터기 변경으로 인해 이벤트 멈춤 빼기
            //Common.EventReturnValue(e);
            //Common.EventCancelBubble(e);
            return;
        });

        // 파일 초기화 기능 추가
        fileInputObj.data("Init", function () {
            thisObj.find(".file_cmt").css("display", "");
            thisObj.find(".file_list").css("display", "none");
            thisObj.find(".file_list").html("");
        });
    });
}

Common.FileDeleteOrg = function (FileOrgObj) {
    var form_file_wrap_Obj = FileOrgObj.parent().parent().parent().parent();

    //var orgFileInfo = FileOrgObj.attr("orgFileInfo");
    if (confirm("파일을 삭제하시겠습니까?") == true) {
        // 실제 Ajax로 파일 삭제 처리
        var idx = FileOrgObj.attr("orgFileInfo");
        // 개별 페이지에 삭제 함수 생성
        DeleteOrgFile(idx);

        // 삭제된 정보 html 제거
        FileOrgObj.parent().remove();

        //Common.FileDelete(size_srmyObj, noListObj, listObj, controllObj);
    }
}

Common.FileUpload = function (file, fileInputObj, size_srmyObj, noListObj, listObj, controllObj) {
    var AddFile = "";
    AddFile += "<li>";
    AddFile += '<a href="#" style="display:none" class="file_del" onclick="return false;"><img src="/images/ico_file_del.gif" width="16" height="" alt="" /></a> ';
    AddFile += '<em class="ico_file ' + Common.FileExe(file.name) + '"></em> ';
    AddFile += '<label class="file_name" style="cursor:pointer;">' + file.name + '</label>';
    AddFile += '<span class="file_size">' + Common.Convert.FileSize(file.size) + ' <em class="file_trans_gauge"><em class="gauge_in" style="width:0%">&nbsp;</em></em></span>';
    AddFile += "</li>";
    var AddFileObj = $(AddFile);

    noListObj.css("display", "none");
    listObj.css("display", "");
    listObj.append(AddFileObj);

    var fileName = Common.GetTodayTimeString();

    //khs modify
//    var fileName = "";
//    if(controllObj.attr("dragfile") == undefined){
//        fileName = Common.GetTodayTimeString();
//    }else{
//        fileName = controllObj.attr("dragfile");
//    }


    var dragFileCount = 1;
    if (controllObj.attr("dragFileCount") == undefined) {
        controllObj.attr("dragFileCount", "1");
    } else {
        dragFileCount = Common.Convert.Int(controllObj.attr("dragFileCount")) + 1;
    }
    AddFileObj.attr("dragFileCount", dragFileCount);

    if (controllObj.attr("dragFile") != undefined && controllObj.attr("dragFile") != "") {
        fileName = controllObj.attr("dragFile");
    } else {
        controllObj.attr("dragFile", fileName);
    }
    controllObj.find("input[type=file]").attr("dragFiles", fileName);

    var sendFileObj = Common.RequestInfo.Files[fileName];

    if (sendFileObj == undefined || sendFileObj == null) {
        var file_arr = [];
        file_arr.push({ dragFileCount: dragFileCount, file: file });
        Common.RequestInfo.Files[fileName] = file_arr;
    } else {
        sendFileObj.push({ dragFileCount: dragFileCount, file: file });
        Common.RequestInfo.Files[fileName] = sendFileObj;
    }

    AddFileObj.find(".gauge_in").parent().remove();
    AddFileObj.find("a.file_del").css("display", "");

    AddFileObj.find("a.file_del").bind("click", function () {
        if (confirm("파일을 삭제하시겠습니까?") == true) {
            var dragFileCount = AddFileObj.attr("dragFileCount");
            AddFileObj.remove();

            var sendFileObj = Common.RequestInfo.Files[fileName];

            for (var Idx in sendFileObj) {
                if (sendFileObj[Idx] && sendFileObj[Idx].dragFileCount == dragFileCount) {
                    delete sendFileObj[Idx];
                    Common.RequestInfo.Files[fileName] = sendFileObj;
                    break;
                }
            }

            Common.FileDelete(size_srmyObj, noListObj, listObj, controllObj);
        }
    });

    AddFileObj.attr("filesize", file.size);

    var filesize = Common.Convert.Int(file.size);
    var total_filesize = Common.Convert.Int(size_srmyObj.attr("filesize"));
    size_srmyObj.attr("filesize", total_filesize + filesize);
    size_srmyObj.html(Common.Convert.FileSize(total_filesize + filesize))
}

Common.FileDelete = function (size_srmyObj, noListObj, listObj, controllObj) {
    if (listObj.children().length == 0) {
        noListObj.css("display", "");
        listObj.css("display", "none");
        controllObj.removeAttr("dragFile");
    } else {
        noListObj.css("display", "none");
        listObj.css("display", "");
    }

    var tmpValue = "";
    var total_filesize = 0;

    listObj.children().each(function () {
        total_filesize += Common.Convert.Int($(this).attr("filesize"));
    });

    size_srmyObj.attr("filesize", total_filesize);
    size_srmyObj.html(Common.Convert.FileSize(total_filesize));
}

// 파일업로드
Common.FileExe = function (fileName) {
    var rtnValue = "zip";

    try {
        var fileNameArr = fileName.split(".");
        var ext = fileNameArr[fileNameArr.length - 1];

        if (ext.toLowerCase() == "jpg" || ext.toLowerCase() == "gif" || ext.toLowerCase() == "png" || ext.toLowerCase() == "jpeg" || ext.toLowerCase() == "bmp") {
            rtnValue = "img";
        } else if (ext.toLowerCase() == "pdf") {
            rtnValue = "pdf";
        } else if (ext.toLowerCase() == "doc" || ext.toLowerCase() == "ppt" || ext.toLowerCase() == "pptx" || ext.toLowerCase() == "xlsx" || ext.toLowerCase() == "xls" || ext.toLowerCase() == "hwp") {
            rtnValue = "doc";
        } else if (ext.toLowerCase() == "mp3" || ext.toLowerCase() == "mp4" || ext.toLowerCase() == "avi" || ext.toLowerCase() == "wmv" || ext.toLowerCase() == "flv" || ext.toLowerCase() == "mov") {
            rtnValue = "mov";
        }

    } catch (e) {
        rtnValue = "zip";
    }

    return rtnValue;
}

//현재 날짜시간 포함 string 리턴
Common.GetTodayTimeString = function () {
    var RtnValue = "";

    var date = new Date(); // 날짜
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var hour = date.getHours();
    var min = date.getMinutes();
    var sec = date.getSeconds();
    if (String(month).length == 1) { month = "0" +month; };
    if(String(day).length == 1) { day = "0" +day; };
    if (String(hour).length == 1) { hour = "0" +hour; };
    if(String(min).length == 1) { min = "0" +min; };
    if(String(sec).length == 1) { sec = "0" +sec; };

    RtnValue = String(year) + String(month) + String(day) + String(hour) + String(min) +String(sec);

    return RtnValue;
}

Common.DialogIndex = 1000;
Common.Dialog = function (info) {
    var diaObj = null;
    var obj = (info.obj || null);
    var url = (info.url || "");
    var name = (info.name || Common.GetTodayTimeString());
    var formMethod = (info.formMethod || "POST");
    var async, loading, blockClose, drag;
    var openFn = (info.openFn || undefined);
    var closeFn = (info.closeFn || undefined);

    if (info.blockClose == undefined) blockClose = true
    else blockClose = info.blockClose

    if (info.drag == undefined) drag = true
    else drag = info.drag

    var overlayObj = $('<div class="overlay"></div>');
    $('body').append(overlayObj);

    if (obj) {
        Common.DialogIndex = Common.DialogIndex + 100;
        overlayObj.css("opacity", "0%");
        overlayObj.show(); // 오버레이 배경 표시
        overlayObj.css("z-index", Common.DialogIndex);
        overlayObj.animate({opacity: '100%'}, 500);
        obj.css("z-index", Common.DialogIndex + 1);
        obj.fadeIn({
            complete : function () {
                if (typeof openFn == "function") {
                    openFn(obj);
                }
            }
        });

        obj.find("button[dialogBtn=close]").bind("click", function () {
            obj.data("Hide")();
        });

        obj.data("Hide", function () {
            Common.DialogIndex = Common.DialogIndex - 100;
            overlayObj.animate({opacity: '0%'},{duration: 500, complete: function(){
                overlayObj.remove();
            }});
            obj.fadeOut({
                start : function () {
                    if (typeof closeFn == "function") {
                        closeFn(obj);
                    }
                }
            });
        });

        if (blockClose) {
            overlayObj.one("click", function () {
                obj.data("Hide")();
            });
        }

        if (drag) {
            var modalHeader = obj.find(".dialog_head");
            modalHeader.unbind("mousedown");
            modalHeader.unbind("mouseup");

            var isDragging = false;
            modalHeader.bind("mousedown", function (e) {
                isDragging = true;
            });
            modalHeader.bind("mouseup", function () {
                isDragging = false;
            });

            obj.draggable({
                start: function (event, ui) {
                    if (!isDragging) {
                        return false;
                    }
                }, drag: function( event, ui ) {
                    if (isDragging) {
                        ui.position.left = ui.position.left + (obj.width() / 2);
                        ui.position.top = ui.position.top + (obj.height() / 2);
                    } else {
                        return false;
                    }
                }
            });
        }
    } else {
        if (url == "")
            return;

        if (info.async == undefined) async = false
        else async = info.async

        if (info.loading == undefined) loading = false
        else loading = info.loading

        var reqInfo = new Common.RequestInfo();

        for (var keyName in info.param) {
            reqInfo.AddParameter(keyName, info.param[keyName]);
        }

        setTimeout(function () {
            Common.Ajax(url, reqInfo, function (html) {
                var diaObj = $(html);
                siteLang.showLangs(diaObj);
                Common.Load(diaObj);

                $('body').append(diaObj);
                obj = diaObj;

                Common.DialogIndex = Common.DialogIndex + 100;
                overlayObj.css("opacity", "0%");
                overlayObj.show(); // 오버레이 배경 표시
                overlayObj.css("z-index", Common.DialogIndex);
                overlayObj.animate({opacity: '100%'}, 500);
                obj.css("z-index", Common.DialogIndex + 1);
                obj.fadeIn({
                    complete : function () {
                        if (typeof openFn == "function") {
                            openFn(obj);
                        }
                    }
                });

                obj.find("button[dialogBtn=close]").bind("click", function () {
                    obj.data("Hide")();
                });

                obj.data("Hide", function () {
                    Common.DialogIndex = Common.DialogIndex - 100;
                    overlayObj.animate({opacity: '0%'},{duration: 500, complete: function(){
                        overlayObj.remove();
                    }});
                    obj.fadeOut({
                        complete : function () {
                            if (typeof closeFn == "function") {
                                closeFn(obj);
                            }
                            obj.remove();
                        }
                    });
                });

                if (blockClose) {
                    overlayObj.one("click", function () {
                        obj.data("Hide")();
                    });
                }

                if (drag) {
                    var modalHeader = obj.find(".dialog_head");
                    modalHeader.unbind("mousedown");
                    modalHeader.unbind("mouseup");

                    var isDragging = false;
                    modalHeader.bind("mousedown", function (e) {
                        isDragging = true;
                    });
                    modalHeader.bind("mouseup", function () {
                        isDragging = false;
                    });

                    obj.draggable({
                        start: function (event, ui) {
                            if (!isDragging) {
                                return false;
                            }
                        }, drag: function( event, ui ) {
                            if (isDragging) {
                                ui.position.left = ui.position.left + (obj.width() / 2);
                                ui.position.top = ui.position.top + (obj.height() / 2);
                            } else {
                                return false;
                            }
                        }
                    });
                }
            }, { formMethod: formMethod, async: async });
        }, 10);
    }
}

Common.Loading = new function (op) {
    var overlay = null;
    var Option = {
        msg: "Loading", duration: 0
    };

    // 옵션 설정
    this.Options = function (OrgOption, NewOption) {
        if (NewOption) {
            for (var keyName in NewOption) {
                Option[keyName] = NewOption[keyName];
            }
        }
        return Option;
    };
    Option = this.Options(Option, op);

    this.Show = function (op) {
        Option = this.Options(Option, op);

        if (overlay != null) {
            overlay.hide();
        }

        var opts = {
            lines: 13, // The number of lines to draw
            length: 11, // The length of each line
            width: 5, // The line thickness
            radius: 17, // The radius of the inner circle
            corners: 1, // Corner roundness (0..1)
            rotate: 0, // The rotation offset
            color: '#FFF', // #rgb or #rrggbb
            speed: 1, // Rounds per second
            trail: 60, // Afterglow percentage
            shadow: false, // Whether to render a shadow
            hwaccel: false, // Whether to use hardware acceleration
            className: 'spinner', // The CSS class to assign to the spinner
            zIndex: 2e9, // The z-index (defaults to 2000000000)
            top: 'auto', // Top position relative to parent in px
            left: 'auto' // Left position relative to parent in px
        };
        var target = document.createElement("div");
        document.body.appendChild(target);
        var spinner = new Spinner(opts).spin(target);
        overlay = iosOverlay({
            text: Option.msg,
            duration: Option.duration,
            spinner: spinner
        });
    };

    this.Hide = function (Mode, Msg) {
        if (Mode == "Success") {
            Msg = (Msg || "성공");

            overlay.update({ icon: "/images/check.png", text: Msg });

            window.setTimeout(function () {
                overlay.hide();
            }, 500);
        } else {
            if (overlay != null) {
                overlay.hide();
            }
        }
    };

    this.Message = function (Msg) {
        overlay.update({
            text: Msg
        });
    };
};

//===========================================================================================
//제목 : Form
//===========================================================================================
Common.Validate = function (FormObj) {
    var RtnValue = true;

    if (FormObj == null) {
        return RtnValue;
    }

    FormObj.find("input,select,textarea").each(function (n) {
        if (typeof $(this).attr("type") != "undefined" && $(this).attr("type").toLowerCase() == "checkbox" && typeof $(this).attr("reqcheck") != "undefined") {
            if ((typeof $(this).attr("Multi") == "undefined" && $("input:checkbox[name='" + $(this).attr("name") + "']").is(":checked") == false) || (typeof $(this).attr("Multi") != "undefined" && $("input:checkbox[Multi='" + $(this).attr("Multi") + "']").is(":checked") == false)) {
                var thisObj = $(this);
                if (thisObj.attr("messages")) {
                    Common.Msg(thisObj.attr("messages"));
                } else {
                    Common.Msg("Enter the required input value.");
                }
                RtnValue = false;
                return false;
            }
        } else if (typeof $(this).attr("type") != "undefined" && $(this).attr("type").toLowerCase() == "radio" && typeof $(this).attr("reqcheck") != "undefined") {
            if ($("input[name=" + $(this).attr("name") + "]").is(":checked") == false) {
                var thisObj = $(this);
                if (thisObj.attr("messages")) {
                    Common.Msg(thisObj.attr("messages"));
                } else {
                    Common.Msg("Enter the required input value.");
                }
                RtnValue = false;
                return false;
            }
        } else if (typeof $(this).attr("reqcheck") != "undefined" && ($(this).val() == "" || $(this).val() == null)) {
            var thisObj = $(this);
            var alertMsg = "";
            if (thisObj.attr("messages")) {
                alertMsg = thisObj.attr("messages");
            } else {
                alertMsg = "Enter the required input value.";
            }

            Common.Msg(alertMsg, {
                okback: function () {
                    if (thisObj.attr("type") && thisObj.attr("type").toLowerCase() != "hidden")
                        thisObj.focus();
                }
            });
            RtnValue = false;
            return false;
        } else if (typeof $(this).attr("type") != "undefined" && $(this).attr("type") == "email" && $(this).val() != "") {
            var pattern = /^[_a-zA-Z0-9-\.\_]+@[\.a-zA-Z0-9-]+\.[a-zA-Z]+$/;
            if (pattern.exec($(this).val())) {
            } else {
                Common.Msg(siteLang.getLang("잘못입력된 이메일주소 입니다."));
                RtnValue = false;
                return false;
            }
        } else if ($(this).attr("passCheck") && $(this).attr("passCheck") != "") {
            if ($(this).val() != $("#" + $(this).attr("passCheck")).val()) {
                Common.Msg(siteLang.getLang("비밀번호와 비밀번호 확인정보가 다릅니다."));
                RtnValue = false;
                return false;
            }

            if (!Common.ChkPwd($.trim($(this).val()))) {
                Common.Msg(siteLang.getLang("비밀번호를 확인하세요.<br>(영문,숫자를 혼합하여 10~20자 이내)"));
                RtnValue = false;
                return false;
            }
        }
    });
    return RtnValue;
}

Common.ChkPwd = function (str) {
    var reg_pwd = /^.*(?=.{10,20})(?=.*[0-9])(?=.*[a-zA-Z]).*$/;
    if (!reg_pwd.test(str)) {
        return false;
    }
    return true;
}

Common.NumberKeyPress = function (Obj) {
    if (/[^0-9\-]/g.test(Obj.val())) {
        Obj.val("");
    }
}
Common.NumberCommaKeyPress = function (Obj) {
    if (/[^0-9,.\-]/g.test(Obj.val())) {
        Obj.val("");
    }
}
Common.NumCheck = function (num, Separator, DecLength) {
    if (Separator == null || Separator == undefined) {
        Separator = "";
    }
    if (DecLength == null || DecLength == undefined) {
        DecLength = 0;
    }

    var sign = "";
    num = num.toString().replaceAll(",", "");

    if (isNaN(num)) {
        //Common.Msg("숫자만 입력할 수 있습니다.");
        return 0;
    }

    if (num == 0) {
        return 0;
    }

    if (Separator == "")
        return num;

    if (num < 0) {
        num = num * (-1);
        sign = "-";
    } else {
        num = num * 1;
    }

    num = num.toString();

    var temp = "";
    var pos = Common.Convert.Int(DecLength);
    var num_len = num.length;

    if (pos == 0)
        return num;

    while (num_len > 0) {
        num_len = num_len - pos;
        if (num_len < 0) {
            pos = num_len + pos;
            num_len = 0;
        }
        temp = Separator + num.substr(num_len, pos) + temp;
    }
    return sign + temp.substr(1);
}

Common.ChkEmail = function (str) {
    var pattern = /^[_a-zA-Z0-9-\.\_]+@[\.a-zA-Z0-9-]+\.[a-zA-Z]+$/;
    if (pattern.exec(str)) {
        return true;
    } else {
        return false;
    }
}

Common.EncParam = function(value) {
    var val = value || {};

    // 객체가 아닌 경우 그냥 URL-safe로 변환
    if (typeof val === "string") {
        return encodeURIComponent(val);
    } else {
        // 객체일 경우 JSON.stringify로 변환 후 URL-safe하게 인코딩
        var jsonString = JSON.stringify(val);
        return encodeURIComponent(jsonString);  // URL-safe하게 변환
    }
}

String.prototype.replaceAll = function (org, rep) {
    var temp_str = this.trim();
    try {
        temp_str = temp_str.split(org).join(rep);
    } catch (e) { }
    return temp_str;
}

String.prototype.trim = function () {
    return this.replace(/(^\s*)|(\s*$)/gi, "");
}
Number.prototype.trim = function () {
    return this.replace(/(^\s*)|(\s*$)/gi, "");
}

Common.SelectBox = function(selectId, dataList, textName, valueName, defaultText = "") {
    var $select = $(selectId); // 셀렉트 박스를 jQuery로 선택

    // 셀렉트 박스를 초기화 (기존 옵션들 지우기)
    $select.empty();

    // 기본 선택 옵션 추가
    $select.append('<option value="">' + defaultText + '</option>');

    // dataList 배열을 순회하며 셀렉트 박스에 옵션 추가
    $.each(dataList, function(index, item) {
        var optionText = item[textName];  // 'name' 필드 값
        var optionValue = item[valueName];  // 'code' 필드 값

        // 셀렉트 박스에 <option> 추가
        $select.append('<option value="' + optionValue + '">' + optionText + '</option>');
    });

    // 기본 선택 인덱스를 설정 (선택된 인덱스를 0으로 설정)
    $select.prop('selectedIndex', 0);
}

$(document).ready(function () {
    Common.Load();
});


//그리드 설정
var commonSettings = {
    sortable: false,
    resizable: true,
    menuIcon: true,
    scrollModel: { autoFit: false },
    hoverMode: 'row',
    rowHtHead: 30,
    rowHt: 30,
    roundCorners: false,
    rowBorders: true,
    showBottom: true,
    showHeader: true,
    showTitle: true,
    showTop: false,
    showToolbar: true,
    stripeRows: true,
    wrap: false,
    autoAddRow: false,
    freezeCols: 0,
    autoRow: false,
    autoRowHead: false,
    rowInit: function () {
                return { style: 'font-size:12px;' };
            },
            selectChange: function (event, ui) {
            },
            rowClick: function (event, ui) {
            },
            cellClick: function (event, ui) {
            },
            rowDblClick: function (event, ui) {
            },
            rowSelect: function (event, ui) {
            },
            open: function () {
            },
            headerCellClick: function (event, ui) {
            }
};

function getFileHash(file, callback) {
    var reader = new FileReader();
    reader.onload = function(event) {
        var hash = 0;
        var data = new Uint8Array(event.target.result);
        for (var i = 0; i < data.length; i++) {
            hash = (hash << 5) - hash + data[i];
            hash |= 0;  // 32-bit overflow
        }
        callback(hash); // 계산된 해시값을 콜백으로 전달
    };
    reader.readAsArrayBuffer(file); // 파일을 읽어 배열 버퍼로 변환
}