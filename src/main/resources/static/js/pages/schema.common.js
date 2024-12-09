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

    var responseType, formMethod, async, errback, okErr;

    if (info == undefined) {
        formMethod = "POST";
    } else {
        if (info.async == undefined) async = false
        else async = info.async

        formMethod = (info.formMethod || "POST");
        responseType = (info.responseType || undefined);
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
                type: "POST", contentType: "application/x-www-form-urlencoded;charset=utf-8", processData: true
            };
        } else {
            AType = {
                type: "POST", contentType: false, processData: false
            };
        }
        Parameters = data.GetJson();
    }

    $.ajax({
        type: AType.type,
        contentType: AType.contentType,
        processData: AType.processData,
        headers: {
            "cache-control": "no-cache", "pragma": "no-cache", "requestType": "ajax"
        },
        async: async,
        cache: false,
        url: url,
        data: Parameters,
        dataType: (responseType || "text"),
        timeout: 1000 * 60 * 60 * 24,
        success: function (result, textStatus) {
            if (typeof callback == "function")
                callback(result);
        },
        error: ((errback == null || errback == undefined) ? Common.AjaxError : function (XMLHttpRequest, textStatus, errorThrown) {
            //Common.Loading.Hide();
            if (typeof errback == "function")
                errback(errorThrown);
        })
    });
}
Common.AjaxError = function (XMLHttpRequest, textStatus, errorThrown) {
    //Common.Loading.Hide();
    if (errorThrown == "")
        return;

    try {
        alert(errorThrown);
    } catch (e) {
    };
}

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

    // Input Enter Event Set
    Obj.find("input[search]").each(function () {
        var BtnId = $(this).attr("search");

        if ($("#" + BtnId).length > 0) {
            $(this).bind("keypress", function (e) {
                if (Common.Enter(e) == true) {
                    $("#" + BtnId).click();
                }
            });
        }
    });
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
                complete : function () {
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

$(document).ready(function () {
    Common.Load();
});