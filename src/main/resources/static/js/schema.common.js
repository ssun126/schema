// form 데이터 -> json
$.fn.serializeObject = function()
{
   var o = {};
   var a = this.serializeArray();

   $.each(a, function() {
	   if(this.value == null || this.value == ''){
		   return;
	   }
	   if (o[this.name]) {
           if (!o[this.name].push) {
               o[this.name] = [o[this.name]];
               o[this.name] = [o[this.name]];
           }
           o[this.name].push(this.value || '');
       } else {
           o[this.name] = this.value || '';
       }
   });
   return o;
};

//ajax call
function apiRequest(apiUrl, param, type, loading = false,xtable = null) {

	$("[role=tooltip]").remove();

	var $d = $.Deferred();
	var data = param == null ? {} : param;
	var isWindowLoading = loading; // window 전체 로딩
	var isXtableLoading = !loading && xtable; // xtable의 로딩
	var xhr = {
			isApi : true,
			ajax : null,
			response : false,
			requestTime : moment().format('YYYY-MM-DD HH:mm:ss')
	};

	if(isWindowLoading){
		loadingStart();
		$("button").blur();
	}
	else if(isXtableLoading){
		xtable.showLoading();
		$("button").blur();
	}

	if(xtable){
		if(!xtable.nodeName){
			xLoading(xtable);
			$(xtable.selector).find('.tr-filter-box').find("input").val("");
		}
	}

	if(param != null && typeof param.serialize == 'function'){
		if(type.toUpperCase() == 'GET'){
			data = param.serializeObject();
		}
		else{
			data = param.serializeObjectCustom();
		}
	}

	xhr.ajax = $.ajax({
		url : contextPath + apiUrl,
		type : type,
		data : type.toUpperCase() == 'GET' ? data : JSON.stringify(data),
		dataType : 'JSON',
		contentType: 'application/json;charset=UTF-8',
		success : function(data){
			if(apiSuccessFnc(apiUrl, param, loading, data, xtable, xhr)){
	    		$d.resolve(data);
	    	}else{
	    		$d.reject(data);
	    	}
		},
		error : function(reason){
			if(apiErrorFnc(apiUrl, param, loading, reason, xhr)){
				$d.reject(reason);
			}
		}
	});
	_topLocalStorage.xmlHttpRequestWaitList.unshift(xhr);

	return $d.promise();
}

function fncErrorMessage(reason,apiUrl = null,param = null){
	var message = LL.commonAlertMessageError;

	if(reason.returnCode != undefined){
		if(reason.errorCode == "401"){
			message = ( reason.errorMessage || '' )+ " " + LL.commonAlertPleaseLogin;
			setTimeout(function(){
				location.href = contextPath + "/t/logout";
			},1000 * 1);
		}else{
			message = reason.errorMessage;
		}
	}

	cpnAlert(message,'error');
}

function successMessage(data,apiUrl,param){
	console.log(apiUrl + " [[SUCCESS]] :: request",param," response",data);
}

function apiSuccessFnc (apiUrl, param, loading, data, xtable, xhr = null) {
	var isWindowLoading = loading; // window 전체 로딩
	var isXtableLoading = !loading && xtable; // xtable의 로딩

	if(isWindowLoading){
		loadingEnd();
		$("button").blur();
	}
	else if(isXtableLoading){
		xtable.hideLoading();
		$("button").blur();
	}

	if(param == null){
		param = "";
	}

	if(xhr){
		xhr.response = true;
		_topLocalStorage.xmlHttpRequestWaitList.pop(xhr);
	}

	if(data.returnCode == undefined || data.returnCode == "F"){
		fncErrorMessage(data,apiUrl,param);
		xError(xtable);

		return false;
	}
	else{
		if(!data.data){
			data['data'] = {result : null};
		}
		if(data.data.pagination){
			xNoData(data.data.pagination.totalCount || 0, xtable);
		}
		successMessage(data,apiUrl,param);

		return true;

	}
}

function apiErrorFnc (apiUrl, param, loading, reason, xhr = null) {
	var isWindowLoading = loading; // window 전체 로딩
	var isXtableLoading = !loading && xtable; // xtable의 로딩

	if(isWindowLoading){
		loadingEnd();
		$("button").blur();
	}
	else if(isXtableLoading){
		xtable.hideLoading();
		$("button").blur();
	}

	if(xhr){
		xhr.response = true;
		_topLocalStorage.xmlHttpRequestWaitList.pop(xhr);
	}

	xError();
	fncErrorMessage(reason,apiUrl,param);
	return true;
}

// jsp 파일 특정영역에 include (본 프로젝트에서는 모달 include로 사용)
// 특정영역을 지정하는것보다 렌덤값으로 id생성해서 만드는것으로 구현
function includeJsp($includeWrap,fileUrl, successFnc = null)
{
	var xhr = {
			isApi : true,
			ajax : null,
			response : false,
			requestTime : moment().format('YYYY-MM-DD HH:mm:ss')
	};

	if(fileUrl == null)
	{
		return;
	}
	if (window['cpn_modal_open'])
	{
		return;
	}
	window['cpn_modal_open'] = true;

	if(!successFnc){
		successFnc = function(){};
	}

	$includeWrap.empty();
	xhr.ajax = $includeWrap.load(fileUrl,function(){
		window['cpn_modal_open'] = false;
		successFnc();
	});

	_topLocalStorage.xmlHttpRequestWaitList.unshift(xhr);

	$(document).on("submit", "form", function() {
		return false;
	});
}
//null, "" 값 체크

// isEmpty(0) : true
// isEmpty("") : false
function isEmpty(obj){
	if(obj == null || obj == ""){
		return true;
	}
	return false;
}

// 0 or ''인지 반환
// isZeroOrEmptyByType(0) : true
// issZeroOrEmptyByType('0') : true
function isZeroOrEmptyByType(data){
	if(data == null || data == undefined){
		return false;
	}

	// number 0, string '0' : true
	switch(typeof(data)){
	case "string":
		return !$.isEmptyObject(data);
		break;
	case "number":
		return $.isNumeric(data);
	default :
		break;
	}
}
function replaceStrByType(data){
	return isZeroOrEmptyByType(data) ? data : '';
}
function replaceNumByType(data){
	return isZeroOrEmptyByType(data) ? data : 0;
}



//문자열의 byte구하기

function getByteLength(s,b,i,c){
    for(b=i=0;c=s.charCodeAt(i++);b+=c>>11?3:c>>7?2:1);
    return b;
}


/*
문자열 포맷
ex. replaceMessage('{0}는 {1}이다.', 'a', 'b'); -> ;a는 b이다.'*/

function replaceMessage(str, ...param){
// var str = "잘못된 파라미터 ''{0}''(empty). {1} {2} {3} {0}";
// var param = ["p1", "p2", "p3", "4"];
	if(str){
		str = str.replace(/''/g,"'");
		for(var i = 0;i < param.length; i++){
			var regexp = new RegExp("\\{"+i+"\\}", "g");
			str = str.replace(regexp, param[i]);
		}
	}

	return str || '';
}


//문자열 텍스트의 픽셀 구하기
function getTextWidth(text,font) {

    inputText = text;
    canvas = document.createElement("canvas");
    context = canvas.getContext("2d");
    context.font = font;
    width = context.measureText(inputText).width;

    return  Math.ceil(width);
}



// 숫자만 반환
function onlyNumber(event){
	event = event || window.event;
	var keyID = (event.which) ? event.which : event.keyCode;
	if ( (keyID >= 48 && keyID <= 57) || (keyID >= 96 && keyID <= 105) || keyID == 8 || keyID == 46 || keyID == 37 || keyID == 39 || keyID == 190 || keyID == 9 || keyID == 110)
		return;
	else
		return false;
}

// 글자+특수문자 지우기 -> 숫자만 반환
function removeChar($input) {
	return $input.val().replace(/[^0-9]/g, "");
}

function removeChar2(event) {
	event = event || window.event;
	var keyID = (event.which) ? event.which : event.keyCode;
	if ( keyID == 8 || keyID == 46 || keyID == 37 || keyID == 39 )
		return;
	else
		event.target.value = event.target.value.replace(/[^0-9]/g, "");
}

// 특수문자 지우기
function removeExpChar(event){
	var regExp = /[\{\}\[\]\/?.,;:|\)*~`!^\-+<>@\#$%&\\\=\(\'\"]/gi;

	return event.replace(regExp, "");
}


// 회계(통화) 포맷
function applyNumFmt(num) {
	if(!$.isNumeric(num)) {
		if(num == null || num == undefined || num == '') {
			return 0;
		}
		return num;
	}

	var isMinus = false;
	if(num == 0 || num == '0') {
		return 0;
	}
	if(num < 0) {
		num *= -1;
		isMinus = true;
	}
	num = ''+num;
	var rult = '';
	while(num.length>3) {
		var temp = num.substring(num.length-3);
		num = num.substring(0, num.length-3);
		rult = temp + rult;
		rult = ',' + rult;
	}
	rult = num + rult;
	if(isMinus) rult = '-'+rult;
	return rult;
}

// 회계(통화) 포맷 제거
function removeNumFmt(num) {
	return parseInt(num.replace(/,/g, ''));
}



// 숫자 Integer 체크
function numberChkInteger($input,name,minus = false){
	var flag = true;

	var max = 2147483647;
	var min = -2147483648;
	var value = $input.val();

	var message = LL.commonAlertMessageBigInteger;

	if(max < value){
		flag = false;
	}else{
		if(!minus && value < 0){
			flag = false;
			message = LL.commonAlertMessageNotMinus;
		}else if(minus && value < min){
			flag = false;
			message = LL.commonAlertMessageSmallInteger;
		}
	}


	if(!flag){
		cpnAlert(replaceMessage(message,name),'warning');
	}

	return flag;
}


// 입력폼 초기화
function resetInput($inputForm, hiddenChkFlag = true, disabledChkFlag = true){

	if(hiddenChkFlag){
		$(`input[type=hidden]`,$inputForm).val(null);
	}

	$(`input[type=password]`,$inputForm).val(null);
	$(`input[type=text]`,$inputForm).val(null);
	$(`input[type=number]`,$inputForm).val(null);
	$(`input[type=email]`,$inputForm).val(null);
	$(`input[type=checkbox]`,$inputForm).prop("checked", false);
	$(`input[type=radio]`,$inputForm).prop("checked", false);
	$(`textarea`,$inputForm).val(null);

	$.each($(`.dropdown-menu`,$inputForm),function(index, item){
		$(`a:eq(0)`,$(item)).trigger("click");
	});

	$.each($('select',$inputForm),function(index, item){
		item = $(item);
		$('option:eq(0)',item).prop('selected',true);
	});

	// disabled false
	if(disabledChkFlag){
		$(`input, button, textarea`,$inputForm).prop('disabled',false);
	}
}


// 날짜 기본값 설정
function resetDate($stInput , $fnsInput, compVal, compUnit)
{
	$stInput.val(moment().add(compVal * -1, compUnit).format('YYYY-MM-DD'));
	$fnsInput.val(moment().format('YYYY-MM-DD'));
}


// 날짜 비교
function dateComparison(targetDate, compDate, type = 'YYYYMMDD'){
	// targetDate가 compDate보다 이전(<=)이면 true, 아니면(>) false
	var flag = false;
	var targetDateNum = Number(moment(targetDate).format(type));
	var compDateNum = Number(moment(compDate).format(type));

	if(targetDateNum <= compDateNum){
		flag = true;
	}

	return flag;
}


//전체 체크박스 제어 (with xtable)
// 전체 체크박스 제어
function setAllCheckbox(idNm,allCheckNm = null){
	var flag = false;
	var $checkboxs = $(idNm+" td input[type=checkbox]");

	if($checkboxs.length){
		if($checkboxs.length == $(idNm+" td input[type=checkbox]:checked").length){
			flag = true;
		}
	}
	$(idNm + " th [type=checkbox]").prop("checked", flag);
}

// 그리드 내 row 체크박스 제어
function setRowCheckboxEvent(idNm, allCheckNm = null){
	$(idNm + " td input:checkbox").on("change",function(){
		setAllCheckbox(idNm);
	});
}


//파일 사이즈, 형식 체크
//파일 선택시
function excelUploadFileChange(files){
	var fileName = '';
    // 다중파일 등록
    if(files != null){
    	for(var i = 0; i < files.length; i++){
            // 파일 이름
            fileName = files[i].name;
            var fileNameArr = fileName.split("\.");
            // 확장자
            var ext = fileNameArr[fileNameArr.length - 1];
            // 파일 사이즈(단위 :MB)
            var fileSize = Math.round(((files[i].size / 1024) / 1024) * 100) / 100;

            if(ext != 'xlsx'){
            	cpnAlert("<spring:message code='common.alert.message.not.regist.ext'/>",'error');
                return null;
            }

        }
    }

    return fileName;
}